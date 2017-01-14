package finals.shotefplus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnReceipts;
    Button btnOffers;
    Button btnExpenses;
    Button btnShowReports;
    Button btnReports;
    Button btnSettings;
    Button btnWorks;
    Button btnCustomers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Toast.makeText(this,"ERROR:" , Toast.LENGTH_LONG).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnReceipts = (Button) findViewById(R.id.btnReceipts);
        btnOffers = (Button) findViewById(R.id.btnOffers);
        btnExpenses = (Button) findViewById(R.id.btnExpenses);
        btnShowReports = (Button) findViewById(R.id.btnShowReports);
        btnReports = (Button) findViewById(R.id.btnReports);
        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnWorks = (Button) findViewById(R.id.btnWorks);
        btnCustomers = (Button) findViewById(R.id.btnCustomers);


        btnReceipts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ReceiptsActivity.class));
            }
        });
        btnOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PriceOffersActivity.class));
            }
        });
        btnExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ExpensesActivity.class));
            }
        });
        btnShowReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,IncomesVsExpensesActivity.class));
            }
        });
        btnReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ReportsActivity.class));
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
            }
        });
        btnWorks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,WorksActivity.class));
            }
        });
        btnCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CustomersActivity.class));
            }
        });


    }

}
