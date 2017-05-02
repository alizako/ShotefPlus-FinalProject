package finals.shotefplus.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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

import finals.shotefplus.DataAccessLayer.FirebaseHandler;
import finals.shotefplus.R;
import finals.shotefplus.adapters.PriceOfferListAdapter;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.PriceOffer;


public class InsertPriceOfferActivity extends AppCompatActivity {
    static final int REQ_ADD_CUSTOMER = 1;
    private FirebaseAuth firebaseAuth;
    Button btnAdd, btnSendPdf;
    private Customer customer;
    PriceOffer priceOffer;
    String currentKey;
    EditText etDate, etCustomer, etLocation, etDetails, etQuantity, etSum;
    CheckBox cbSumMaam;
    ImageButton imgBtnCustomer;
    boolean isUpdateMode = false;
    //private Firebase mRef;

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
        etCustomer = (EditText) findViewById(R.id.etCustomer);
        imgBtnCustomer = (ImageButton) findViewById(R.id.imgBtnCustomer);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etDetails = (EditText) findViewById(R.id.etDetails);
        etQuantity = (EditText) findViewById(R.id.etQuantity);
        etSum = (EditText) findViewById(R.id.etSum);
        cbSumMaam = (CheckBox) findViewById(R.id.cbSumMaam);

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

    private void setValuesToFields(final String priceOfferId) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/PriceOffers/");
        dbRef.orderByChild("idNum").startAt(priceOfferId).endAt(priceOfferId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                            try {
                                priceOffer = new PriceOffer();
                                priceOffer = postSnapshot.getValue(PriceOffer.class);
                                etDate.setText(priceOffer.dueDateToString());
                                etLocation.setText(priceOffer.getLocation());
                                etCustomer.setText(priceOffer.getCustomer().getName());
                                etDetails.setText(priceOffer.getWorkDetails());
                                etQuantity.setText(Long.toString(priceOffer.getQuantity()));
                                etSum.setText(Double.toString(priceOffer.getSumPayment()));
                                cbSumMaam.setChecked(priceOffer.isSumPaymentMaam());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_ADD_CUSTOMER) {
            if (resultCode == Activity.RESULT_OK) {
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
        if (etDate.getText().toString().equals("")) flagErr = true; //etDate.setText(" ");
        /*if (etLocation.getText().toString().equals("")) etLocation.setText(" ");
        if (etCustomer.getText().toString().equals("")) etCustomer.setText(" ");
        if (etDetails.getText().toString().equals("")) etDetails.setText(" ");*/
        if (etQuantity.getText().toString().equals("")) etQuantity.setText("0");
        if (etSum.getText().toString().equals("")) flagErr = true;// etDetails.setText("0");
        if (flagErr) {
            Toast.makeText(getBaseContext(), "יש למלא שדות חובה: תאריך, סכום", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
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
                if (priceOffer==null)//in case sending pdf pre-saving new record
                    priceOffer=new PriceOffer();
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
    private void addPriceOfferToFireBase(View v) {

        try {

            //TODO: call insertCustomer instead code below
            customer = new Customer();
            customer.setName(((EditText) findViewById(R.id.etCustomer)).getText().toString().trim());

            //  priceOffer = new PriceOffer();
            priceOffer.setDueDate(etDate.getText().toString());
            priceOffer.setLocation(etLocation.getText().toString());
            priceOffer.setCustomer(customer);
            priceOffer.setWorkDetails(etDetails.getText().toString());
            priceOffer.setQuantity(Long.parseLong(etQuantity.getText().toString()));
            priceOffer.setSumPayment(Double.parseDouble(etSum.getText().toString()));
            priceOffer.setSumPaymentMaam(cbSumMaam.isChecked());


            if (isUpdateMode) {
                //priceOffer.setIdNum(currentKey);
                FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid()).
                        updatePriceOffer(priceOffer, currentKey);

                Toast.makeText(v.getContext(), "הצעת מחיר התעדכנה", Toast.LENGTH_LONG).show();
            } else {//add new price offer
                currentKey = FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                        .insertPriceOffer(priceOffer);
                Toast.makeText(v.getContext(), "הצעת מחיר התווספה", Toast.LENGTH_LONG).show();
            }

            finish(); //back to list
        } catch (Exception ex) {
            Toast.makeText(v.getContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /*private void dataRefHandling() {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/"+
                        firebaseAuth.getCurrentUser().getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    //Getting the data from snapshot
                    //PriceOffer priceOffer = postSnapshot.getValue(PriceOffer.class);

                    //Displaying it on textview
                    // textViewPersons.setText(string);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(getBaseContext(), "ERROR: "  + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                //String Id = (String) dataSnapshot.child(firebaseAuth.getCurrentUser().getUid()).getValue();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                *//*Post removedPost = dataSnapshot.getValue(Post.class);
                System.out.println("The blog post titled " + removedPost.title + " has been deleted");*//*
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }*/
}
