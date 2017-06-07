package finals.shotefplus.activities;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import finals.shotefplus.R;
import finals.shotefplus.adapters.ReceiptListAdapter;
import finals.shotefplus.adapters.SearchResultsListAdapter;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.Expense;
import finals.shotefplus.objects.LinkedObjects;
import finals.shotefplus.objects.PriceOffer;
import finals.shotefplus.objects.Receipt;
import finals.shotefplus.objects.Work;

public class SearchResultsActivity extends AppCompatActivity {

    private TextView txtQuery;
    ListView lvSearchResults, lvExpenses;
    String receiptNum, dueDateWork, dueDateWorkPO, priceOfferIdNum, dateReceipt, customerIdNum, workIdNum;
    int paymentType, paymentMethod;
    private FirebaseAuth firebaseAuth;
    DatabaseReference dbRef;
    private ProgressDialog dialog;
    private List<LinkedObjects> linkedObjectsList;
    private SearchResultsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        firebaseAuth = FirebaseAuth.getInstance();
        lvSearchResults = (ListView) findViewById(R.id.lvSearchResults);

        Intent intent = getIntent();
        int searchType = intent.getIntExtra("searchType", 0);

        switch (searchType) {
            case 1:
                customerIdNum = intent.getStringExtra("customerIdNum");
                searchCustomer();
                break;
            case 2:
                dueDateWork = intent.getStringExtra("dueDateWork");
                workIdNum = intent.getStringExtra("workIdNum");
                break;
            case 3:
                dueDateWorkPO = intent.getStringExtra("dueDateWork");
                priceOfferIdNum = intent.getStringExtra("workIdNum");
                break;
            case 4:
                receiptNum = intent.getStringExtra("receiptNum");
                dateReceipt = intent.getStringExtra("dateReceipt");
                paymentType = intent.getIntExtra("paymentType", -1);
                paymentMethod = intent.getIntExtra("paymentMethod", -1);
                break;
            default:

        }
        if (searchType > 0)
            SearchForQuery();

    }

    /* ------------------------------------------------------------------------------------------- */
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

                 final long[] pendingLoadCountCustomer = {snapshot.getChildrenCount()};

                 for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                     try {
                         Customer customer = new Customer();
                         customer = postSnapshot.getValue(Customer.class);
                         currentLinkedObjects.setCustomer(customer);

                         pendingLoadCountCustomer[0] = pendingLoadCountCustomer[0] - 1;

                         //find all PriceOffers of customer:
                         final DatabaseReference currentDdbRef = FirebaseDatabase.getInstance()
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
                                                                     adapter = new SearchResultsListAdapter(SearchResultsActivity.this, currentLinkedObjects);
                                                                     lvSearchResults.setAdapter(adapter);
                                                                     dialog.dismiss();
                                                                 }

                                                                /* //1. find all Receipts of Work:
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
                                                                                 Toast.makeText(getBaseContext(), "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
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
                                                                                 Toast.makeText(getBaseContext(), "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                                                                 dialog.dismiss();
                                                                             }
                                                                         });*/

                                                             }
                                                         }

                                                         @Override
                                                         public void onCancelled(DatabaseError firebaseError) {
                                                             Toast.makeText(getBaseContext(), "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                                             dialog.dismiss();
                                                         }
                                                     });


                                         }
                                     }

                                     @Override
                                     public void onCancelled(DatabaseError firebaseError) {
                                         Toast.makeText(getBaseContext(), "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                         dialog.dismiss();
                                     }
                                 });

                     } catch (Exception ex) {
                         Toast.makeText(getBaseContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
                         dialog.dismiss();
                     }
                 }
             }

             @Override
             public void onCancelled(DatabaseError firebaseError) {
                 Toast.makeText(getBaseContext(), "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                 dialog.dismiss();
             }
         }

        );
    }

    /* ------------------------------------------------------------------------------------------- */
    private void SearchForQuery() {
        // firebase

    }


}
