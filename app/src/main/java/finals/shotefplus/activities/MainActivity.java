package finals.shotefplus.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import finals.shotefplus.MPcharts.PieChartActivity;
import finals.shotefplus.R;

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
                /*startActivity(new Intent(MainActivity.this,PieChartActivity.class));
                try{
                    startActivity(new Intent(MainActivity.this,PieChartActivity.class));
                }
                catch (Exception ex){

                }*/

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {

            case R.id.search:
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
                return true;
            case R.id.dotsMenu1Profile:
                //show profile
                return true;
            case R.id.dotsMenu2About:
                //show profile
                return true;
            case R.id.dotsMenu3Connect:
                //show profile
                return true;
            case R.id.dotsMenu4Exit:
                //exit app
                startActivity(new Intent(MainActivity.this, EntranceActivity.class)); //Go back to home page
                finish();
                Toast.makeText(this,"התנתקת מהמערכת בהצלחה" , Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);



        // Associate searchable configuration with the SearchView
        /*SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.e("onQueryTextChange", "called");
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                startActivity(new Intent(MainActivity.this,SearchResultsActivity.class));
                // Do your task here

                return false;
            }

        });*/

       // return true;//super.onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }


}
