package finals.shotefplus.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import finals.shotefplus.R;
import finals.shotefplus.adapters.CustomerListAdapter;
import finals.shotefplus.adapters.ExpenseListAdapter;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.Expense;
import finals.shotefplus.objects.Work;

public class ExpensesActivity extends AppCompatActivity {

    ImageButton btnAdd;
    ListView lvExpenses;
    private List<Expense> expenseList;
    private ExpenseListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        initExpensesList();


        btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExpensesActivity.this,InsertExpenseActivity.class));
            }
        });
    }

    private void initExpensesList() {

        lvExpenses = (ListView) findViewById(R.id.listViewExpenses);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        Expense expense1= new Expense(dateFormat.format(date),new Work(), 30, 3000, "תיאור 1");
        Expense expense2= new Expense(dateFormat.format(date),new Work(), 60, 2000, "תיאור 2");
        Expense expense3= new Expense(dateFormat.format(date),new Work(), 90, 3020, "תיאור 3");
        Expense expense4= new Expense(dateFormat.format(date),new Work(), 0, 100, "תיאור 4");

        expenseList = new ArrayList<Expense>();
        expenseList.add(expense1);
        expenseList.add(expense2);
        expenseList.add(expense3);
        expenseList.add(expense4);


        adapter = new ExpenseListAdapter(ExpensesActivity.this,expenseList);
        lvExpenses.setAdapter(adapter);
    }
}
