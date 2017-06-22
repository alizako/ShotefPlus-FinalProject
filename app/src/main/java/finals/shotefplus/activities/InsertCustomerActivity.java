package finals.shotefplus.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import finals.shotefplus.DataAccessLayer.FirebaseHandler;
import finals.shotefplus.R;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.Expense;
import finals.shotefplus.objects.PriceOffer;
import finals.shotefplus.objects.Work;

public class InsertCustomerActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    Button btnAdd;
    EditText etName, etContactName, etPhone, etEmail, etAddress;
    boolean isUpdateMode = false;
    RadioButton rbBsns, rbPrivate;
    String currentKey;
    Customer customer;
    static final String ESEK_TYPE ="1";
    static final String PRIVTE_TYPE ="2";
    static final String NONE_TYPE ="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_customer);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        etName = (EditText) findViewById(R.id.etName);
        etContactName = (EditText) findViewById(R.id.etContactName);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etAddress = (EditText) findViewById(R.id.etAddress);
        rbBsns = (RadioButton) findViewById(R.id.rbBsns);
        rbPrivate = (RadioButton) findViewById(R.id.rbPrivate);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        Intent intent = getIntent();

        String customerId = intent.getStringExtra(getString(R.string.customerIdNum));
        if (customerId != null)//has value
        {
            isUpdateMode = true;
            currentKey = customerId;
          //  Toast.makeText(getBaseContext(), customerId, Toast.LENGTH_LONG).show();
            setValuesToFields(customerId);
        }

        setEvents();
    }


    /**********************************************************************************
     * Events
     **********************************************************************************/

    private void setEvents() {

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFieldsValidate())
                    addCustomerToFireBase(v);
                /* setResult(Activity.RESULT_OK);
                finish();*/
                // Toast.makeText(v.getContext(), "הוצאה התווספה", Toast.LENGTH_LONG).show();
            }
        });


    }

    /**********************************************************************************
     * FireBase
     **********************************************************************************/
    /*
    * exist customer- set values
    * get id of chosen row in list to look for it in firebase
    */
    private void setValuesToFields(final String customerId) {

        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseLink) +
                firebaseAuth.getCurrentUser().getUid() +
                getString(R.string.customersLink));

        dbRef.orderByChild(getString(R.string.idNum))
                .startAt(customerId).endAt(customerId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                            try {
                                customer = new Customer();
                                customer = postSnapshot.getValue(Customer.class);

                                btnAdd.setText(getString(R.string.update));

                                etName.setText(customer.getName());
                                etContactName.setText(customer.getCustomerContactName());
                                etPhone.setText(customer.getPhoneNum());
                                etEmail.setText(customer.getEmail());
                                etAddress.setText(customer.getAdrs());
                                if (customer.getCustomerType().equals(ESEK_TYPE)) { //1
                                    rbBsns.setChecked(true);
                                    rbPrivate.setChecked(false);
                                }
                                else if (customer.getCustomerType().equals(PRIVTE_TYPE)) { //2
                                    rbBsns.setChecked(false);
                                    rbPrivate.setChecked(true);
                                }
                                else if (customer.getCustomerType().equals(NONE_TYPE)) { //0
                                    rbBsns.setChecked(false);
                                    rbPrivate.setChecked(false);
                                }
                            } catch (Exception ex) {
                                Toast.makeText(getBaseContext(),
                                        getString(R.string.errorMsg) + ex.toString(),
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

// update or insert customer to firebase
    private void addCustomerToFireBase(View v) {

        try {

            if (! isUpdateMode)
                customer = new Customer();

            customer.setName(etName.getText().toString());
            customer.setCustomerContactName(etContactName.getText().toString());
            customer.setPhoneNum(etPhone.getText().toString());
            customer.setEmail(etEmail.getText().toString());
            customer.setAdrs(etAddress.getText().toString());
            if (rbBsns.isChecked()) //1
                customer.setCustomerType(ESEK_TYPE);
            else if (rbPrivate.isChecked()) //2
                customer.setCustomerType(PRIVTE_TYPE);
            else //0
                customer.setCustomerType(NONE_TYPE);

            if (isUpdateMode) {
              //  customer.setIdNum(currentKey);
                FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                        .updateCustomer(customer, currentKey);

                Toast.makeText(v.getContext(),
                        getString(R.string.customerUpdated),
                        Toast.LENGTH_LONG).show();
            } else {//add new Customer

                DateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormatFull));
                Date date = new Date();
                customer.setDateInsertion(dateFormat.format(date));

                currentKey = FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                        .insertCustomer(customer);
                Intent intent= new Intent();
                intent.putExtra(getString(R.string.customerAddedIdNum), currentKey);
                intent.putExtra(getString(R.string.customerAddedName), customer.getName());
                setResult(RESULT_OK, intent);

                Toast.makeText(v.getContext(),
                        getString(R.string.customerAdded),
                        Toast.LENGTH_LONG).show();
                finish();
            }

            finish(); //back to list
        } catch (Exception ex) {
            Toast.makeText(v.getContext(),
                    getString(R.string.errorMsg) + ex.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**********************************************************************************
     * private functions
     **********************************************************************************/

    private boolean isFieldsValidate() {

        boolean flagErr = false;
        if (etName.getText().toString().equals(""))
            flagErr = true;

        if (etPhone.getText().toString().equals("")) flagErr = true;

        if (etEmail.getText().toString().equals("")) flagErr = true;

        if (flagErr) {
            Toast.makeText(getBaseContext(),
                    getString(R.string.requiredFieldsCustomers),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
