package finals.shotefplus.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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


public class InsertPriceOfferActivity extends AppCompatActivity {
    static final int REQ_ADD_CUSTOMER = 1;
    private FirebaseAuth firebaseAuth;
    Button btnAdd, btnSendPdf;
    private Customer customer;
    private Customer customerById;
    PriceOffer priceOffer;
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
        setEvents();

        Intent intent = getIntent();
        // String priceOfferId = "";
        String priceOfferId = intent.getStringExtra("PriceOfferId");
        if (priceOfferId != null)//has value
        {
            isUpdateMode = true;
            currentKey = priceOfferId;
            Toast.makeText(getBaseContext(), priceOfferId, Toast.LENGTH_LONG).show();
            setValuesToFields(priceOfferId);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_ADD_CUSTOMER) {
            if (resultCode == Activity.RESULT_OK) {
                customerAddedIdNum = data.getStringExtra("customerAddedIdNum");
                etCustomer.setText(data.getStringExtra("customerName"));
            } else {
                Toast.makeText(getBaseContext(), "ERROR: adding customer", Toast.LENGTH_LONG).show();
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
        if (etQuantity.getText().toString().equals("")) etQuantity.setText("0");
        if (etSum.getText().toString().equals("")) flagErr = true;// etDetails.setText("0");
        if(etDetails.getText().toString().equals("")) flagErr = true;

        if (flagErr) {
            Toast.makeText(getBaseContext(), "יש למלא שדות חובה: תאריך, סכום, פרטי עסקה", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    /* ------------------------------------------------------------------------------------------- */
    private void setSpinners(PriceOffer priceOffer) {

        // Customer Spinner:
        int i = 0;
        while (customerList != null && i < customerList.size()
                && !(priceOffer.getCustomerIdNum().equals(customerList.get(i).getIdNum()))) {
            i++;
        }
        if (i < customerList.size())
            spnrCustomer.setSelection(i + 1);
    }

    /* ------------------------------------------------------------------------------------------- */
    private void setSpinnerValuesToPriceOffer() {
        //Title of spinner in position 0
        if (!spnrCustomer.getSelectedItem().equals("-בחר לקוח-")) {
            int pos = spnrCustomer.getSelectedItemPosition();
            priceOffer.setCustomerIdNum(customerList.get(pos - 1).getIdNum());
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
                    addPriceOfferToFireBase(v);

            }
        });

        btnSendPdf.setOnClickListener(new View.OnClickListener() {
            //open gmail to edit the msg before sending
            @Override
            public void onClick(View v) {
                //TODO send pdf to customer
               /* if (!isUpdateMode) {//will update db only in add mode. in update mode clicking on btn Update will update the db
                    FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                            .updatePriceOffer(priceOffer, currentKey);
                }*/
                if (priceOffer == null)//in case sending pdf pre-saving new record
                    priceOffer = new PriceOffer();
                priceOffer.setPriceOfferSent(true);
                Toast.makeText(v.getContext(), "הצעת מחיר נשלחה ללקוח במייל", Toast.LENGTH_LONG).show();
            }
        });

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
    private void setValuesToFields(final String priceOfferId) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/PriceOffers/");

        dbRef.orderByChild("idNum")
                .startAt(priceOfferId).endAt(priceOfferId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                            try {
                                priceOffer = new PriceOffer();
                                priceOffer = postSnapshot.getValue(PriceOffer.class);
                                etDate.setText(priceOffer.dueDateToString());
                                etLocation.setText(priceOffer.getLocation());

                                // Customer customer = priceOffer.getCustomerByID();
                                //  etCustomer.setText(customer.getName());

                                etDetails.setText(priceOffer.getWorkDetails());
                                etQuantity.setText(Long.toString(priceOffer.getQuantity()));
                                etSum.setText(Double.toString(priceOffer.getSumPayment()));
                                cbSumMaam.setChecked(priceOffer.isSumPaymentMaam());

                                setSpinners(priceOffer);

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

        // FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid()).getPriceOffer(priceOfferId);
    }

    /* ------------------------------------------------------------------------------------------- */
    private void addPriceOfferToFireBase(View v) {

        try {
            if (!isUpdateMode)
                priceOffer = new PriceOffer();

            priceOffer.setDueDate(etDate.getText().toString());
            priceOffer.setLocation(etLocation.getText().toString());
            priceOffer.setCustomerIdNum(customerAddedIdNum);
            priceOffer.setWorkDetails(etDetails.getText().toString());
            priceOffer.setQuantity(Long.parseLong(etQuantity.getText().toString()));
            priceOffer.setSumPayment(Double.parseDouble(etSum.getText().toString()));
            priceOffer.setSumPaymentMaam(cbSumMaam.isChecked());

            setSpinnerValuesToPriceOffer();

            if (isUpdateMode) {
                //priceOffer.setIdNum(currentKey);
                FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid()).
                        updatePriceOffer(priceOffer, currentKey);

                Toast.makeText(v.getContext(), "הצעת מחיר התעדכנה", Toast.LENGTH_LONG).show();
            } else {//add new price offer

                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                priceOffer.setDateInsertion(dateFormat.format(date));

                currentKey = FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                        .insertPriceOffer(priceOffer);
                Toast.makeText(v.getContext(), "הצעת מחיר התווספה", Toast.LENGTH_LONG).show();
            }

            finish(); //back to list
        } catch (Exception ex) {
            Toast.makeText(v.getContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /* ------------------------------------------------------------------------------------------- */
    private void initSpinnersFromDB() {
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

                        spinnerAdapterCustomer = new ArrayAdapter<String>(InsertPriceOfferActivity.this, android.R.layout.simple_spinner_item, strTitleList);
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

}
