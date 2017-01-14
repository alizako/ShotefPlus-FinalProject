package finals.shotefplus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class CustomersActivity extends AppCompatActivity {

    ListView lvCustomers;
    private List<Customer> customers;
    private CustomerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        lvCustomers = (ListView) findViewById(R.id.listViewCustomers);
        Customer c= new Customer("Aliza",false,"dddfgdf","05555555","dsf@gggg");
        customers = new ArrayList<Customer>();
        customers.add(c);
        customers.add(c);
        customers.add(c);
        customers.add(c);
        customers.add(c);
        customers.add(c);
        customers.add(c);
        customers.add(c);
        customers.add(c);
        adapter = new CustomerListAdapter(CustomersActivity.this,customers);
        lvCustomers.setAdapter(adapter);

    }
}
