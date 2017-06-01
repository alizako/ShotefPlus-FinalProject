package finals.shotefplus.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import finals.shotefplus.DataAccessLayer.FirebaseHandler;
import finals.shotefplus.R;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.Expense;
import finals.shotefplus.objects.PriceOffer;
import finals.shotefplus.objects.Work;

public class InsertExpenseActivity extends AppCompatActivity {

    static final int REQ_ADD_WORK = 1;
    private FirebaseAuth firebaseAuth;
    Button btnAdd;
    Spinner spnrExpenseType, spnrWork;
    EditText etDate, etDetails, etSum;
    ImageButton imgBtnWork;
    boolean isUpdateMode = false;
    String expenseType;
    //Long workNum;
    Work work;
    ArrayAdapter<CharSequence> adapter;
    String currentKey;
    Expense expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_expense);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        setSpinners();

        etDate = (EditText) findViewById(R.id.etDate);
        etDate.setShowSoftInputOnFocus(false);
        etDetails = (EditText) findViewById(R.id.etDetails);
        etSum = (EditText) findViewById(R.id.etSum);
        imgBtnWork = (ImageButton) findViewById(R.id.imgBtnWork);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        Intent intent = getIntent();

        String expenseId = intent.getStringExtra("workIdNum");
        if (expenseId != null)//has value
        {
            isUpdateMode = true;
            currentKey = expenseId;
            Toast.makeText(getBaseContext(), expenseId, Toast.LENGTH_LONG).show();
            setValuesToFields(expenseId);
        }

        setEvents();
    }

    private void setSpinners() {
        //spinnerExpenses
        spnrExpenseType = (Spinner) findViewById(R.id.spnrExpenseType);
        adapter = ArrayAdapter.createFromResource(this, R.array.spinnerExpenseType,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrExpenseType.setAdapter(adapter);

        //spnrWork
        //TODO: make a work list instead of spinnerwork
        spnrWork = (Spinner) findViewById(R.id.spnrWork);
        adapter = ArrayAdapter.createFromResource(this, R.array.spinnerWork,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spnrWork.setAdapter(adapter);

    }

    /**********************************************************************************
     * Events
     **********************************************************************************/

    private void setEvents() {

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date;

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //updateLabel();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                final String dueDate = dateFormat.format(calendar.getTime());
                etDate.setText(dueDate);
            }
        };

        etDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new DatePickerDialog(InsertExpenseActivity.this, date,
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                }
        );

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFieldsValidate())
                    addExpenseToFireBase(v);
                // Toast.makeText(v.getContext(), "הוצאה התווספה", Toast.LENGTH_LONG).show();
            }
        });

        imgBtnWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Check with Adam
              //  Intent intent = new Intent(InsertExpenseActivity.this, InsertCustomerActivity.class);
                Intent intent = new Intent(InsertExpenseActivity.this, InsertPriceOfferActivity.class);
                startActivityForResult(intent, REQ_ADD_WORK);
            }
        });
    }


    private void setValuesToFields(final String expenseId) {

        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/Expenses/");

        dbRef.orderByChild("idNum").startAt(expenseId).endAt(expenseId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                            try {
                                expense = new Expense();
                                expense = postSnapshot.getValue(Expense.class);
                                etDate.setText(expense.dateToString());
                                etSum.setText(String.valueOf(expense.getSumPayment()));
                                etDetails.setText(expense.getSumDetails());

                                spnrWork.setPrompt(String.valueOf(expense.getWork().getIdNum()));//ToDo!! not working need to handle it in code
                              //  setSpinners();
                                // spnrExpenseType.setPrompt(expense.get);

                                btnAdd.setText("עדכן");
                            } catch (Exception ex) {
                                Toast.makeText(getBaseContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Toast.makeText(getBaseContext(), "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        // dialog.dismiss();
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_ADD_WORK) {
            if (resultCode == Activity.RESULT_OK) {
                spnrWork.setPrompt(data.getStringExtra("workIdNum"));
            } else {
                Toast.makeText(getBaseContext(), "ERROR: adding work", Toast.LENGTH_LONG).show();
            }

        }
    }

    /**********************************************************************************
     * private functions
     **********************************************************************************/

    private boolean isFieldsValidate() {
        // ((EditText) findViewById(R.id.etDetails)).getText().toString().trim();
        boolean flagErr = false;
        if (etDate.getText().toString().equals("")) flagErr = true;
        else if(! etDate.getText().toString().matches("^\\d{2}/\\d{2}/\\d{4}")) flagErr = true;
        if (etSum.getText().toString().equals("")) flagErr = true;
        if (flagErr) {
            Toast.makeText(getBaseContext(), "יש למלא שדות חובה: תאריך, סכום", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    /**********************************************************************************
     * FireBase
     **********************************************************************************/
    private void addExpenseToFireBase(View v) {

        try {

            //TODO: call insertWork instead code below
            work = new Work();
          //  work.setPriceOffer(new PriceOffer());

            String selected = spnrWork.getPrompt().toString();
            String frstEntry = getResources().getString(R.string.workNum);
            if (!selected.equals(frstEntry)) {
               // spnrWork.getSelectedItem().toString()
                work.setIdNum((spnrWork.getSelectedItem()).toString().trim());
            }

            expense = new Expense();
            expense.setDate(etDate.getText().toString());
            expense.setWork(work);
            expense.setSumDetails(etDetails.getText().toString());
            expense.setSumPayment(Double.parseDouble(etSum.getText().toString()));

            if (isUpdateMode) {
                FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid()).
                        updateExpense(expense, currentKey);

                Toast.makeText(v.getContext(), "הוצאה התעדכנה", Toast.LENGTH_LONG).show();
            } else {//add new expense
                currentKey = FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                        .insertExpense(expense);
                Toast.makeText(v.getContext(), "הוצאה התווספה", Toast.LENGTH_LONG).show();
            }

            finish(); //back to list
        } catch (Exception ex) {
            Toast.makeText(v.getContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }


}
