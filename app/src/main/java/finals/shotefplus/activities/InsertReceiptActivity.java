package finals.shotefplus.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import finals.shotefplus.DataAccessLayer.FirebaseHandler;
import finals.shotefplus.R;
import finals.shotefplus.adapters.CustomerListAdapter;
import finals.shotefplus.adapters.ReceiptListAdapter;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.PriceOffer;
import finals.shotefplus.objects.Receipt;
import finals.shotefplus.objects.Work;

public class InsertReceiptActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    Button btnAdd;
    EditText etReceiptNum, etDate, etDetails, etSum;
    CheckBox cbSumMaam, cbPaid;
    Spinner spnrCustomer, spnrWork, spnrPaymentType, spnrPaymentMethod;
    ImageView imgBtnCustomer;
    boolean isUpdateMode = false;
    Receipt receipt;
    String currentKey;
    private ProgressDialog dialog;

    private ArrayList<Work> worksList;
    private ArrayList<Customer> customerList;
    private ArrayList<String> strTitleList;
    private ArrayAdapter<String> spinnerAdapterWork;
    private ArrayAdapter<String> spinnerAdapterCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_receipt);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        btnAdd = (Button) findViewById(R.id.btnAdd);

        etReceiptNum = (EditText) findViewById(R.id.etReceiptNum);
        etDate = (EditText) findViewById(R.id.etDate);
        etDate.setShowSoftInputOnFocus(false);
        etDetails = (EditText) findViewById(R.id.etDetails);
        etSum = (EditText) findViewById(R.id.etSum);

        cbSumMaam = (CheckBox) findViewById(R.id.cbSumMaam);
        cbPaid = (CheckBox) findViewById(R.id.cbPaid);

        spnrCustomer = (Spinner) findViewById(R.id.spnrCustomer);
        spnrWork = (Spinner) findViewById(R.id.spnrWork);
        spnrPaymentType = (Spinner) findViewById(R.id.spnrPaymentType);
        spnrPaymentMethod = (Spinner) findViewById(R.id.spnrPaymentMethod);


        Intent intent = getIntent();

        String receiptId = intent.getStringExtra("receiptIdNum");
        if (receiptId != null)//has value
        {
            isUpdateMode = true;
            currentKey = receiptId;
            Toast.makeText(getBaseContext(), receiptId, Toast.LENGTH_LONG).show();
            initSpinnersFromDB();
            setValuesToFields(receiptId);
        }

        initSpinnersFromDB();
        setEvents();
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

                        new DatePickerDialog(InsertReceiptActivity.this, date,
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
                    addCustomerToFireBase(v);
            }
        });

    }

    /**********************************************************************************
     * FireBase
     **********************************************************************************/

    private void setValuesToFields(final String receiptId) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/Receipts/");

        dbRef.orderByChild("idNum").startAt(receiptId).endAt(receiptId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                            try {
                                receipt = new Receipt();
                                receipt = postSnapshot.getValue(Receipt.class);

                                etReceiptNum.setText(receipt.getReceiptNum());
                                etDate.setText(receipt.dateReceiptToString());
                                etDetails.setText(receipt.getDealDetails());
                                etSum.setText(Double.toString(receipt.getSumPayment()));

                                cbSumMaam.setChecked(receipt.isSumPaymentMaam());
                                cbPaid.setChecked(receipt.isPaid());

                                setSpinners(receipt);

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


    private void addCustomerToFireBase(View v) {
        try {
            if (!isUpdateMode)
                receipt = new Receipt();
            receipt.setSumPayment(Double.parseDouble(etSum.getText().toString()));
            receipt.setSumPaymentMaam(cbSumMaam.isChecked());
            receipt.setDateReceipt(etDate.getText().toString());//TODO:CHECK!
            receipt.setReceiptNum(etReceiptNum.getText().toString());
            receipt.setDealDetails(etDetails.getText().toString());
            receipt.setPaid(cbPaid.isChecked());

            setSpinnerValuesToReceipt();

            if (isUpdateMode) {
                //priceOffer.setIdNum(currentKey);
                FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid()).
                        updateReceipt(receipt, currentKey);

                Toast.makeText(v.getContext(), "קבלה התעדכנה", Toast.LENGTH_LONG).show();
            } else {//add new price offer
                currentKey = FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                        .insertReceipt(receipt);
                Toast.makeText(v.getContext(), "קבלה התווספה", Toast.LENGTH_LONG).show();
            }

            finish(); //back to list
        } catch (Exception ex) {
            Toast.makeText(v.getContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }


    /* ------------------------------------------------------------------------------------------- */
    private void initSpinnersFromDB() {

        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/Works/");


        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                worksList = new ArrayList<Work>();
                strTitleList = new ArrayList<String>();
                strTitleList.add("-בחר עבודה-");

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        Work work = new Work();
                        work = postSnapshot.getValue(Work.class);
                        worksList.add(work);
                        strTitleList.add(work.getWorkDetails());

                        spinnerAdapterWork = new ArrayAdapter<String>(InsertReceiptActivity.this, android.R.layout.simple_spinner_item, strTitleList);
                        spinnerAdapterWork.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnrWork.setAdapter(spinnerAdapterWork);


                    } catch (Exception ex) {
                        Toast.makeText(getBaseContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(getBaseContext(), "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();

            }

        });

        //       spnrCustomer
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/Customers/");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                customerList = new ArrayList<Customer>();
                strTitleList = new ArrayList<String>();
                strTitleList.add("-בחר לקוח-");
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        Customer customer = new Customer();
                        customer = postSnapshot.getValue(Customer.class);
                        customerList.add(customer);
                        strTitleList.add(customer.getName());

                        spinnerAdapterCustomer = new ArrayAdapter<String>(InsertReceiptActivity.this, android.R.layout.simple_spinner_item, strTitleList);
                        spinnerAdapterCustomer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnrCustomer.setAdapter(spinnerAdapterCustomer);

                    } catch (Exception ex) {
                        Toast.makeText(getBaseContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(getBaseContext(), "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }

    /**********************************************************************************
     * private functions
     **********************************************************************************/
    private boolean isFieldsValidate() {
        boolean flagErr = false;
        if (etDate.getText().toString().equals("")) flagErr = true;
        if (etSum.getText().toString().equals("")) flagErr = true;
        if (spnrCustomer.getSelectedItem().equals("-בחר לקוח-")) flagErr = true;
        if (spnrWork.getSelectedItem().equals("-בחר עבודה-")) flagErr = true;

        if (flagErr) {
            Toast.makeText(getBaseContext(), "יש למלא שדות חובה: תאריך, סכום, לקוח, עבודה", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /* ------------------------------------------------------------------------------------------- */
    private void setSpinners(Receipt receipt) {

        // Customer Spinner:
        int i = 0;
        while (customerList != null && i < customerList.size()
                && !(receipt.getCustomerIdNum().equals(customerList.get(i).getIdNum()))) {
            i++;
        }
        if (i < customerList.size())
            spnrCustomer.setSelection(i+1);

        // Work Spinner:
        i = 0;
        while (worksList != null && i < worksList.size()
                && !(receipt.getWorkIdNum().equals(worksList.get(i).getIdNum()))) {
            i++;
        }
        if (i < worksList.size())
            spnrWork.setSelection(i+1);

        // PaymentMethod Spinner:
        if (receipt.getPaymentMethod() > 0) {
            int pos = 0;
            switch (receipt.getPaymentMethod()) {
                case 30:
                    pos = 1;
                case 60:
                    pos = 2;
                case 90:
                    pos = 3;
                case 120:
                    pos = 4;
            }
            spnrPaymentMethod.setSelection(pos);
        }

        // PaymentType Spinner:
        spnrPaymentType.setSelection(receipt.getPaymentType());
    }

    /* ------------------------------------------------------------------------------------------- */
    private void setSpinnerValuesToReceipt() {
        //Title of spinner in position 0
        if (!spnrCustomer.getSelectedItem().equals("-בחר לקוח-")) {
            int pos = spnrCustomer.getSelectedItemPosition();
            receipt.setCustomerIdNum(customerList.get(pos - 1).getIdNum());
        }
        if (!spnrWork.getSelectedItem().equals("-בחר עבודה-")) {
            int pos = spnrWork.getSelectedItemPosition();
            receipt.setWorkIdNum(worksList.get(pos - 1).getIdNum());
        }
        if (!spnrPaymentMethod.getSelectedItem().equals("-בחר סוג תשלום-")) {
            int pos = spnrPaymentMethod.getSelectedItemPosition();
            int paymentMethod = 0;
            switch (pos) {
                case 1:
                    paymentMethod = 30;
                case 2:
                    paymentMethod = 60;
                case 3:
                    paymentMethod = 90;
                case 4:
                    paymentMethod = 120;
            }
            receipt.setPaymentMethod(paymentMethod);
        }

        if (!spnrPaymentType.getSelectedItem().equals("-בחר צורת תשלום-")) {
            int pos = spnrPaymentType.getSelectedItemPosition();
            receipt.setPaymentType(pos);
        }

    }
}
