package finals.shotefplus.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import finals.shotefplus.R;
import finals.shotefplus.adapters.ExpenseListAdapter;
import finals.shotefplus.objects.Expense;
import finals.shotefplus.objects.Work;

public class ExpensesActivity extends AppCompatActivity {

    View barMonth;
    ImageButton btnAdd;
    private TextView txtNext, txtPrev, txtDate;
    ListView lvExpenses;
    private List<Expense> expenseList;
    private ExpenseListAdapter adapter;
    static final int RESULT_CLEAN = 2;
    static final int REQ_UPD_WORK = 3;
    static final int REQ_ADD_CUSTOMER = 2;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog dialog;
    DatabaseReference dbRef;
    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);


       // initExpensesList();

        //init vars:
        firebaseAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseLink) +
                firebaseAuth.getCurrentUser().getUid() +
                getString(R.string.expensesLink));

        barMonth = findViewById(R.id.barMonth);
        txtNext = (TextView) barMonth.findViewById(R.id.txtNext);
        txtPrev = (TextView) barMonth.findViewById(R.id.txtPrev);
        txtDate = (TextView) barMonth.findViewById(R.id.txtMonth);
        btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        lvExpenses = (ListView) findViewById(R.id.listViewExpenses);

        date = new Date();
        setCurrentDateBarMonth();

        try {
            dialog = ProgressDialog.show(ExpensesActivity.this,
                    "",
                    getString(R.string.loadMsg),
                    true);

            dataRefHandling();

        } catch (Exception e) {
            dialog.dismiss();
            Toast.makeText(ExpensesActivity.this,
                    "" + e.toString(),
                    Toast.LENGTH_LONG).show();
        }

        setEvents();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_UPD_WORK) {
            //   dataRefHandling();
        }
    }//onActivityResult

    /**********************************************************************************
     * Private Functions
     **********************************************************************************/
    private void setCurrentDateBarMonth() {
        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormatMonthBar));
        txtDate.setText(dateFormat.format(date));
    }

    /**********************************************************************************
     * Events
     **********************************************************************************/
    private void setEvents() {
        //add expense
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExpensesActivity.this,InsertExpenseActivity.class));
            }
        });
        //edit expense
        lvExpenses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Expense expense = (Expense) lvExpenses.getAdapter().getItem(position);

                Intent intent = new Intent(ExpensesActivity.this, InsertExpenseActivity.class);
                intent.putExtra(getString(R.string.expenseIdNum), expense.getIdNum());
                startActivityForResult(intent,REQ_UPD_WORK);
            }
        });

       /* lvExpenses.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id)
            {
                return true;
            }
        });*/

        //bar month clicks- next, prev, current: changes the view
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
    private void dataRefHandling() {

        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormat));
        final String dateFormated = dateFormat.format(date);
        dbRef.orderByChild(getString(R.string.dateDB))
                .startAt(dateFormated).endAt(dateFormated + "\uf8ff") //!!!!!
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        expenseList = new ArrayList<Expense>();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                            try {
                                Expense expense = new Expense();
                                expense = postSnapshot.getValue(Expense.class);
                                expenseList.add(expense);
                            } catch (Exception ex) {
                                Toast.makeText(getBaseContext(),
                                        getString(R.string.errorMsg) + ex.toString(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                        adapter = new ExpenseListAdapter(ExpensesActivity.this, expenseList);
                        lvExpenses.setAdapter(adapter);
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
