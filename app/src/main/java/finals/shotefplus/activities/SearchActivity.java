package finals.shotefplus.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import finals.shotefplus.R;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.PriceOffer;
import finals.shotefplus.objects.Work;

public class SearchActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    Button btnSearch, btnClean;
    TextView etReceiptNum, etDueDateWork, etDateReceipt, etDueDateWorkPO;
    Spinner spnrPaymentType, spnrPaymentMethod, spnrWork, spnrCustomer, spnrPriceOffer;
    RadioButton rdCustomer, rdWork, rdPriceOffer, rdReceipt;
    LinearLayout loCustomer,loWork,loPriceOffer, loReceipt;
    RadioGroup rdGrp;

    private ArrayList<PriceOffer> priceOffersList;
    private ArrayList<Work> worksList;
    private ArrayList<Customer> customerList;
    private ArrayList<String> strTitleList;
    private ArrayAdapter<String> spinnerAdapterWork;
    private ArrayAdapter<String> spinnerAdapterCustomer;
    private ArrayAdapter<String> spinnerAdapterPO;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        firebaseAuth = FirebaseAuth.getInstance();

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnClean = (Button) findViewById(R.id.btnClean);

        etReceiptNum = (EditText) findViewById(R.id.etReceiptNum);
        etDueDateWork = (EditText) findViewById(R.id.etDueDateWork);
        etDueDateWork.setShowSoftInputOnFocus(false);
        etDateReceipt = (EditText) findViewById(R.id.etDateReceipt);
        etDateReceipt.setShowSoftInputOnFocus(false);
        etDueDateWorkPO = (EditText) findViewById(R.id.etDueDateWorkPO);
        etDueDateWorkPO.setShowSoftInputOnFocus(false);

        spnrPaymentType = (Spinner) findViewById(R.id.spnrPaymentType);
        spnrPaymentMethod = (Spinner) findViewById(R.id.spnrPaymentMethod);
        spnrWork = (Spinner) findViewById(R.id.spnrWork);
        spnrCustomer = (Spinner) findViewById(R.id.spnrCustomer);
        spnrPriceOffer = (Spinner) findViewById(R.id.spnrPriceOffer);

        rdCustomer = (RadioButton) findViewById(R.id.rdCustomer);
        rdWork = (RadioButton) findViewById(R.id.rdWork);
        rdPriceOffer = (RadioButton) findViewById(R.id.rdPriceOffer);
        rdReceipt = (RadioButton) findViewById(R.id.rdReceipt);
        rdGrp = (RadioGroup) findViewById(R.id.rdGrp);

        loCustomer = (LinearLayout)findViewById(R.id.loCustomer);
        loWork = (LinearLayout)findViewById(R.id.loWork);
        loPriceOffer= (LinearLayout)findViewById(R.id.loPriceOffer);
        loReceipt= (LinearLayout)findViewById(R.id.loReceipt);

        setSpinners();
        disableLayouts();
        setEvents();
    }

    /* ------------------------------------------------------------------------------------------- */
    private void disableLayouts() {
        setViewAndChildrenEnabled(loCustomer, false);
        setViewAndChildrenEnabled(loPriceOffer, false);
        setViewAndChildrenEnabled(loReceipt, false);
        setViewAndChildrenEnabled(loWork, false);
    }

    private void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }
    /* ------------------------------------------------------------------------------------------- */
    private void setEvents() {
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dueDateWork, dateReceipt, datePriceOffer;

        dueDateWork = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //updateLabel();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                final String dueDate = dateFormat.format(calendar.getTime());
                etDueDateWork.setText(dueDate);
            }
        };

        dateReceipt = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //updateLabel();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                final String dueDate = dateFormat.format(calendar.getTime());
                etDateReceipt.setText(dueDate);
            }
        };

        datePriceOffer = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //updateLabel();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                final String dueDate = dateFormat.format(calendar.getTime());
                etDueDateWorkPO.setText(dueDate);
            }
        };

        etDueDateWork.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(SearchActivity.this, dueDateWork,
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                }
        );

        etDateReceipt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(SearchActivity.this, dateReceipt,
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                }
        );

        etDueDateWorkPO.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(SearchActivity.this, datePriceOffer,
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                }
        );

        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSpinners();
                etDateReceipt.setText("");
                etDueDateWork.setText("");
                etDueDateWork.setText("");
                etReceiptNum.setText("");
                disableLayouts();
                rdGrp.clearCheck();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);

                if (!rdCustomer.isChecked() && !rdWork.isChecked() && !rdPriceOffer.isChecked()
                        && !rdReceipt.isChecked())
                    Toast.makeText(getBaseContext(), "לא בוצעה בחירה", Toast.LENGTH_LONG).show();
                else {
                    if (rdCustomer.isChecked()) {
                        intent.putExtra("searchType", 1);
                        intent.putExtra("customerIdNum", getCustomerIdNum());
                    }
                    if (rdWork.isChecked()) {
                        intent.putExtra("searchType", 2);
                        intent.putExtra("dueDateWork", etDueDateWork.getText());
                        intent.putExtra("workIdNum", getWorkIdNum());
                    }
                    if (rdPriceOffer.isChecked()) {
                        intent.putExtra("searchType", 3);
                        intent.putExtra("etDueDateWorkPO", etDueDateWorkPO.getText());
                        intent.putExtra("priceOfferIdNum", getPOIdNum());
                    }
                    if (rdPriceOffer.isChecked()) {
                        intent.putExtra("searchType", 4);
                        intent.putExtra("receiptNum", etReceiptNum.getText());
                        intent.putExtra("dateReceipt", etDateReceipt.getText());
                        intent.putExtra("paymentType", getPaymentType());
                        intent.putExtra("paymentMethod", getPaymentMethod());
                    }

                    startActivity(intent);
                }
            }
        });

        rdGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                disableLayouts();
                if (checkedId==rdReceipt.getId()){
                    setViewAndChildrenEnabled(loReceipt, true);
                    etReceiptNum.requestFocus();
                }
                if (checkedId==rdWork.getId()){
                    setViewAndChildrenEnabled(loWork, true);
                    spnrWork.requestFocus();
                }
                if (checkedId==rdPriceOffer.getId()){
                    setViewAndChildrenEnabled(loPriceOffer, true);
                    spnrPriceOffer.requestFocus();
                }
                if (checkedId==rdCustomer.getId()){
                    setViewAndChildrenEnabled(loCustomer, true);
                    spnrCustomer.requestFocus();
                }
            }
        });

        /*spnrCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position > 0)
                    initWorkSpinnerFromDB(customerList.get(position - 1).getIdNum());//set specific works for customer
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });*/
    }

    /* ------------------------------------------------------------------------------------------- */
    private String getCustomerIdNum() {
        if (!spnrCustomer.getSelectedItem().equals("-בחר לקוח-")) {
            int pos = spnrCustomer.getSelectedItemPosition();
            return customerList.get(pos - 1).getIdNum();
        }
        return "";
    }

    /* ------------------------------------------------------------------------------------------- */
    private String getWorkIdNum() {
        if (!spnrWork.getSelectedItem().equals("-בחר עבודה-")) {
            int pos = spnrWork.getSelectedItemPosition();
            return worksList.get(pos - 1).getIdNum();
        }
        return "";
    }

    /* ------------------------------------------------------------------------------------------- */
    private String getPOIdNum() {
        if (!spnrPriceOffer.getSelectedItem().equals("-בחר הצעת מחיר-")) {
            int pos = spnrPriceOffer.getSelectedItemPosition();
            return priceOffersList.get(pos - 1).getIdNum();
        }
        return "";
    }
    /* ------------------------------------------------------------------------------------------- */
    private int getPaymentType() {
        if (!spnrPaymentType.getSelectedItem().equals("-בחר צורת תשלום-")) {
            int pos = spnrPaymentType.getSelectedItemPosition();
            return pos;
        }
        return 0;
    }

    /* ------------------------------------------------------------------------------------------- */
    private int getPaymentMethod() {
        int paymentMethod = 0;
        if (!spnrPaymentMethod.getSelectedItem().equals("-בחר סוג תשלום-")) {
            int pos = spnrPaymentMethod.getSelectedItemPosition();
            switch (pos) {
                case 1:
                    paymentMethod = 30;
                    break;
                case 2:
                    paymentMethod = 60;
                    break;
                case 3:
                    paymentMethod = 90;
                    break;
                case 4:
                    paymentMethod = 120;
                    break;
            }
        }
        return paymentMethod;
    }

    /* ------------------------------------------------------------------------------------------- */
    private void setSpinners() {
        dialog = ProgressDialog.show(SearchActivity.this,
                "",
                "טוען נתונים..",
                true);
        spnrPaymentMethod.setSelection(0);
        spnrPaymentType.setSelection(0);
        initWorkSpinnerFromDB();
        initCustomerSpinnerFromDB();
        initPriceOfferSpinnerFromDB();
    }

    /* ------------------------------------------------------------------------------------------- */
    private void initPriceOfferSpinnerFromDB() {//all works

        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/PriceOffers/");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                priceOffersList = new ArrayList<PriceOffer>();
                strTitleList = new ArrayList<String>();
                strTitleList.add("-בחר הצעת מחיר-");

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        PriceOffer priceOffer = new PriceOffer();
                        priceOffer = postSnapshot.getValue(PriceOffer.class);
                        priceOffersList.add(priceOffer);
                        strTitleList.add(priceOffer.getWorkDetails());

                        spinnerAdapterPO = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_spinner_item, strTitleList);
                        spinnerAdapterPO.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnrPriceOffer.setAdapter(spinnerAdapterPO);


                    } catch (Exception ex) {
                        Toast.makeText(getBaseContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(getBaseContext(), "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    /* ------------------------------------------------------------------------------------------- */
    private void initWorkSpinnerFromDB() {//all works

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

                        spinnerAdapterWork = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_spinner_item, strTitleList);
                        spinnerAdapterWork.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnrWork.setAdapter(spinnerAdapterWork);


                    } catch (Exception ex) {
                        Toast.makeText(getBaseContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(getBaseContext(), "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /* ------------------------------------------------------------------------------------------- */
    private void initCustomerSpinnerFromDB() {
        //       spnrCustomer
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
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

                        spinnerAdapterCustomer = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_spinner_item, strTitleList);
                        spinnerAdapterCustomer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnrCustomer.setAdapter(spinnerAdapterCustomer);
                        dialog.dismiss();

                    } catch (Exception ex) {
                        Toast.makeText(getBaseContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(getBaseContext(), "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
