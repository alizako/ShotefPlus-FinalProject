package finals.shotefplus.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import finals.shotefplus.objects.Customer;
import finals.shotefplus.adapters.CustomerListAdapter;
import finals.shotefplus.R;

public class CustomersActivity extends AppCompatActivity {

    ImageButton btnAdd;
    ListView lvCustomers;
    private List<Customer> customers;
    private CustomerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomersActivity.this,InsertCustomerActivity.class));
            }
        });


        lvCustomers = (ListView) findViewById(R.id.listViewCustomers);
        //Customer c= new Customer("Aliza",false,"dddfgdf","05555555","dsf@gggg");
        customers = new ArrayList<Customer>();
        customers.add(new Customer("Aliza",false,"dddfgdf","05555555","dsf@gggg"));
        customers.add(new Customer("ששון",false,"בהבעע עיכי","05555555","dsf@gggg"));
        customers.add(new Customer("ADAM",false,"עעג כעיכע","05555555","dsf@gggg"));
        customers.add(new Customer("Moran",false,"dddfgdf","05555555","dsf@gggg"));
        customers.add(new Customer("AlizaKo",false,"dddfgdf","05555555","dsf@gggg"));
        customers.add(new Customer("Shimon",false,"xfdfg dfdfg","05555555","dsf@gggg"));
        customers.add(new Customer("Joe A",false,"dddfgdf","05555555","dsf@gggg"));
        customers.add(new Customer("Ely",false,"dddfgdf","05555555","dsf@gggg"));

        adapter = new CustomerListAdapter(CustomersActivity.this,customers);
        lvCustomers.setAdapter(adapter);

    }
}
