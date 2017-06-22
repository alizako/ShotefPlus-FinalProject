package finals.shotefplus.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import finals.shotefplus.objects.Customer;
import finals.shotefplus.adapters.CustomerListAdapter;
import finals.shotefplus.R;

public class CustomersActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    DatabaseReference dbRef;
    private ProgressDialog dialog;
    ImageButton btnAdd;
    ListView lvCustomers;
    private List<Customer> customers;
    private CustomerListAdapter adapter;
    private List<Customer> customerList;
    static final int REQ_UPD_CUSTOMER = 3;
    static final int REQ_ADD_CUSTOMER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        //init vars:
        firebaseAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseLink) +
                firebaseAuth.getCurrentUser().getUid() +
                getString(R.string.customersLink));

        lvCustomers = (ListView) findViewById(R.id.listViewCustomers);
        btnAdd = (ImageButton) findViewById(R.id.btnAdd);


        try {
            dialog = ProgressDialog.show(CustomersActivity.this,
                    "",
                    getString(R.string.loadMsg),
                    true);

            //get records from firebase
            dataRefHandling();

        } catch (Exception e) {
            dialog.dismiss();
            Toast.makeText(CustomersActivity.this,
                    "" + e.toString(),
                    Toast.LENGTH_LONG).show();
        }

        setEvents();
    }

    /**********************************************************************************
     * Events
     **********************************************************************************/
    private void setEvents() {
        //add new customer
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomersActivity.this, InsertCustomerActivity.class);
                startActivityForResult(intent, REQ_ADD_CUSTOMER);
                //startActivity(new Intent(CustomersActivity.this, InsertCustomerActivity.class));
            }
        });

        //click on one row leads to edit the customer
        lvCustomers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Customer customer = (Customer) lvCustomers.getAdapter().getItem(position);
                Intent intent = new Intent(CustomersActivity.this, InsertCustomerActivity.class);
                intent.putExtra(getString(R.string.customerIdNum), customer.getIdNum());
                startActivityForResult(intent,REQ_UPD_CUSTOMER);
            }
        });
    }

    /**********************************************************************************
     * FireBase
     **********************************************************************************/
    private void dataRefHandling() {

        //get customers ordered by name
        dbRef.orderByChild(getString(R.string.name))
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                customerList = new ArrayList<Customer>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    try {
                        Customer customer = new Customer();
                        customer = postSnapshot.getValue(Customer.class);
                        customerList.add(customer);
                    } catch (Exception ex) {
                        Toast.makeText(getBaseContext(),
                                getString(R.string.errorMsg)+ ex.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }
                //set list view through adapter
                adapter = new CustomerListAdapter(CustomersActivity.this, customerList);
                lvCustomers.setAdapter(adapter);
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
