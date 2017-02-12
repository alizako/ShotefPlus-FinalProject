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
        Expense expense1= new Expense(dateFormat.format(date),new Work(), 3000, "הדפסת 1000 עותקים פלייר");
        Expense expense2= new Expense(dateFormat.format(date),new Work(),  200, "משלוח חו''ל");
        Expense expense3= new Expense(dateFormat.format(date),new Work(),  302, "הדפסת הזמנות");
        Expense expense4= new Expense(dateFormat.format(date),new Work(),  1000, "קניית תוכנה חדשה לביצוע דרישות לקוח");

        expenseList = new ArrayList<Expense>();
        expenseList.add(expense1);
        expenseList.add(expense2);
        expenseList.add(expense3);
        expenseList.add(expense4);


        adapter = new ExpenseListAdapter(ExpensesActivity.this,expenseList);
        lvExpenses.setAdapter(adapter);
    }
}
