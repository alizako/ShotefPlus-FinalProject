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
import finals.shotefplus.objects.EnumObjectType;
import finals.shotefplus.objects.Expense;
import finals.shotefplus.objects.PriceOffer;
import finals.shotefplus.objects.Receipt;
import finals.shotefplus.objects.Work;

public class SearchActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    Button btnSearch, btnClean;
    EditText etReceiptNum, etDueDateWork, etDateReceipt, etDueDateWorkPO, etDateExpense;
    Spinner spnrPaymentType, spnrPaymentMethod, spnrWork, spnrCustomer, spnrPriceOffer, spnrWorkExpenses;
    RadioButton rdCustomer, rdWork, rdPriceOffer, rdReceipt, rdExpense;
    LinearLayout loCustomer, loWork, loPriceOffer, loReceipt, loExpense;
    RadioGroup rdGrp;

    private ArrayList<PriceOffer> priceOffersList;
    private ArrayList<Work> worksList;
    private ArrayList<Customer> customerList;
    private ArrayList<Work> workExpenseList;
    private ArrayList<String> strTitleList;
    private ArrayList<String> strTitleWorkExpenseList;
    private ArrayAdapter<String> spinnerAdapterWork;
    private ArrayAdapter<String> spinnerAdapterCustomer;
    private ArrayAdapter<String> spinnerAdapterPO;

    static final int REQ_UPD_CUSTOMER = 3;

    ProgressDialog dialog;

    int objectType = 0;

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
        etDateExpense=(EditText) findViewById(R.id.etDateExpense);
        etDateExpense.setShowSoftInputOnFocus(false);

        spnrPaymentType = (Spinner) findViewById(R.id.spnrPaymentType);
        spnrPaymentMethod = (Spinner) findViewById(R.id.spnrPaymentMethod);
        spnrWork = (Spinner) findViewById(R.id.spnrWork);
        spnrCustomer = (Spinner) findViewById(R.id.spnrCustomer);
        spnrPriceOffer = (Spinner) findViewById(R.id.spnrPriceOffer);
        spnrWorkExpenses=(Spinner) findViewById(R.id.spnrWorkExpenses);

        rdCustomer = (RadioButton) findViewById(R.id.rdCustomer);
        rdWork = (RadioButton) findViewById(R.id.rdWork);
        rdPriceOffer = (RadioButton) findViewById(R.id.rdPriceOffer);
        rdReceipt = (RadioButton) findViewById(R.id.rdReceipt);
        rdExpense = (RadioButton) findViewById(R.id.rdExpense); //TODO!
        rdGrp = (RadioGroup) findViewById(R.id.rdGrp);

        loCustomer = (LinearLayout) findViewById(R.id.loCustomer);
        loWork = (LinearLayout) findViewById(R.id.loWork);
        loPriceOffer = (LinearLayout) findViewById(R.id.loPriceOffer);
        loReceipt = (LinearLayout) findViewById(R.id.loReceipt);
        loExpense = (LinearLayout) findViewById(R.id.loExpense);

        //  setSpinners();
        disableLayouts();
        setEvents();
    }

    /* ------------------------------------------------------------------------------------------- */
    private void disableLayouts() {
        setViewAndChildrenEnabled(loCustomer, false);
        setViewAndChildrenEnabled(loPriceOffer, false);
        setViewAndChildrenEnabled(loReceipt, false);
        setViewAndChildrenEnabled(loWork, false);
        setViewAndChildrenEnabled(loExpense, false);

        loCustomer.setVisibility(View.GONE);
        loPriceOffer.setVisibility(View.GONE);
        loReceipt.setVisibility(View.GONE);
        loWork.setVisibility(View.GONE);
        loExpense.setVisibility(View.GONE);
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
        final DatePickerDialog.OnDateSetListener dueDateWork, dateReceipt, datePriceOffer, dateExpense;

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

        dateExpense = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //updateLabel();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                final String dueDate = dateFormat.format(calendar.getTime());
                etDateExpense.setText(dueDate);
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

        etDateExpense.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(SearchActivity.this, dateExpense,
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                }
        );


        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  setSpinners();
                etDateReceipt.setText("");
                etDueDateWork.setText("");
                etDueDateWorkPO.setText("");
                etDateExpense.setText("");
                etReceiptNum.setText("");
                disableLayouts();
                rdGrp.clearCheck();
                if(dialog!=null)
                    dialog.dismiss();
            }
        });


        /***************************************************************************
         *   customer:
         *     if selected: 		show customer
         *
         *   Price Offer:
         *     if Selected: 		show Price Offer
         *     if date & selected: show Price Offer
         *     if date:			show Price Offer List inside Results
         *
         *   Work:
         *     if seleceted or date:	show Work List
         *
         *   Receipt:
         *     if Receipt Num:				show Receipt
         *     if Method or Type or date:	show Receipt List inside Results
         *
         *   Expense:
         *      if selected:            show Expense
         *      if date:                show Expense List
         ************************************************************************** */
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);
                intent.putExtra("searchType", objectType);

                if (!rdCustomer.isChecked() && !rdWork.isChecked() && !rdPriceOffer.isChecked()
                        && !rdReceipt.isChecked() && !rdExpense.isChecked())
                    Toast.makeText(getBaseContext(), "לא בוצעה בחירה", Toast.LENGTH_LONG).show();
                else {
                    /* Customer */
                    if (rdCustomer.isChecked()){
                        if (spnrCustomer.getSelectedItemPosition() > 0) {
                            //intent.putExtra("customerIdNum", getCustomerIdNum());
                            Intent intent2 = new Intent(SearchActivity.this, InsertCustomerActivity.class);
                            intent2.putExtra("customerIdNum", getCustomerIdNum());
                            startActivity(intent2);
                        }else
                            Toast.makeText(getBaseContext(), "לא נבחר לקוח", Toast.LENGTH_LONG).show();
                        }
                    /* Work */
                    if (rdWork.isChecked() &&
                            (etDueDateWork.getText().toString().length() > 0 || spnrWork.getSelectedItemPosition() > 0)) {
                        intent.putExtra("dueDateWork", etDueDateWork.getText().toString());
                        intent.putExtra("workIdNum", getWorkIdNum());
                        startActivity(intent);
                    }
                    /* Price Offer */
                    if (rdPriceOffer.isChecked()) {
                        if (/*etDueDateWorkPO.getText().toString().equals("") &&*/
                                spnrPriceOffer.getSelectedItemPosition() > 0) {
                            Intent intent2 = new Intent(SearchActivity.this, InsertPriceOfferActivity.class);
                            intent2.putExtra("PriceOfferId", getPOIdNum());
                            startActivity(intent2);
                        /*} else if (etDueDateWorkPO.getText().toString().length() > 0 &&
                                spnrPriceOffer.getSelectedItemPosition() > 0) {
                            if (isCorrelationPO()) { //TODO!!!!!
                                Intent intent2 = new Intent(SearchActivity.this, InsertPriceOfferActivity.class);
                                intent2.putExtra("etDueDateWorkPO", etDueDateWorkPO.getText().toString());
                                intent2.putExtra("PriceOfferId", getPOIdNum());
                                startActivity(intent2);
                            }*/
                        } else if (etDueDateWorkPO.getText().toString().length() > 0 &&
                                spnrPriceOffer.getSelectedItemPosition() <= 0) {
                            intent.putExtra("etDueDateWorkPO", etDueDateWorkPO.getText().toString());
                            intent.putExtra("PriceOfferId", getPOIdNum());
                            startActivity(intent);
                        }
                    }
                    /* Receipt */
                    if (rdReceipt.isChecked()) {
                      /*  if (etReceiptNum.getText().toString().length() > 0
                                && etDateReceipt.getText().toString().equals("")
                                && spnrPaymentType.getSelectedItemPosition() <= 0
                                && spnrPaymentMethod.getSelectedItemPosition() <= 0) {
                            Intent intent2 = new Intent(SearchActivity.this, InsertReceiptActivity.class);
                            intent2.putExtra("receiptNum", etReceiptNum.getText().toString());
                            startActivity(intent2);
                        } else if (etDateReceipt.getText().toString().length() > 0
                                || spnrPaymentType.getSelectedItemPosition() > 0
                                || spnrPaymentMethod.getSelectedItemPosition() > 0) {*/
                            intent.putExtra("receiptNum", etReceiptNum.getText().toString());
                            intent.putExtra("dateReceipt", etDateReceipt.getText().toString());
                            intent.putExtra("paymentType", getPaymentType());
                            intent.putExtra("paymentMethod", getPaymentMethod());
                            startActivity(intent);
                       // }
                    }
                    /* Expense */
                    if(rdExpense.isChecked()){
                        if(spnrWorkExpenses.getSelectedItemPosition() > 0){
                            Work work=getWorkExpense();
                            intent.putExtra("expenseWorkIdNum", work.getIdNum()+"");
                            intent.putExtra("expenseWorkName", work.getWorkDetails()+"");
                            startActivity(intent);
                        }else if (etDateExpense.getText().toString().length() > 0 &&
                                spnrWorkExpenses.getSelectedItemPosition() <= 0) {
                            intent.putExtra("dateExpense", etDateExpense.getText().toString());
                            startActivity(intent);
                        }
                    }
                    // startActivity(intent);
                }
            }
        });

        rdGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                disableLayouts();
                if (checkedId == rdReceipt.getId()) {
                    objectType = EnumObjectType.RECEIPT.getValue();
                    loReceipt.setVisibility(View.VISIBLE);
                    setViewAndChildrenEnabled(loReceipt, true);
                    etReceiptNum.requestFocus();
                }
                if (checkedId == rdWork.getId()) {
                    objectType = EnumObjectType.WORK.getValue();
                    initWorkSpinnerFromDB();
                    loWork.setVisibility(View.VISIBLE);
                    setViewAndChildrenEnabled(loWork, true);
                    spnrWork.requestFocus();
                }
                if (checkedId == rdPriceOffer.getId()) {
                    objectType = EnumObjectType.PRICE_OFFER.getValue();
                    initPriceOfferSpinnerFromDB();
                    loPriceOffer.setVisibility(View.VISIBLE);
                    setViewAndChildrenEnabled(loPriceOffer, true);
                    spnrPriceOffer.requestFocus();
                }
                if (checkedId == rdCustomer.getId()) {
                    objectType = EnumObjectType.CUSTOMER.getValue();
                    initCustomerSpinnerFromDB();
                    loCustomer.setVisibility(View.VISIBLE);
                    setViewAndChildrenEnabled(loCustomer, true);
                    spnrCustomer.requestFocus();
                }
                if (checkedId == rdExpense.getId()) {
                    objectType = EnumObjectType.EXPENSE.getValue();
                    initWorkSpinnerFromDB(); //sets work expenses as well
                    loExpense.setVisibility(View.VISIBLE);
                    setViewAndChildrenEnabled(loExpense, true);
                    spnrWorkExpenses.requestFocus();
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

    private Work getWorkExpense() {
        if (!spnrWorkExpenses.getSelectedItem().equals("-בחר עבודה עם הוצאות-")) {
            int pos = spnrWorkExpenses.getSelectedItemPosition();
            return worksList.get(pos - 1);
        }
        return null;
    }

    /* ------------------------------------------------------------------------------------------- */
    private boolean isCorrelationPO() {
        return true;
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
        spnrPaymentMethod.setSelection(0);
        spnrPaymentType.setSelection(0);
        initWorkSpinnerFromDB();
        initCustomerSpinnerFromDB();
        initPriceOfferSpinnerFromDB();
     //   initWorkExpenseSpinnerFromDB();
        dialog.dismiss();
    }

    /* ------------------------------------------------------------------------------------------- */
    private void initPriceOfferSpinnerFromDB() {//all works

        dialog = ProgressDialog.show(SearchActivity.this,
                "",
                "טוען נתונים..",
                true);

        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/PriceOffers/");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                priceOffersList = new ArrayList<PriceOffer>();
                strTitleList = new ArrayList<String>();
                strTitleList.add("-בחר הצעת מחיר-");

                final long[] pendingLoadCount = {snapshot.getChildrenCount()};
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        PriceOffer priceOffer = new PriceOffer();
                        priceOffer = postSnapshot.getValue(PriceOffer.class);
                        priceOffersList.add(priceOffer);
                        strTitleList.add(priceOffer.getWorkDetails());

                        pendingLoadCount[0] = pendingLoadCount[0] - 1;
                        if (pendingLoadCount[0] == 0) {
                            spinnerAdapterPO = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_spinner_item, strTitleList);
                            spinnerAdapterPO.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnrPriceOffer.setAdapter(spinnerAdapterPO);
                            dialog.dismiss();
                        }


                    } catch (Exception ex) {
                        Toast.makeText(getBaseContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(getBaseContext(), "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

    }


    /* ------------------------------------------------------------------------------------------- */
    private void initWorkSpinnerFromDB(String customerIdNum) {//all works

        dialog = ProgressDialog.show(SearchActivity.this,
                "",
                "טוען נתונים..",
                true);

        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/Works/");


        dbRef.orderByChild("customerIdNum").startAt(customerIdNum).endAt(customerIdNum)
                .addValueEventListener(new ValueEventListener() {
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
                        dialog.dismiss();
                    }
                });
    }

    /* ------------------------------------------------------------------------------------------- */
    private void initWorkSpinnerFromDB() {//all works
        dialog = ProgressDialog.show(SearchActivity.this,
                "",
                "טוען נתונים..",
                true);

        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/Works/");


        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                worksList = new ArrayList<Work>();
                strTitleList = new ArrayList<String>();
                strTitleList.add("-בחר עבודה-");
                strTitleWorkExpenseList = new ArrayList<String>();
                strTitleWorkExpenseList.add("-בחר עבודה עם הוצאות-");

                final long[] pendingLoadCount = {snapshot.getChildrenCount()};
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        Work work = new Work();
                        work = postSnapshot.getValue(Work.class);
                        worksList.add(work);
                        strTitleList.add(work.getWorkDetails());
                        strTitleWorkExpenseList.add(work.getWorkDetails());

                        pendingLoadCount[0] = pendingLoadCount[0] - 1;
                        if (pendingLoadCount[0] == 0) {
                            // spinner Works
                            spinnerAdapterWork = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_spinner_item, strTitleList);
                            spinnerAdapterWork.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnrWork.setAdapter(spinnerAdapterWork);

                            //spinner Work Expenses
                            spinnerAdapterWork = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_spinner_item, strTitleWorkExpenseList);
                            spinnerAdapterWork.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnrWorkExpenses.setAdapter(spinnerAdapterWork);

                            dialog.dismiss();
                        }


                    } catch (Exception ex) {
                        Toast.makeText(getBaseContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(getBaseContext(), "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }

    /* ------------------------------------------------------------------------------------------- */
    private void initCustomerSpinnerFromDB() {
        dialog = ProgressDialog.show(SearchActivity.this,
                "", "טוען נתונים..", true);
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

                final long[] pendingLoadCount = {snapshot.getChildrenCount()};
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        Customer customer = new Customer();
                        customer = postSnapshot.getValue(Customer.class);
                        customerList.add(customer);
                        strTitleList.add(customer.getName());

                        pendingLoadCount[0] = pendingLoadCount[0] - 1;
                        if (pendingLoadCount[0] == 0) {
                            spinnerAdapterCustomer = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_spinner_item, strTitleList);
                            spinnerAdapterCustomer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnrCustomer.setAdapter(spinnerAdapterCustomer);
                            dialog.dismiss();
                        }

                    } catch (Exception ex) {
                        Toast.makeText(getBaseContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(getBaseContext(), "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }
}
