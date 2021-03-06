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

import finals.shotefplus.DataAccessLayer.FirebaseHandler;
import finals.shotefplus.R;
import finals.shotefplus.adapters.PriceOfferListAdapter;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.PriceOffer;
import finals.shotefplus.objects.Work;


public class PriceOffersActivity extends AppCompatActivity {

    ImageButton btnAdd;
    ListView lvPriceOffer;
    PriceOffer priceOfferToWork;
    private List<PriceOffer> priceOfferList;
    private List<Customer> customerList;
    private PriceOfferListAdapter adapter;
    View filter, barMonth;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog dialog;
    private TextView txtNext, txtPrev, txtDate;
    Date date;
    DatabaseReference dbRef;
    static final int RESULT_CLEAN = 2;
    static final int REQ_UPD_CUSTOMER = 3;
    static final int REQ_UPD_WORK = 4;
    static final int REQ_ADD_CUSTOMER = 2;
    static final int REQ_FILTER = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_offers);

        //init vars:
        firebaseAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseLink) +
                firebaseAuth.getCurrentUser().getUid() +
                getString(R.string.priceOfferLink));

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
                boolean isSent = data.getBooleanExtra(getString(R.string.cbSent), false);
                boolean isApproved = data.getBooleanExtra(getString(R.string.rbApproved), false);
                boolean isNotApproved = data.getBooleanExtra(getString(R.string.rbNotApproved), false);

                //set Filter text:
                String filterTxt = (isSent ? getString(R.string.priceOfferSent)+ " |" : "") +
                        (isApproved ? getString(R.string.priceOfferApproved)+" " : "") +
                        (isNotApproved ? getString(R.string.priceOfferNotApproved)+" " : "");
                if (filterTxt.substring(filterTxt.length() - 1).equals("|")) // last character is |
                    filterTxt = filterTxt.substring(0, filterTxt.length() - 1);

                txtDetails.setText(filterTxt);

                initListPriceOffers(isSent, isApproved, isNotApproved);
            }
            if (resultCode == RESULT_CLEAN) {
                txtDetails.setText(getString(R.string.noFilter));
                dataRefHandling();
            }
            /*if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }*/
        }

        if (requestCode == REQ_UPD_CUSTOMER) {
            dataRefHandling();
        }
        if (requestCode == REQ_UPD_WORK) {
            if (resultCode == Activity.RESULT_OK) {
                setPriceOfferToWork();
                dataRefHandling();
            }
        }
    }//onActivityResult


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

        // click: edit price offer
        lvPriceOffer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PriceOffer priceOffer = (PriceOffer) lvPriceOffer.getAdapter().getItem(position);
                Intent intent = new Intent(PriceOffersActivity.this, InsertPriceOfferActivity.class);
                intent.putExtra(getString(R.string.PriceOfferId), priceOffer.getIdNum());
                startActivityForResult(intent, REQ_UPD_CUSTOMER);
            }
        });

        //long click on item: approve price offer in order to create a new work out of it
        lvPriceOffer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) {
                priceOfferToWork = (PriceOffer) lvPriceOffer.getAdapter().getItem(position);

                if (!priceOfferToWork.isPriceOfferApproved())//only price offer that didn't approved can get approve
                {
                    Intent intent = new Intent(PriceOffersActivity.this, PriceOfferToWork.class);
                    //  intent.putExtra("PriceOfferId", priceOfferToWork.getIdNum());
                    startActivityForResult(intent, REQ_UPD_WORK);
                }
                else{
                    Toast.makeText(PriceOffersActivity.this,
                            getString(R.string.priceOfferIsApproved),
                            Toast.LENGTH_LONG).show();
                }

                return true;
            }
        });

        // bar month- next,prev,current- set view according to it
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
    // find all price offers and its customers
    // show in list through adapter
    private void dataRefHandling() {

        dialog = ProgressDialog.show(PriceOffersActivity.this,
                "",
                getString(R.string.loadMsg),
                true);

        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormat));
        final String dueDate = dateFormat.format(date);
        dbRef.orderByChild(getString(R.string.dueDateDB))
                .startAt(dueDate)
                .endAt(dueDate + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot snapshot) {
                       priceOfferList = new ArrayList<PriceOffer>();
                       customerList = new ArrayList<Customer>();
                       final long[] pendingLoadCount = {snapshot.getChildrenCount()};

                       for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                           try {
                               PriceOffer priceOffer = new PriceOffer();
                               priceOffer = postSnapshot.getValue(PriceOffer.class);
                               priceOfferList.add(priceOffer);

                               //get Customer of current priceOffer
                               final DatabaseReference currentDdbRef = FirebaseDatabase.getInstance()
                                       .getReferenceFromUrl(getString(R.string.firebaseLink) +
                                               firebaseAuth.getCurrentUser().getUid() +
                                               getString(R.string.customersLink)+
                                               priceOffer.getCustomerIdNum());

                               currentDdbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                   public void onDataChange(DataSnapshot dataSnapshot) {
                                        Customer customer= new Customer();
                                        customer = dataSnapshot.getValue(Customer.class);
                                        if (customer!=null) customerList.add(customer);
                                        // we loaded a child, check if we're done
                                        pendingLoadCount[0] = pendingLoadCount[0] - 1;
                                        if (pendingLoadCount[0] == 0) {
                                            adapter = new PriceOfferListAdapter(PriceOffersActivity.this, priceOfferList, customerList);
                                            lvPriceOffer.setAdapter(adapter);
                                            dialog.dismiss();
                                        }
                                   }
                                   @Override
                                   public void onCancelled(DatabaseError firebaseError) {
                                       Toast.makeText(getBaseContext(),
                                               getString(R.string.errorMsg) + firebaseError.getMessage(),
                                               Toast.LENGTH_LONG).show();
                                       dialog.dismiss();
                                   }
                               });
                               //END of get Customer of current priceOffer


                           } catch (Exception ex) {
                               Toast.makeText(getBaseContext(),
                                       getString(R.string.errorMsg) + ex.toString(),
                                       Toast.LENGTH_LONG).show();
                           }
                       }
                       if(pendingLoadCount[0] == 0) {
                           adapter = new PriceOfferListAdapter(PriceOffersActivity.this, priceOfferList, customerList);
                           lvPriceOffer.setAdapter(adapter);
                           dialog.dismiss();
                       }
                   }

                   @Override
                   public void onCancelled(DatabaseError firebaseError) {
                       Toast.makeText(getBaseContext(),
                               getString(R.string.errorMsg) + firebaseError.getMessage(),
                               Toast.LENGTH_LONG).show();
                       dialog.dismiss();
                   }
               }
        );
    }

    /**********************************************************************************
     * private functions
     **********************************************************************************/
    private void setPriceOfferToWork() {
        priceOfferToWork.setPriceOfferApproved(true);
        //update price offer- approved
        FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid()).
                updatePriceOffer(priceOfferToWork, priceOfferToWork.getIdNum());

        Work work = new Work();
        work.setCustomerIdNum(priceOfferToWork.getCustomerIdNum());

        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormatFull));
        Date date = new Date();
        work.setDateInsertion(dateFormat.format(date));

        work.setDueDate(priceOfferToWork.getDueDate());
        work.setLocation(priceOfferToWork.getLocation());
        work.setPriceOfferSent(priceOfferToWork.isPriceOfferSent());
        work.setPriceOfferApproved(priceOfferToWork.isPriceOfferApproved());
        work.setQuantity(priceOfferToWork.getQuantity());
        work.setSumPayment(priceOfferToWork.getSumPayment());
        work.setSumPaymentMaam(priceOfferToWork.isSumPaymentMaam());
        work.setWorkDetails(priceOfferToWork.getWorkDetails());
        work.setPriceOfferIdNum(priceOfferToWork.getIdNum());
        String currentKey = FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid()).insertWork(work);
        //Toast.makeText(PriceOffersActivity.getContext(), "הצעת מחיר התווספה", Toast.LENGTH_LONG).show();
    }

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
        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormatMonthBar));
        txtDate.setText(dateFormat.format(date));
    }

    //TODO: not working well- need to simplify!
    private void initListPriceOffers(boolean isSent, boolean isApproved, boolean isNotApproved) {

        List<PriceOffer> listTmp = new ArrayList<>();
        List<Customer> listCTmp = new ArrayList<>();
        for (int i = 0; i < priceOfferList.size(); i++) {
            PriceOffer priceOffer = priceOfferList.get(i);
            if (isNotApproved == false && isApproved == false) { //both not selected
                if (priceOffer.isPriceOfferSent() == isSent)
                    listTmp.add(priceOffer);
            } else {//different selection
                boolean tmpApproved = isNotApproved ? false : isApproved ? true : false;
                if ((priceOffer.isPriceOfferSent() == isSent && priceOffer.isPriceOfferApproved() == tmpApproved)
                        || (priceOffer.isPriceOfferApproved() == tmpApproved)) {
                    listTmp.add(priceOffer);

                    int j = 0;
                    while (j < customerList.size() && !customerList.get(j).getIdNum().equals(priceOffer.getCustomerIdNum())) {
                        j++;
                    }
                    if (j < customerList.size()) {
                        listCTmp.add(customerList.get(j));
                    }
                }
            }
        }
        adapter = new PriceOfferListAdapter(PriceOffersActivity.this, listTmp, listCTmp);
        lvPriceOffer.setAdapter(adapter);
        dialog.dismiss();
    }

}
