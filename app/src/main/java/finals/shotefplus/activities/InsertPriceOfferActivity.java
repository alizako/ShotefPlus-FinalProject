package finals.shotefplus.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.images.ImageManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import finals.shotefplus.DataAccessLayer.FirebaseHandler;
import finals.shotefplus.R;
import finals.shotefplus.adapters.PriceOfferListAdapter;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.PriceOffer;
import finals.shotefplus.objects.Receipt;
import finals.shotefplus.objects.Work;


public class InsertPriceOfferActivity extends AppCompatActivity {
    static final int REQ_ADD_CUSTOMER = 1;
    private FirebaseAuth firebaseAuth;
    Button btnAdd, btnSendPdf;
    private Customer customer;
    private Customer customerById;
    PriceOffer priceOffer;
    Work workPO;
    String currentKey;
    EditText etDate, etCustomer, etLocation, etDetails, etQuantity, etSum;
    CheckBox cbSumMaam;
    Spinner spnrCustomer;
    ImageButton imgBtnCustomer;
    boolean isUpdateMode = false;
    String customerAddedIdNum;
    private ArrayList<Customer> customerList;
    private ArrayList<String> strTitleList;
    private ArrayAdapter<String> spinnerAdapterCustomer;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_price_offer);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //dataRefHandling();

        btnSendPdf = (Button) findViewById(R.id.btnSendPdf);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        etDate = (EditText) findViewById(R.id.etDate);
        etDate.setShowSoftInputOnFocus(false);
        // etCustomer = (EditText) findViewById(R.id.etCustomer);
        imgBtnCustomer = (ImageButton) findViewById(R.id.imgBtnCustomer);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etDetails = (EditText) findViewById(R.id.etDetails);
        etQuantity = (EditText) findViewById(R.id.etQuantity);
        etSum = (EditText) findViewById(R.id.etSum);
        cbSumMaam = (CheckBox) findViewById(R.id.cbSumMaam);

        spnrCustomer = (Spinner) findViewById(R.id.spnrCustomer);

        initSpinnersFromDB();
        /*setEvents();

        Intent intent = getIntent();
        // String priceOfferId = "";
        String priceOfferId = intent.getStringExtra("PriceOfferId");
        if (priceOfferId != null)//has value
        {
            isUpdateMode = true;
            currentKey = priceOfferId;
            //  Toast.makeText(getBaseContext(), priceOfferId, Toast.LENGTH_LONG).show();
            setValuesToFields(priceOfferId);
        }*/


    }


    protected void initOnStart() {
        // super.onStart();
        setEvents();

        Intent intent = getIntent();
        // String priceOfferId = "";
        String priceOfferId = intent.getStringExtra(getString(R.string.PriceOfferId));
        if (priceOfferId != null)//has value
        {
            isUpdateMode = true;
            currentKey = priceOfferId;
            //  Toast.makeText(getBaseContext(), priceOfferId, Toast.LENGTH_LONG).show();
            setValuesToFields(priceOfferId);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_ADD_CUSTOMER) {
            if (resultCode == Activity.RESULT_OK) {
                customerAddedIdNum = data.getStringExtra(getString(R.string.customerAddedIdNum));
                setSpinners(customerAddedIdNum);//set selection to added customer
            } else {
                Toast.makeText(getBaseContext(),
                        getString(R.string.errorMsg)+ " " +getString(R.string.errorPOMsg),
                        Toast.LENGTH_LONG).show();
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
        if (etQuantity.getText().toString().equals("")) flagErr = true;
        if (etSum.getText().toString().equals("")) flagErr = true;// etDetails.setText("0");
        if (etDetails.getText().toString().equals("")) flagErr = true;

        if (flagErr) {
            Toast.makeText(getBaseContext(),
                    getString(R.string.requiredFieldsPO),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /* ------------------------------------------------------------------------------------------- */
    private void setSpinners(String customerIdNum) {

        // Customer Spinner:
        int i = 0;
        while (customerList != null && i < customerList.size()
                && !(customerIdNum.equals(customerList.get(i).getIdNum()))) {
            i++;
        }
        if (i < customerList.size())
            spnrCustomer.setSelection(i + 1);
    }

    /* ------------------------------------------------------------------------------------------- */
    private void setSpinnerValuesToPriceOffer() {
        //Title of spinner in position 0
        if (!spnrCustomer.getSelectedItem().equals(getString(R.string.promptCustomer))) {
            int pos = spnrCustomer.getSelectedItemPosition();
            priceOffer.setCustomerIdNum(customerList.get(pos - 1).getIdNum());
        }
    }

    private void sendPriceOfferByMail() {
        String subject = getString(R.string.insertPriceOffer);
        String body = "";
        body += "תאריך: " + etDate.getText() + "\n";
        body += "מיקום: " + etLocation.getText() + "\n";
        body += "כמות: " + etQuantity.getText() + "\n";
        body += "סכום: " + etSum.getText() + "\n";
        body += "פרטים נוספים: " + etDetails.getText() + "\n\n";

        Customer customer = getcustomer();

        if (customer != null) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{customer.getEmail()});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject); //"retype subject");
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);//"insert body\n");
            try {
                startActivity(Intent.createChooser(emailIntent, getString(R.string.chooseClientEmail)));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getBaseContext(),
                        getString(R.string.noEmailClients),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Customer getcustomer() {
        int i = 0;
        while (customerList != null && i < customerList.size()
                && !(spnrCustomer.getSelectedItem().toString().equals(customerList.get(i).getName()))) {
            i++;
        }
        if (i > 0 && i < customerList.size())
            return customerList.get(i);

        return null;
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
                DateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormatFull));
                final String dueDate = dateFormat.format(calendar.getTime());
                etDate.setText(dueDate);
            }
        };

        etDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new DatePickerDialog(InsertPriceOfferActivity.this, date,
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
                    addPriceOfferToFireBase(false);
            }
        });

        //this version doesn't use pdf, but email
        btnSendPdf.setOnClickListener(new View.OnClickListener() {
            //open gmail to edit the msg before sending
            @Override
            public void onClick(View v) {
                //TODO send pdf to customer

                if (!isUpdateMode)//in case sending pdf pre-saving new record
                    priceOffer = new PriceOffer();

                if (isFieldsValidate()) {
                    if (spnrCustomer.getSelectedItemPosition() > 0) {
                        sendPriceOfferByMail();
                        addPriceOfferToFireBase(true);
                    } else
                        Toast.makeText(getBaseContext(), getString(R.string.customerNotSet), Toast.LENGTH_SHORT).show();
                }
//                Toast.makeText(v.getContext(), "הצעת מחיר נשלחה ללקוח במייל", Toast.LENGTH_LONG).show();
                //Toast.makeText(v.getContext(), "כרגע לא בשימוש", Toast.LENGTH_LONG).show();
            }
        });

        //add new customer
        imgBtnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InsertPriceOfferActivity.this, InsertCustomerActivity.class);
                startActivityForResult(intent, REQ_ADD_CUSTOMER);
            }
        });
    }


    /**********************************************************************************
     * FireBase
     **********************************************************************************/
    //get current price offer from firebase
    private void setValuesToFields(final String priceOfferId) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseLink) +
                        firebaseAuth.getCurrentUser().getUid() +
                        getString(R.string.priceOfferLink)
                        + priceOfferId);

        dbRef/*.orderByChild("idNum")
                .startAt(priceOfferId).endAt(priceOfferId)*/
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        //    for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                        try {
                            priceOffer = new PriceOffer();
                            priceOffer = snapshot.getValue(PriceOffer.class);

                            btnAdd.setText(getString(R.string.update));

                            etDate.setText(priceOffer.dueDateToString());
                            etLocation.setText(priceOffer.getLocation());

                            // Customer customer = priceOffer.getCustomerByID();
                            //  etCustomer.setText(customer.getName());

                            etDetails.setText(priceOffer.getWorkDetails());
                            etQuantity.setText(Long.toString(priceOffer.getQuantity()));
                            etSum.setText(Double.toString(priceOffer.getSumPayment()));
                            cbSumMaam.setChecked(priceOffer.isSumPaymentMaam());

                            if (priceOffer.getCustomerIdNum() != null && priceOffer.getCustomerIdNum().length() > 0)
                                setSpinners(priceOffer.getCustomerIdNum());

                        } catch (Exception ex) {
                            Toast.makeText(getBaseContext(),
                                    getString(R.string.errorMsg) + ex.toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                        // }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Toast.makeText(getBaseContext(),
                                getString(R.string.errorMsg) + firebaseError.getMessage(),
                                Toast.LENGTH_LONG).show();
                        // dialog.dismiss();
                    }
                });

        // FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid()).getPriceOffer(priceOfferId);
    }

    /* ------------------------------------------------------------------------------------------- */
    private void addPriceOfferToFireBase(boolean isSent) {

        try {
            if (!isUpdateMode)//new price offer
                priceOffer = new PriceOffer();

            priceOffer.setDueDate(etDate.getText().toString());
            priceOffer.setLocation(etLocation.getText().toString());
            priceOffer.setCustomerIdNum(customerAddedIdNum);
            priceOffer.setWorkDetails(etDetails.getText().toString());
            priceOffer.setQuantity(Long.parseLong(etQuantity.getText().toString()));
            priceOffer.setSumPayment(Double.parseDouble(etSum.getText().toString()));
            priceOffer.setSumPaymentMaam(cbSumMaam.isChecked());

            if (isSent)
                priceOffer.setPriceOfferSent(true);

            setSpinnerValuesToPriceOffer();

            if (isUpdateMode) { //update current price offer
                //priceOffer.setIdNum(currentKey);
                FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid()).
                        updatePriceOffer(priceOffer, currentKey);

                //check if this p.o has a work attached and update the work as well:
                if (priceOffer.isPriceOfferApproved()) {
                    updateWorkInDB();
                }

                //  Toast.makeText(v.getContext(), "הצעת מחיר התעדכנה", Toast.LENGTH_LONG).show();
            } else {//add new price offer

                DateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormatFull));
                Date date = new Date();
                priceOffer.setDateInsertion(dateFormat.format(date));

                currentKey = FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                        .insertPriceOffer(priceOffer);
                Toast.makeText(getBaseContext(),
                        getString(R.string.priceOfferAdded)
                        , Toast.LENGTH_LONG).show();
            }

            finish(); //back to list
        } catch (Exception ex) {
            Toast.makeText(getBaseContext(),
                    getString(R.string.errorMsg) + ex.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    /* ------------------------------------------------------------------------------------------- */
    // in case this price offer is already saved as a Work- needs to update work as well
    private void updateWorkInDB() {

        final String priceOfferId = priceOffer.getIdNum();

        final DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseLink) +
                firebaseAuth.getCurrentUser().getUid() +
                getString(R.string.worksLink));

        dbRef.orderByChild(getString(R.string.idNum))
                // .startAt(priceOfferId).endAt(priceOfferId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    public void onDataChange(DataSnapshot snapshot) {

                        final long[] pendingLoadCount = {snapshot.getChildrenCount()};

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                            pendingLoadCount[0] = pendingLoadCount[0] - 1;

                            try {
                                // Work workPO = new Work();
                                workPO = postSnapshot.getValue(Work.class);

                                if (workPO.getPriceOfferIdNum().equals(priceOfferId)) {
                                    //properties from price offer
                                    workPO.setDueDate(priceOffer.getDueDate());
                                    workPO.setLocation(priceOffer.getLocation());
                                    workPO.setCustomerIdNum(priceOffer.getCustomerIdNum());
                                    workPO.setWorkDetails(priceOffer.getWorkDetails());
                                    workPO.setQuantity(priceOffer.getQuantity());
                                    workPO.setSumPayment(priceOffer.getSumPayment());
                                    workPO.setSumPaymentMaam(priceOffer.isSumPaymentMaam());
                                }

                            } catch (Exception ex) {
                                Toast.makeText(getBaseContext(),
                                        getString(R.string.errorMsg)+ ex.toString(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                        if (pendingLoadCount[0] == 0) {

                            try {
                                //update price offer
                                final DatabaseReference currentDdbRef = FirebaseDatabase.getInstance()
                                        .getReferenceFromUrl(getString(R.string.firebaseLink) +
                                                firebaseAuth.getCurrentUser().getUid() +
                                                getString(R.string.worksLink) +
                                                workPO.getIdNum());

                                currentDdbRef.setValue(workPO);

                                Toast.makeText(getBaseContext(),
                                        getString(R.string.priceOfferUpdated)
                                        , Toast.LENGTH_LONG).show();

                            } catch (Exception ex) {
                                Toast.makeText(getBaseContext(),
                                        getString(R.string.errorMsg)+ ex.toString(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Toast.makeText(getBaseContext(),
                                getString(R.string.errorMsg) + firebaseError.getMessage(),
                                Toast.LENGTH_LONG).show();
                        // dialog.dismiss();
                    }
                });
    }

    /* ------------------------------------------------------------------------------------------- */
    //get all customers from firebase- set in spinner through adapter
    private void initSpinnersFromDB() {
        dialog = ProgressDialog.show(InsertPriceOfferActivity.this,
                "",
                getString(R.string.loadMsg),
                true);
        //       spnrCustomer
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseLink) +
                        firebaseAuth.getCurrentUser().getUid() +
                        getString(R.string.customersLink));

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                customerList = new ArrayList<Customer>();
                strTitleList = new ArrayList<String>();
                strTitleList.add(getString(R.string.promptCustomer));

                final long[] pendingLoadCount = {snapshot.getChildrenCount()};

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    pendingLoadCount[0] = pendingLoadCount[0] - 1;
                    try {
                        Customer customer = new Customer();
                        customer = postSnapshot.getValue(Customer.class);
                        customerList.add(customer);
                        strTitleList.add(customer.getName());

                        spinnerAdapterCustomer = new ArrayAdapter<String>(InsertPriceOfferActivity.this, android.R.layout.simple_spinner_item, strTitleList);
                        spinnerAdapterCustomer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnrCustomer.setAdapter(spinnerAdapterCustomer);

                    } catch (Exception ex) {
                        Toast.makeText(getBaseContext(),
                                getString(R.string.errorMsg)+ ex.toString(),
                                Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
                if (pendingLoadCount[0] == 0) {
                    initOnStart();
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(getBaseContext(),
                        getString(R.string.errorMsg)+ firebaseError.getMessage(),
                        Toast.LENGTH_LONG).show();

            }
        });
    }

}
