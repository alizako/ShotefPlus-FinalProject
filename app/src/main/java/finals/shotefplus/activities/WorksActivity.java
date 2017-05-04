package finals.shotefplus.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import finals.shotefplus.R;
import finals.shotefplus.adapters.WorkListAdapter;
import finals.shotefplus.objects.Work;

public class WorksActivity extends AppCompatActivity {
    ListView lvWorks;
    private List<Work> workList;
    private WorkListAdapter adapter;
    View filter, barMonth;
    private ProgressDialog dialog;
    private TextView txtNext, txtPrev, txtDate;
    private FirebaseAuth firebaseAuth;
    DatabaseReference dbRef;
    Date date;
    static final int RESULT_CLEAN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_works);

        firebaseAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/Works/");
        barMonth = findViewById(R.id.barMonth);
        filter = findViewById(R.id.barFilter);
        lvWorks = (ListView) findViewById(R.id.listViewWorks);
        txtNext = (TextView) barMonth.findViewById(R.id.txtNext);
        txtPrev = (TextView) barMonth.findViewById(R.id.txtPrev);
        txtDate = (TextView) barMonth.findViewById(R.id.txtMonth);

        date = new Date();
        setCurrentDateBarMonth();
        initFilter();

        try {
            dialog = ProgressDialog.show(WorksActivity.this,
                    "",
                    "טוען נתונים..",
                    true);

            dataRefHandling();

        } catch (Exception e) {
            dialog.dismiss();
            Toast.makeText(WorksActivity.this,
                    "" + e.toString(),
                    Toast.LENGTH_LONG).show();
        }

        setEvents();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TextView txtDetails = (TextView) filter.findViewById(R.id.txtDetails);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                boolean isWorkDone = data.getBooleanExtra("cbWorkDone", false);
                boolean isWorkCancelled = data.getBooleanExtra("cbWorkCancelled", false);

                // get from DB
                //set Filter text:

                txtDetails.setText(
                        (isWorkDone ? "עבודה בוצעה | " : "") +
                                (isWorkCancelled ? "עבודה בוטלה | " : "")
                );
                initListWorks(isWorkDone, isWorkCancelled);

            }
            if (resultCode == RESULT_CLEAN) {
                txtDetails.setText("ללא פילטר");
                dataRefHandling();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private void initFilter() {
        filter = findViewById(R.id.barFilter);
        ImageButton imgBtnFilter = (ImageButton) filter.findViewById(R.id.imgbtnFilter);
        imgBtnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorksActivity.this, FilterWorkActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void setCurrentDateBarMonth() {
        DateFormat dateFormat = new SimpleDateFormat("MMM | yyyy");
        txtDate.setText(dateFormat.format(date));
    }


    private void initListWorks(boolean isWorkDone, boolean isWorkCancelled) {

        List<Work> listTmp = new ArrayList<Work>();
        for (int i = 0; i < workList.size(); i++) {

            Work work = workList.get(i);
            if (work.isWorkDone() == isWorkDone && work.isWorkCanceled() == isWorkCancelled)
                listTmp.add(work);
        }
        adapter = new WorkListAdapter(WorksActivity.this, listTmp, getBaseContext());
        lvWorks.setAdapter(adapter);
        dialog.dismiss();
    }


    /**********************************************************************************
     * Events
     **********************************************************************************/

    private void setEvents() {

       /* lvWorks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Work work = (Work) lvWorks.getAdapter().getItem(position);
              *//*  Intent intent = new Intent(WorksActivity.this, InsertPriceOfferActivity.class);
                intent.putExtra("WorkId", work.getIdNum());
                startActivityForResult(intent,REQ_UPD_WORK);*//*
            }
        });

        lvWorks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id)
            {
                *//*w = (PriceOffer) lvPriceOffer.getAdapter().getItem(position);

                if (!priceOfferToWork.isPriceOfferApproved())//only price offer that didn't approved can get approve
                {
                    Intent intent = new Intent(PriceOffersActivity.this, PriceOfferToWork.class);
                    //  intent.putExtra("PriceOfferId", priceOfferToWork.getIdNum());
                    startActivityForResult(intent,REQ_UPD_WORK);
                }*//*

                return true;
            }
        });*/

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = new Date();
                setCurrentDateBarMonth();
                dataRefHandling();
            }
        });
        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.MONTH, 1);
                date = c.getTime();
                setCurrentDateBarMonth();
                dataRefHandling();
            }
        });
        txtPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.MONTH, -1);
                date = c.getTime();
                setCurrentDateBarMonth();
                dataRefHandling();
            }
        });
    }

    /**********************************************************************************
     * FireBase
     **********************************************************************************/
    private void dataRefHandling() {

        DateFormat dateFormat = new SimpleDateFormat("yyyyMM");
        final String dueDate = dateFormat.format(date);
        dbRef.orderByChild("priceOffer/dueDate").startAt(dueDate).endAt(dueDate + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                workList = new ArrayList<Work>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    try {
                        Work work = new Work();
                        work = postSnapshot.getValue(Work.class);
                        workList.add(work);
                    } catch (Exception ex) {
                        Toast.makeText(getBaseContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
                    }
                }
                adapter = new WorkListAdapter(WorksActivity.this, workList, getBaseContext());
                lvWorks.setAdapter(adapter);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(getBaseContext(), "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }

}
