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
    static final int REQ_FILTER = 1;
    static final int RESULT_CLEAN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_works);

        //set firebase link to works
        firebaseAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(
                        getString(R.string.firebaseLink) +
                        firebaseAuth.getCurrentUser().getUid() +
                        getString(R.string.worksLink));

        //set fields
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
            //get records of firebase
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

        if (requestCode == REQ_FILTER) {
            if (resultCode == Activity.RESULT_OK) {
                boolean isWorkDone = data.getBooleanExtra(getString(R.string.cbWorkDone), false);
                boolean isWorkCancelled = data.getBooleanExtra(getString(R.string.cbWorkCancelled), false);

                // get from DB
                //set Filter text:

                txtDetails.setText(
                        (isWorkDone ? getString(R.string.workDone)+ " | "  : "") +
                                (isWorkCancelled ? getString(R.string.workCancelled)+" | " : "")
                );
                initListWorks(isWorkDone, isWorkCancelled);
            }
            if (resultCode == RESULT_CLEAN) {
                txtDetails.setText(R.string.noFilter);
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
                startActivityForResult(intent, REQ_FILTER);
            }
        });
    }

    private void setCurrentDateBarMonth() {
        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormatMonthBar));
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

        //set events of clicking prev,next,current date
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

        dialog = ProgressDialog.show(WorksActivity.this,
                "", getString(R.string.loadMsg), true);

        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormat));
        final String dueDate = dateFormat.format(date);


        //find records with the right date (chosen in bar-month)
        dbRef.orderByChild(getString(R.string.dueDateDB))
                .startAt(dueDate).endAt(dueDate + "\uf8ff")
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
                                Toast.makeText(getBaseContext(),
                                        getString(R.string.errorMsg)+ ex.toString(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                        //generate list to view through adapter
                        adapter = new WorkListAdapter(WorksActivity.this, workList, getBaseContext());
                        lvWorks.setAdapter(adapter);
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Toast.makeText(getBaseContext(),
                                getString(R.string.errorMsg) + firebaseError.getMessage(),
                                Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
    }

}
