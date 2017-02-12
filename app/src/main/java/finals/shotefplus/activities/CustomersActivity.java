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
        customers.add(new Customer("Adam", false, "כפר אדומים", "050-5555535", "adam@gmail.com"));
        customers.add(new Customer("Aliza", false, "עמק רפאים ירושלים", "052-8657571", "aliza@gmail.com"));
        customers.add(new Customer("Ely", true, "הרצליה", "056-9998877", "ely@gmail.com"));
        customers.add(new Customer("Joe A", false, "אבן גבירול תל אביב-יפו", "050-6662223", "joe.a@gmail.com"));
        customers.add(new Customer("Moran", true, "גילה ירושלים", "055-5533554", "moran@gmail.com"));
        customers.add(new Customer("Sasson", false, "מלחה ירושלים", "052-5555555", "sasson@gmail.com"));
        customers.add(new Customer("Shimon", true, "מבשרת ציון", "050-5463723", "shimi@gmail.com"));



        adapter = new CustomerListAdapter(CustomersActivity.this,customers);
        lvCustomers.setAdapter(adapter);

    }
}
