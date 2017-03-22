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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import finals.shotefplus.R;
import finals.shotefplus.adapters.PriceOfferListAdapter;
import finals.shotefplus.objects.PriceOffer;

public class PriceOffersActivity extends AppCompatActivity {

    ImageButton btnAdd;
    ListView lvPriceOffer;
    private List<PriceOffer> priceOfferList;
    private PriceOfferListAdapter adapter;
    View filter, barMonth;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog dialog;
    private TextView txtNext, txtPrev, txtDate;
    Date date;
    DatabaseReference dbRef;
    static final int RESULT_CLEAN = 2;
    static final int REQ_UPD_CUSTOMER = 3;
    static final int REQ_ADD_CUSTOMER = 2;
    static final int REQ_FILTER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_offers);

        //init vars:
        firebaseAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/PriceOffers/");
        barMonth = findViewById(R.id.barMonth);
        filter = findViewById(R.id.barFilter);
        btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        lvPriceOffer = (ListView) findViewById(R.id.listViewPriceOffers);
        txtNext = (TextView) barMonth.findViewById(R.id.txtNext);
        txtPrev = (TextView) barMonth.findViewById(R.id.txtPrev);
        txtDate = (TextView) barMonth.findViewById(R.id.txtMonth);

        date = new Date();
        setCurrentDateBarMonth();
        initFilter();

        try {
            dialog = ProgressDialog.show(PriceOffersActivity.this,
                    "",
                    "טוען נתונים..",
                    true);

            dataRefHandling();

        } catch (Exception e) {
            dialog.dismiss();
            Toast.makeText(PriceOffersActivity.this,
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
                boolean isSent = data.getBooleanExtra("cbSent", false);
                boolean isApproved = data.getBooleanExtra("rbApproved", false);
                boolean isNotApproved = data.getBooleanExtra("rbNotApproved", false);

                //set Filter text:
                String filterTxt = (isSent ? "נשלח ללקוח |" : "") +
                        (isApproved ? "אושר לביצוע " : "") +
                        (isNotApproved ? "לא אושר לביצוע" : "");
                if (filterTxt.substring(filterTxt.length() - 1).equals("|")) // last character is |
                    filterTxt = filterTxt.substring(0, filterTxt.length() - 1);

                txtDetails.setText(filterTxt);

                initListPriceOffers(isSent, isApproved, isNotApproved);
            }
            if (resultCode == RESULT_CLEAN) {
                txtDetails.setText("ללא פילטר");
                dataRefHandling();
            }
            /*if (resultCode == Activity.RESULT_CANCELED) {

                //Write your code if there's no result
            }*/
        }

        if (requestCode == REQ_UPD_CUSTOMER) {
            dataRefHandling();
        }
    }//onActivityResult


    private void initFilter() {
        ImageButton imgBtnFilter = (ImageButton) filter.findViewById(R.id.imgbtnFilter);
        imgBtnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PriceOffersActivity.this, FilterPriceOfferActivity.class);
                startActivityForResult(intent, REQ_FILTER);
            }
        });
    }

    private void setCurrentDateBarMonth() {
        DateFormat dateFormat = new SimpleDateFormat("MMM | yyyy");
        txtDate.setText(dateFormat.format(date));
    }

    private void initListPriceOffers(boolean isSent, boolean isApproved, boolean isNotApproved) {
        boolean tmpApproved = isNotApproved ? false : isApproved ? true : false;
        List<PriceOffer> listTmp = new ArrayList<PriceOffer>();
        for (int i = 0; i < priceOfferList.size(); i++) {
            PriceOffer priceOffer = priceOfferList.get(i);

            if (priceOffer.isPriceOfferSent() == isSent && priceOffer.isPriceOfferApproved() == tmpApproved)
                listTmp.add(priceOffer);
        }
        adapter = new PriceOfferListAdapter(PriceOffersActivity.this, listTmp);
        lvPriceOffer.setAdapter(adapter);
        dialog.dismiss();
    }

    /**********************************************************************************
     * Events
     **********************************************************************************/

    private void setEvents() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PriceOffersActivity.this, InsertPriceOfferActivity.class));
            }
        });

        lvPriceOffer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PriceOffer priceOffer = (PriceOffer) lvPriceOffer.getAdapter().getItem(position);

                Intent intent = new Intent(PriceOffersActivity.this, InsertPriceOfferActivity.class);
                intent.putExtra("PriceOfferId", priceOffer.getIdNum());
                startActivityForResult(intent,REQ_UPD_CUSTOMER);
                //????
            }
        });

        lvPriceOffer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id)
            {
               //TODO- need to able the user to approve price offer- in order to add it to works list!!
                return true;
            }
        });

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
        dbRef.orderByChild("dueDate").startAt(dueDate).endAt(dueDate + "\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                priceOfferList = new ArrayList<PriceOffer>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    try {
                        PriceOffer priceOffer = new PriceOffer();
                        priceOffer = postSnapshot.getValue(PriceOffer.class);
                        priceOfferList.add(priceOffer);
                    } catch (Exception ex) {
                        Toast.makeText(getBaseContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
                    }
                }
                adapter = new PriceOfferListAdapter(PriceOffersActivity.this, priceOfferList);
                lvPriceOffer.setAdapter(adapter);
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
