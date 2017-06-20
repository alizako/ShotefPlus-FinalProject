package finals.shotefplus.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    private FirebaseStorage storage;
    StorageReference storageRef;
    Button btnAdd;
    EditText etReceiptNum, etDate, etDetails, etSum;
    CheckBox cbSumMaam, cbPaid;
    Spinner spnrCustomer, spnrWork, spnrPaymentType, spnrPaymentMethod;
    ImageView imgBtnCustomer, imgBtnAddReceipt, imgBtnReceiptPreview;
    boolean isUpdateMode = false;
    Receipt receipt;
    String currentKey;
    String customerAddedIdNum;
    private ProgressDialog dialog;

    private ArrayList<Work> worksList;
    private ArrayList<Customer> customerList;
    private ArrayList<String> strTitleList;
    private ArrayAdapter<String> spinnerAdapterWork;
    private ArrayAdapter<String> spinnerAdapterCustomer;

    int PICK_IMAGE_REQUEST = 111;
    static final int REQ_ADD_CUSTOMER = 1;
    static final int REQ_OPEN_RECEIPT = 5;
    Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_receipt);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://shotefplus-72799.appspot.com/" + firebaseAuth.getCurrentUser().getUid());

        btnAdd = (Button) findViewById(R.id.btnAdd);
        imgBtnAddReceipt = (ImageView) findViewById(R.id.imgBtnAddReceipt);
        imgBtnReceiptPreview = (ImageView) findViewById(R.id.imgBtnReceiptPreview);
        imgBtnCustomer = (ImageView) findViewById(R.id.imgBtnCustomer);

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

        initWorkSpinnerFromDB();
        initCustomerSpinnerFromDB();


        Intent intent = getIntent();

        String receiptId = intent.getStringExtra("receiptIdNum");
        if (receiptId != null)//has value
        {
            isUpdateMode = true;
            currentKey = receiptId;
         //   Toast.makeText(getBaseContext(), receiptId, Toast.LENGTH_LONG).show();
            setValuesToFields(receiptId);
        }

        setEvents();//must be after setValuesToFields!
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
        }
        if (requestCode == REQ_ADD_CUSTOMER) {
            if (resultCode == Activity.RESULT_OK) {
                customerAddedIdNum = data.getStringExtra("customerAddedIdNum");
                setSelectionCustomerSpinner(customerAddedIdNum);//set selection to added customer
            } else {
                Toast.makeText(getBaseContext(), "ERROR: adding customer", Toast.LENGTH_LONG).show();
            }
        }
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
                    addReceiptToFireBase(v);
            }
        });

        imgBtnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InsertReceiptActivity.this, InsertCustomerActivity.class);
                startActivityForResult(intent, REQ_ADD_CUSTOMER);
            }
        });

        spnrPaymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position > 0)
                    cbPaid.setChecked(true);
                else
                    cbPaid.setChecked(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        imgBtnAddReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });

        imgBtnReceiptPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InsertReceiptActivity.this, ReceiptImageActivity.class);
                if (!isUpdateMode ||
                        (isUpdateMode && filePath != null)) {
                    intent.putExtra("isFromStorage", false);
                    intent.setData(filePath);
                    startActivityForResult(intent, REQ_OPEN_RECEIPT);
                } else //updateMode or re-chosen pic
                {
                    if (receipt.isPicReceiptExist()) {
                        intent.putExtra("isFromStorage", true);
                        intent.putExtra("receiptPictureIdNum", receipt.getIdNum());
                        intent.putExtra("receiptNum", receipt.getReceiptNum());
                        startActivityForResult(intent, REQ_OPEN_RECEIPT);
                    } else {
                        Toast.makeText(InsertReceiptActivity.this,
                                "לא נשמרה תמונה עבור קבלה זו",
                                Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

    }

    /**********************************************************************************
     * FireBase
     **********************************************************************************/

    private void setValuesToFields(final String receiptId) {
        dialog = ProgressDialog.show(InsertReceiptActivity.this,
                "",
                "טוען נתונים..",
                true);

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
                                dialog.dismiss();
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

    /* ------------------------------------------------------------------------------------------- */
    private void addReceiptToFireBase(View v) {
        try {
            if (!isUpdateMode)
                receipt = new Receipt();

            setSpinnerValuesToReceipt();
            receipt.setSumPayment(Double.parseDouble(etSum.getText().toString()));
            receipt.setSumPaymentMaam(cbSumMaam.isChecked());
            receipt.setPaymentWithMaam();
            receipt.setDateReceipt(etDate.getText().toString());//TODO:CHECK!
            receipt.setDueDatePayment();
            receipt.setReceiptNum(etReceiptNum.getText().toString());
            receipt.setDealDetails(etDetails.getText().toString());
            receipt.setPaid(cbPaid.isChecked());

            if (filePath != null)
                receipt.setPicReceiptExist(true);


            String strReceipt = "";
            dialog = ProgressDialog.show(InsertReceiptActivity.this,
                    "",
                    "שומר נתונים..",
                    true);

            if (isUpdateMode) {

                FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid()).
                        updateReceipt(receipt, currentKey);
                if (filePath != null)
                    storageRef.child(currentKey + ".jpg").delete(); //delete pic from storage before save new one

                strReceipt = "קבלה התעדכנה";
            } else {//add new price offer

                currentKey = FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                        .insertReceipt(receipt);

                strReceipt = "קבלה התווספה";
            }

            if (currentKey != null && filePath != null) {
                storageRef.child(currentKey + ".jpg").putFile(filePath);//add picture to Firebase Storage
            }
            Toast.makeText(v.getContext(), strReceipt, Toast.LENGTH_LONG).show();
            dialog.dismiss();

            finish(); //back to list
        } catch (Exception ex) {
            Toast.makeText(v.getContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
        }
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

    /* ------------------------------------------------------------------------------------------- */
    private void initWorkSpinnerFromDB() {

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
        setSelectionCustomerSpinner(receipt.getCustomerIdNum());

        // Work Spinner:
        int i = 0;
        while (worksList != null && i < worksList.size()
                && !(receipt.getWorkIdNum().equals(worksList.get(i).getIdNum()))) {
            i++;
        }
        if (i < worksList.size())
            spnrWork.setSelection(i + 1);

        // PaymentMethod Spinner:
        if (receipt.getPaymentMethod() > 0) {
            int pos = 0;
            switch (receipt.getPaymentMethod()) {
                case 30:
                    pos = 1;
                    break;
                case 60:
                    pos = 2;
                    break;
                case 90:
                    pos = 3;
                    break;
                case 120:
                    pos = 4;
                    break;
            }
            spnrPaymentMethod.setSelection(pos);
        }

        // EnumPaymentType Spinner:
        spnrPaymentType.setSelection(receipt.getPaymentType());
    }

    /* ------------------------------------------------------------------------------------------- */
    private void setSelectionCustomerSpinner(String customerIdNum) {
        int i = 0;
        while (customerList != null && i < customerList.size()
                && !(customerIdNum.equals(customerList.get(i).getIdNum()))) {
            i++;
        }
        if (i < customerList.size())
            spnrCustomer.setSelection(i + 1);//1- for prompt
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
            receipt.setPaymentMethod(paymentMethod);
        }

        if (!spnrPaymentType.getSelectedItem().equals("-בחר צורת תשלום-")) {
            int pos = spnrPaymentType.getSelectedItemPosition();
            receipt.setPaymentType(pos);
        }

    }
}
