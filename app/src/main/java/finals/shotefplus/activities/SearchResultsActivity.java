package finals.shotefplus.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import finals.shotefplus.DataAccessLayer.FirebaseHandler;
import finals.shotefplus.R;
import finals.shotefplus.adapters.PriceOfferListAdapter;
import finals.shotefplus.adapters.ReceiptListAdapter;
import finals.shotefplus.adapters.SearchResultsListAdapter;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.EnumObjectType;
import finals.shotefplus.objects.Expense;
import finals.shotefplus.objects.LinkedObjects;
import finals.shotefplus.objects.PriceOffer;
import finals.shotefplus.objects.Receipt;
import finals.shotefplus.objects.Work;

public class SearchResultsActivity extends AppCompatActivity {

    private TextView txtQuery;
    ListView lvSearchResults;
    String receiptNum, dueDateWork, dueDateWorkPO, priceOfferIdNum,
            dateReceipt, dateExpense, customerIdNum, workIdNum, expenseWorkIdNum, expenseWorkName;
    int paymentType, paymentMethod;
    private FirebaseAuth firebaseAuth;
    DatabaseReference dbRef;
    private ProgressDialog dialog;
    private List<LinkedObjects> linkedObjectsList;
    private SearchResultsListAdapter adapter;
    int searchType = 0;
    PriceOffer priceOfferToWork;
    static final int REQ_UPD_CUSTOMER = 3;
    static final int REQ_UPD_WORK = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        firebaseAuth = FirebaseAuth.getInstance();
        lvSearchResults = (ListView) findViewById(R.id.lvSearchResults);
        txtQuery = (TextView) findViewById(R.id.txtQuery);

        Intent intent = getIntent();
        searchType = intent.getIntExtra("searchType", 0);
        String strQuery = "מציג תוצאות עבור: ";
       /* if (EnumObjectType.CUSTOMER.getValue() == searchType) {
            customerIdNum = intent.getStringExtra("customerIdNum");
            searchCustomer();
        } else*/

        /* Price Offer */
        if (EnumObjectType.PRICE_OFFER.getValue() == searchType) {
            dueDateWorkPO = intent.getStringExtra("etDueDateWorkPO");
            priceOfferIdNum = intent.getStringExtra("PriceOfferId");
            if (searchPriceOffers()) {
                strQuery += "הצעות מחיר\n לפי " + (dueDateWorkPO != null ? "תאריך: " + dueDateWorkPO + "\n" : "");
                txtQuery.setText(strQuery);
            } else
                Toast.makeText(this, getString(R.string.priceOfferNotFound), Toast.LENGTH_LONG).show();
        }
         /* Work */
        else if (EnumObjectType.WORK.getValue() == searchType) {
            dueDateWork = intent.getStringExtra("dueDateWork");
            workIdNum = intent.getStringExtra("workIdNum");
            if (searchWorks()) {
                strQuery += "עבודות\n לפי " + (dueDateWork != null ? "תאריך: " + dueDateWork + "\n" : "") +
                        (workIdNum != null ? " עבודה נבחרת" : "");
                txtQuery.setText(strQuery);
            } else
                Toast.makeText(this, getString(R.string.workNotFound), Toast.LENGTH_LONG).show();
        }
        /* Receipt */
        else if (EnumObjectType.RECEIPT.getValue() == searchType) {
            receiptNum = intent.getStringExtra("receiptNum");
            dateReceipt = intent.getStringExtra("dateReceipt");
            paymentType = intent.getIntExtra("paymentType", -1);
            paymentMethod = intent.getIntExtra("paymentMethod", -1);
            if (searchReceipts()) {
                strQuery += "קבלות\n לפי " + (receiptNum != null && receiptNum.length() > 0 ? "מס' קבלה: " + receiptNum + " " : "") +
                        (dateReceipt != null && dateReceipt.length() > 0 ? "תאריך: " + dateReceipt + "" : "") +
                        (paymentType > 0 ? "\nצורת תשלום: " + getPaymentType() + " " : "") +
                        (paymentMethod > 0 ? "סוג תשלום: שוטף+" + paymentMethod : "");
                txtQuery.setText(strQuery);
            } else
                Toast.makeText(this, getString(R.string.receiptNotFound), Toast.LENGTH_LONG).show();
        }
        /* Expense */
        else if (EnumObjectType.EXPENSE.getValue() == searchType) {
            expenseWorkIdNum = intent.getStringExtra("expenseWorkIdNum");
            expenseWorkName = intent.getStringExtra("expenseWorkName");
            dateExpense = intent.getStringExtra("dateExpense");
            if (searchExpenses()) {
                strQuery += "הוצאות\n לפי " + (dateExpense != null ? "תאריך: " + dateExpense + "\n" : "") +
                        (expenseWorkIdNum != null && expenseWorkName != null ?
                                " הוצאות עבור עבודה נבחרת" + "\n" + expenseWorkName : "");
                txtQuery.setText(strQuery);
            } else
                Toast.makeText(this, getString(R.string.expenseNotFound), Toast.LENGTH_LONG).show();
        }

        setEvents();

    }


    private String getPaymentType() {
        switch (paymentType) {
            case 1:
                return "מזומן";
            case 2:
                return "אשראי";
            case 3:
                return "המחאה";
            case 4:
                return "העברה";
        }
        return "";
    }


    private void setEvents() {

        lvSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 /* PRICE_OFFER */
                if (EnumObjectType.PRICE_OFFER.getValue() == searchType) {
                    PriceOffer priceOffer = (PriceOffer) lvSearchResults.getAdapter().getItem(position);
                    Intent intent = new Intent(SearchResultsActivity.this, InsertPriceOfferActivity.class);
                    intent.putExtra("PriceOfferId", priceOffer.getIdNum());
                    startActivityForResult(intent, REQ_UPD_CUSTOMER);
                }

                if (EnumObjectType.EXPENSE.getValue() == searchType) {
                    Expense expense = (Expense) lvSearchResults.getAdapter().getItem(position);
                    Intent intent = new Intent(SearchResultsActivity.this, InsertExpenseActivity.class);
                    intent.putExtra("expenseIdNum", expense.getIdNum());
                    startActivityForResult(intent, REQ_UPD_WORK);
                }
            }
        });

        lvSearchResults.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                                       @Override
                                                       public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) {

               /* PRICE_OFFER */
                                                           if (EnumObjectType.PRICE_OFFER.getValue() == searchType) {
                                                               priceOfferToWork = (PriceOffer) lvSearchResults.getAdapter().getItem(position);

                                                               if (!priceOfferToWork.isPriceOfferApproved())//only price offer that didn't approved can get approve
                                                               {
                                                                   Intent intent = new Intent(SearchResultsActivity.this, PriceOfferToWork.class);
                                                                   //  intent.putExtra("PriceOfferId", priceOfferToWork.getIdNum());
                                                                   startActivityForResult(intent, REQ_UPD_WORK);
                                                               } else {
                                                                   Toast.makeText(SearchResultsActivity.this,
                                                                           getString(R.string.priceOfferIsApproved),
                                                                           Toast.LENGTH_LONG).show();
                                                               }
                                                           }
               /* END PRICE_OFFER*/
                                                           return true;
                                                       }
                                                   }
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_UPD_WORK) {
            if (resultCode == Activity.RESULT_OK) {
                setPriceOfferToWork();
                /*dataRefHandling();*/
            }
        }
    }//onActivityResult

    /* *********************************************************************************************
    * Private function
    * ******************************************************************************************* */
    private boolean searchPriceOffers() {
        if (dueDateWorkPO != null && dueDateWorkPO.length() > 0) {
            searchPOByDate();
            return true;
        }
        return false;
    }


    private boolean searchWorks() {
        if (workIdNum != null && workIdNum.length() > 0) {
            searchWorkById();
            return true;
        } else if (dueDateWork != null && dueDateWork.length() > 0) {
            searchWorkByDate();
            return true;
        } else
            return false;
    }

    private boolean searchReceipts() {
        if (receiptNum != null && receiptNum.length() > 0) {
            searchReceiptByNum();
            return true; //dateReceipt
        } else if (dateReceipt != null && dateReceipt.length() > 0) {
            searchReceiptByDate();
            return true;
        } else
            return false;

        //this moment dont check for type and method! TODO..
    }

    private boolean searchExpenses() {
        if (expenseWorkIdNum != null && expenseWorkIdNum.length() > 0) {
            searchExpenseByWorkId();
            return true;
        } else if (dateExpense != null && dateExpense.length() > 0) {
            searchExpensesByDate();
            return true;
        }
        return false;
    }

    private String getDateAsDBtoString(String strDate) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dateFormatted = new Date();
        try {
            dateFormatted = dateFormat.parse(strDate);
        } catch (ParseException pe) {
            // throw new Exception("") ;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateFormatted);

        dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(cal.getTime());
    }

    private void setPriceOfferToWork() {
        priceOfferToWork.setPriceOfferApproved(true);
        //update price offer- approved
        FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid()).
                updatePriceOffer(priceOfferToWork, priceOfferToWork.getIdNum());

        Work work = new Work();
        work.setCustomerIdNum(priceOfferToWork.getCustomerIdNum());

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
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


    /* *********************************************************************************************
   * Firebase function
   * ******************************************************************************************* */

    private void searchExpensesByDate() {
        dialog = ProgressDialog.show(SearchResultsActivity.this,
                "", "טוען נתונים..", true);

        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/Expenses/");

        final String date = getDateAsDBtoString(dateExpense);

        dbRef.orderByChild("date").startAt(date).endAt(date + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot snapshot) {

                        final long[] pendingLoadCount = {snapshot.getChildrenCount()};
                        if (pendingLoadCount[0] == 0) {
                            Toast.makeText(SearchResultsActivity.this, getString(R.string.expenseNotFound), Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }

                        final List<Expense> expenseList = new ArrayList<Expense>();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Expense expense = new Expense();
                            expense = postSnapshot.getValue(Expense.class);
                            expenseList.add(expense);

                            pendingLoadCount[0] = pendingLoadCount[0] - 1;

                            if (pendingLoadCount[0] == 0) {
                                adapter = new SearchResultsListAdapter(SearchResultsActivity.this, getBaseContext(), searchType, null, null, null, null, null, expenseList);
                                lvSearchResults.setAdapter(adapter);
                                dialog.dismiss();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Toast.makeText(getBaseContext(), getString(R.string.errorMsg) + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
    }

    private void searchExpenseByWorkId() {
        dialog = ProgressDialog.show(SearchResultsActivity.this,
                "", getString(R.string.loadMsg), true);

        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseLink) +
                        firebaseAuth.getCurrentUser().getUid() + "/Expenses/");

        dbRef.orderByChild("workIdNum").startAt(expenseWorkIdNum).endAt(expenseWorkIdNum)
                .addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot snapshot) {

                        final long[] pendingLoadCount = {snapshot.getChildrenCount()};
                        if (pendingLoadCount[0] == 0) {
                            Toast.makeText(SearchResultsActivity.this, getString(R.string.expenseNotFound), Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }

                        final List<Expense> expenseList = new ArrayList<Expense>();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Expense expense = new Expense();
                            expense = postSnapshot.getValue(Expense.class);
                            expenseList.add(expense);

                            pendingLoadCount[0] = pendingLoadCount[0] - 1;

                            if (pendingLoadCount[0] == 0) {
                                adapter = new SearchResultsListAdapter(SearchResultsActivity.this, getBaseContext(), searchType, null, null, null, null, null, expenseList);
                                lvSearchResults.setAdapter(adapter);
                                dialog.dismiss();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Toast.makeText(getBaseContext(), getString(R.string.errorMsg) + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });

    }

    private void searchPOByDate() {
        dialog = ProgressDialog.show(SearchResultsActivity.this,
                "", getString(R.string.loadMsg), true);

        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseLink) +
                        firebaseAuth.getCurrentUser().getUid() + "/PriceOffers/");

        final String dueDate = getDateAsDBtoString(dueDateWorkPO);

        dbRef.orderByChild("dueDate").startAt(dueDate).endAt(dueDate + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {

                    public void onDataChange(DataSnapshot snapshot) {

                        final long[] pendingLoadCountPO = {snapshot.getChildrenCount()};
                        if (pendingLoadCountPO[0] == 0) {
                            Toast.makeText(SearchResultsActivity.this, getString(R.string.priceOfferNotFound), Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }

                        final List<PriceOffer> priceOfferList = new ArrayList<PriceOffer>();
                        final List<Customer> customerOfPOList = new ArrayList<Customer>();

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            PriceOffer priceOffer = new PriceOffer();
                            priceOffer = postSnapshot.getValue(PriceOffer.class);
                            priceOfferList.add(priceOffer);


                            //get Customer of current priceOffer
                            final DatabaseReference currentDdbRef = FirebaseDatabase.getInstance()
                                    .getReferenceFromUrl(getString(R.string.firebaseLink) +
                                            firebaseAuth.getCurrentUser().getUid() + "/Customers/"
                                            + priceOffer.getCustomerIdNum());

                            currentDdbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Customer customer = new Customer();
                                    customer = dataSnapshot.getValue(Customer.class);
                                    if (customer != null) customerOfPOList.add(customer);
                                    // we loaded a child, check if we're done
                                    pendingLoadCountPO[0] = pendingLoadCountPO[0] - 1;

                                    if (pendingLoadCountPO[0] == 0) {
                                        adapter = new SearchResultsListAdapter(SearchResultsActivity.this, getBaseContext(), searchType, priceOfferList, customerOfPOList, null, null, null, null);
                                        lvSearchResults.setAdapter(adapter);
                                        dialog.dismiss();
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError firebaseError) {
                                    Toast.makeText(getBaseContext(), getString(R.string.errorMsg) + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            });
                            //END of get Customer of current priceOffer


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Toast.makeText(getBaseContext(), getString(R.string.errorMsg) + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
    }

    private void searchWorkByDate() {
        dialog = ProgressDialog.show(SearchResultsActivity.this,
                "", "טוען נתונים..", true);

        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/Works/");

        final String dueDate = getDateAsDBtoString(dueDateWork);

        dbRef.orderByChild("dueDate").startAt(dueDate).endAt(dueDate + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {

                    public void onDataChange(DataSnapshot snapshot) {

                        final long[] pendingLoadCountWork = {snapshot.getChildrenCount()};
                        if (pendingLoadCountWork[0] == 0) {
                            Toast.makeText(SearchResultsActivity.this, getString(R.string.workNotFound), Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }

                        final List<Work> worklList = new ArrayList<Work>();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Work work = new Work();
                            work = postSnapshot.getValue(Work.class);
                            worklList.add(work);

                            pendingLoadCountWork[0] = pendingLoadCountWork[0] - 1;

                            if (pendingLoadCountWork[0] == 0) {
                                adapter = new SearchResultsListAdapter(SearchResultsActivity.this, getBaseContext(), searchType, null, null, worklList, null, null, null);
                                lvSearchResults.setAdapter(adapter);
                                dialog.dismiss();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Toast.makeText(getBaseContext(), getString(R.string.errorMsg) + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
    }

    private void searchWorkById() {
        dialog = ProgressDialog.show(SearchResultsActivity.this,
                "", "טוען נתונים..", true);

        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/Works/" + workIdNum);

        dbRef/*.orderByChild("idNum").startAt(workIdNum).endAt(workIdNum)*/
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    public void onDataChange(DataSnapshot snapshot) {

                        final List<Work> worklList = new ArrayList<Work>();
                        Work work = new Work();
                        work = snapshot.getValue(Work.class);
                        worklList.add(work);

                        adapter = new SearchResultsListAdapter(SearchResultsActivity.this, getBaseContext(), searchType, null, null, worklList, null, null, null);
                        lvSearchResults.setAdapter(adapter);
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Toast.makeText(getBaseContext(), getString(R.string.errorMsg) + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
    }

    private void searchReceiptByNum() {
        dialog = ProgressDialog.show(SearchResultsActivity.this,
                "", "טוען נתונים..", true);

        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/Receipts/");

        dbRef.orderByChild("receiptNum")/*.startAt(receiptNum).endAt(receiptNum)*/
                .equalTo(receiptNum)
                .addValueEventListener(new ValueEventListener() {

                    public void onDataChange(DataSnapshot snapshot) {
                        final List<Receipt> receiptList = new ArrayList<Receipt>();
                        final List<Customer> customerList = new ArrayList<Customer>();
                        final long[] pendingLoadCount = {snapshot.getChildrenCount()};
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            try {
                                Receipt receipt = new Receipt();
                                receipt = postSnapshot.getValue(Receipt.class);
                                receiptList.add(receipt);

                                //get Customer of current Receipts
                                final DatabaseReference currentDdbRef = FirebaseDatabase.getInstance()
                                        .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                                                firebaseAuth.getCurrentUser().getUid() + "/Customers/" + receipt.getCustomerIdNum());

                                currentDdbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Customer customer = new Customer();
                                        customer = dataSnapshot.getValue(Customer.class);
                                        if (customer != null) customerList.add(customer);

                                        pendingLoadCount[0] = pendingLoadCount[0] - 1;
                                        if (pendingLoadCount[0] == 0) {
                                            adapter = new SearchResultsListAdapter(SearchResultsActivity.this, getBaseContext(), searchType, null, null, null, receiptList, customerList, null);
                                            lvSearchResults.setAdapter(adapter);
                                            dialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError firebaseError) {
                                        Toast.makeText(getBaseContext(), getString(R.string.errorMsg) + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                });
                                //END of get Customer of current Receipts
                            } catch (Exception ex) {
                                Toast.makeText(getBaseContext(), getString(R.string.errorMsg) + ex.toString(), Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Toast.makeText(getBaseContext(), getString(R.string.errorMsg) + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
    }

    private void searchReceiptByDate() {
        dialog = ProgressDialog.show(SearchResultsActivity.this,
                "", "טוען נתונים..", true);

        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/Receipts/");

        final String date = getDateAsDBtoString(dateReceipt);

        dbRef.orderByChild("dateReceipt").startAt(date).endAt(date + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {

                    public void onDataChange(DataSnapshot snapshot) {

                        final long[] pendingLoadCount = {snapshot.getChildrenCount()};
                        if (pendingLoadCount[0] == 0) {
                            Toast.makeText(SearchResultsActivity.this, getString(R.string.receiptNotFound), Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }

                        final List<Receipt> receiptList = new ArrayList<Receipt>();
                        final List<Customer> customerList = new ArrayList<Customer>();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Receipt receipt = new Receipt();
                            receipt = postSnapshot.getValue(Receipt.class);
                            receiptList.add(receipt);

                            //get Customer of current Receipts
                            final DatabaseReference currentDdbRef = FirebaseDatabase.getInstance()
                                    .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                                            firebaseAuth.getCurrentUser().getUid() + "/Customers/" + receipt.getCustomerIdNum());

                            currentDdbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Customer customer = new Customer();
                                    customer = dataSnapshot.getValue(Customer.class);
                                    if (customer != null) customerList.add(customer);

                                    pendingLoadCount[0] = pendingLoadCount[0] - 1;

                                    if (pendingLoadCount[0] == 0) {
                                        adapter = new SearchResultsListAdapter(SearchResultsActivity.this, getBaseContext(), searchType, null, null, null, receiptList, customerList, null);
                                        lvSearchResults.setAdapter(adapter);
                                        dialog.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError firebaseError) {
                                    Toast.makeText(getBaseContext(), getString(R.string.errorMsg) + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            }); //END of get Customer of current Receipts
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Toast.makeText(getBaseContext(),getString(R.string.errorMsg) + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
    }
    /* ------------------------------------------------------------------------------------------- *//*
    private void searchCustomer() {

        dialog = ProgressDialog.show(SearchResultsActivity.this,
                "", "טוען נתונים..", true);

        linkedObjectsList = new ArrayList<LinkedObjects>();

        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/Customers/");

        dbRef.orderByChild("idNum").startAt(customerIdNum).endAt(customerIdNum)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    public void onDataChange(DataSnapshot snapshot) {
                                                        final LinkedObjects currentLinkedObjects = new LinkedObjects();
                                                        final List<Customer> customerList = new ArrayList<Customer>();
                                                        final long[] pendingLoadCountCustomer = {snapshot.getChildrenCount()};

                                                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                                            try {
                                                                Customer customer = new Customer();
                                                                customer = postSnapshot.getValue(Customer.class);
                                                                customerList.add(customer);

                                                                currentLinkedObjects.setCustomer(customer);

                                                                pendingLoadCountCustomer[0] = pendingLoadCountCustomer[0] - 1;
                                                                if (pendingLoadCountCustomer[0] == 0) {
                                                                    adapter = new SearchResultsListAdapter(SearchResultsActivity.this, getBaseContext(), searchType, customerList, null);
                                                                    lvSearchResults.setAdapter(adapter);
                                                                    dialog.dismiss();
                                                                }

                                                                //find all PriceOffers of customer:
                        *//* final DatabaseReference currentDdbRef = FirebaseDatabase.getInstance()
                                 .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                                         firebaseAuth.getCurrentUser().getUid() + "/PriceOffers/");

                         currentDdbRef.orderByChild("customerIdNum").startAt(customerIdNum).endAt(customerIdNum)
                                 .addValueEventListener(new ValueEventListener() {
                                     public void onDataChange(DataSnapshot snapshot) {

                                         final long[] pendingLoadCountPO = {snapshot.getChildrenCount()};

                                         for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                             PriceOffer priceOffer = new PriceOffer();
                                             priceOffer = snapshot.getValue(PriceOffer.class);

                                             pendingLoadCountPO[0] = pendingLoadCountPO[0] - 1;

                                             currentLinkedObjects.addToPriceOfferList(priceOffer);

                                             //find all Works of priceOffer:
                                             final DatabaseReference currentDdbRefWorks = FirebaseDatabase.getInstance()
                                                     .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                                                             firebaseAuth.getCurrentUser().getUid() + "/Works/");

                                             currentDdbRefWorks.orderByChild("customerIdNum").startAt(customerIdNum).endAt(customerIdNum)
                                                     .addValueEventListener(new ValueEventListener() {
                                                         public void onDataChange(DataSnapshot snapshot) {

                                                             final long[] pendingLoadCountWork = {snapshot.getChildrenCount()};

                                                             for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                                                 Work work = new Work();
                                                                 work = snapshot.getValue(Work.class);

                                                                 pendingLoadCountWork[0] = pendingLoadCountWork[0] - 1;

                                                                 currentLinkedObjects.addToWorkList(work);

                                                                 if (pendingLoadCountCustomer[0] == 0 && pendingLoadCountPO[0] == 0
                                                                         && pendingLoadCountWork[0] == 0 ) {
                                                                     adapter = new SearchResultsListAdapter(SearchResultsActivity.this, searchType,customerList);
                                                                     lvSearchResults.setAdapter(adapter);
                                                                     dialog.dismiss();
                                                                 }

                                                                *//**//* //1. find all Receipts of Work:
                                                                 final DatabaseReference currentDdbRefReceipt = FirebaseDatabase.getInstance()
                                                                         .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                                                                                 firebaseAuth.getCurrentUser().getUid() + "/Receipts/");

                                                                 currentDdbRefReceipt.orderByChild("customerIdNum").startAt(customerIdNum).endAt(customerIdNum)
                                                                         .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                             public void onDataChange(DataSnapshot snapshot) {
                                                                                 final long[] pendingLoadCountReceipt = {snapshot.getChildrenCount()};
                                                                                 Receipt receipt = new Receipt();
                                                                                 receipt = snapshot.getValue(Receipt.class);
                                                                                 currentLinkedObjects.setReceipt(receipt);

                                                                                 pendingLoadCountReceipt[0] = pendingLoadCountReceipt[0] - 1;
                                                                             }

                                                                             @Override
                                                                             public void onCancelled
                                                                                     (DatabaseError firebaseError) {
                                                                                 Toast.makeText(getBaseContext(), getString(R.string.errorMsg) + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                                                                 dialog.dismiss();
                                                                             }
                                                                         });


                                                                 //2. find all Expenses of Work:
                                                                 final DatabaseReference currentDdbRefExpenses = FirebaseDatabase.getInstance()
                                                                         .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                                                                                 firebaseAuth.getCurrentUser().getUid() + "/Expenses/");

                                                                 currentDdbRefExpenses.orderByChild("workIdNum").startAt(work.getIdNum()).endAt(work.getIdNum())
                                                                         .addValueEventListener(new ValueEventListener() {
                                                                             public void onDataChange(DataSnapshot snapshot) {

                                                                                 final long[] pendingLoadCountEx = {snapshot.getChildrenCount()};

                                                                                 for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                                                                     Expense expense = new Expense();
                                                                                     expense = snapshot.getValue(Expense.class);

                                                                                     currentLinkedObjects.addToExpenseList(expense);

                                                                                     pendingLoadCountEx[0] = pendingLoadCountEx[0] - 1;

                                                                                     if (pendingLoadCountCustomer[0] == 0 && pendingLoadCountPO[0] == 0
                                                                                             && pendingLoadCountWork[0] == 0 && pendingLoadCountEx[0] == 0) {
                                                                                         adapter = new SearchResultsListAdapter(SearchResultsActivity.this, currentLinkedObjects);
                                                                                         lvSearchResults.setAdapter(adapter);
                                                                                         dialog.dismiss();
                                                                                     }
                                                                                 }
                                                                             }

                                                                             @Override
                                                                             public void onCancelled(DatabaseError firebaseError) {
                                                                                 Toast.makeText(getBaseContext(), getString(R.string.errorMsg) + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                                                                 dialog.dismiss();
                                                                             }
                                                                         });*//**//*

                                                             }
                                                         }

                                                         @Override
                                                         public void onCancelled(DatabaseError firebaseError) {
                                                             Toast.makeText(getBaseContext(), getString(R.string.errorMsg) + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                                             dialog.dismiss();
                                                         }
                                                     });


                                         }
                                     }

                                     @Override
                                     public void onCancelled(DatabaseError firebaseError) {
                                         Toast.makeText(getBaseContext(), getString(R.string.errorMsg) + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                         dialog.dismiss();
                                     }
                                 });*//*

                                                            } catch (Exception ex) {
                                                                Toast.makeText(getBaseContext(), getString(R.string.errorMsg) + ex.toString(), Toast.LENGTH_LONG).show();
                                                                dialog.dismiss();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError firebaseError) {
                                                        Toast.makeText(getBaseContext(), getString(R.string.errorMsg) + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                                        dialog.dismiss();
                                                    }
                                                }

                );
    }*/

    /* ------------------------------------------------------------------------------------------- */
    private void SearchForQuery() {
        // firebase

    }


}
