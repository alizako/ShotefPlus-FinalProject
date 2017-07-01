package finals.shotefplus.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.firebase.auth.FirebaseAuth;

import finals.shotefplus.MPcharts.HalfPieChartActivity;
import finals.shotefplus.MPcharts.PieChartActivity;
import finals.shotefplus.R;
import finals.shotefplus.objects.Expense;

public class MainActivity extends AppCompatActivity {

    Button btnReceipts;
    Button btnOffers;
    Button btnExpenses;
    Button btnShowCharts;
    Button btnReports;
    Button btnSettings;
    Button btnWorks;
    Button btnCustomers;
    private FirebaseAuth firebaseAuth;//defining firebaseauth object
    static final int REQ_PROFILE = 1;
    static final int RESULT_EXP = 2;
    String errMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Toast.makeText(this,"ERROR:" , Toast.LENGTH_LONG).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        errMsg = getString(R.string.err1)+ "\n"+ getString(R.string.err2);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        btnReceipts = (Button) findViewById(R.id.btnReceipts);
        btnOffers = (Button) findViewById(R.id.btnOffers);
        btnExpenses = (Button) findViewById(R.id.btnExpenses);
        btnShowCharts = (Button) findViewById(R.id.btnShowCharts);
        btnReports = (Button) findViewById(R.id.btnReports);
        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnWorks = (Button) findViewById(R.id.btnWorks);
        btnCustomers = (Button) findViewById(R.id.btnCustomers);


        btnReceipts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ReceiptsActivity.class));
            }
        });
        btnOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PriceOffersActivity.class));
            }
        });
        btnExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ExpensesActivity.class));
            }
        });
        btnShowCharts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, HalfPieChartActivity.class));

               startActivity(new Intent(MainActivity.this, IncomesVsExpensesActivity.class));
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
               // startActivity(new Intent(MainActivity.this, ReportsActivity.class));
                Toast.makeText(getBaseContext(), "לא בשימוש בשלב זה", Toast.LENGTH_LONG).show();
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
        btnWorks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WorksActivity.class));
            }
        });
        btnCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CustomersActivity.class));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {

            case R.id.search:
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                return true;
            case R.id.dotsMenu1Profile:
                //show profile
                showProfile();
                return true;
            case R.id.dotsMenu2About:
                //about
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;
            case R.id.dotsMenu3Connect:
                //connect
                startActivity(new Intent(MainActivity.this, ConnectActivity.class));
                return true;
            case R.id.dotsMenu4Exit:
                //exit app
               // signOut();
                AlertDialog confirmationDialog = openConfirmationDialog();
                confirmationDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog confirmationDialog = openConfirmationDialog();
        confirmationDialog.show();
    }

    private AlertDialog openConfirmationDialog() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.signout))
                .setMessage(getString(R.string.signOutMsg))
                .setIcon(R.drawable.alert_32)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        signOut();
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;

    }

    private void showProfile() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivityForResult(intent, REQ_PROFILE);
    }

    private void signOut() {
        try {
            firebaseAuth.signOut();
            startActivity(new Intent(MainActivity.this, EntranceActivity.class)); //Go back to home page
            finish();
            Toast.makeText(this, getString(R.string.signOutOk), Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
            System.exit(0);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, getString(R.string.profileUpdateMsg), Toast.LENGTH_LONG).show();
            }
            if (resultCode == RESULT_EXP) {
                Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
            }
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
