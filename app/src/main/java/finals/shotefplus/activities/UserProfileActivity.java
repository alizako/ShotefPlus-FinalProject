package finals.shotefplus.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import finals.shotefplus.DataAccessLayer.FirebaseHandler;
import finals.shotefplus.R;
import finals.shotefplus.objects.UserProfile;


public class UserProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    DatabaseReference dbRef;
    EditText bsns, tik, phone;
    Button btnUpdate, btnCancel;
    UserProfile userProfile;
    static final int RESULT_EXP = 2;
    boolean isTextChanged = false;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        bsns = ((EditText) findViewById(R.id.etBsns));
        tik = ((EditText) findViewById(R.id.etTik));
        phone = ((EditText) findViewById(R.id.etPhone));

        firebaseAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/UserProfile/");

        userProfile = new UserProfile();
        setValuesToFields();

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        setEvents();

        dialog = ProgressDialog.show(UserProfileActivity.this,
                "",
                "אנא המתן בזמן טעינת הנתונים..",
                true);
    }

    private void setEvents() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isTextChanged) {
                    userProfile.setBusinessName(bsns.getText().toString());
                    userProfile.setTik(tik.getText().toString());
                    userProfile.setPhone(phone.getText().toString());

                    try {
                        FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid()).updateUserProfile(userProfile);
                        setResult(RESULT_OK, new Intent());
                        finish();
                    } catch (Exception ex) {
                        setResult(RESULT_EXP, new Intent());
                        finish();
                    }
                } else {//nothing to update
                    setResult(Activity.RESULT_CANCELED, new Intent());
                    finish();
                }

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED, new Intent());
                finish();
            }
        });

        bsns.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                isTextChanged = true;
            }
        });

        tik.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                isTextChanged = true;
            }
        });

        phone.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                isTextChanged = true;
            }
        });

    }


    private void setValuesToFields() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {


                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        //  intent.putExtra(postSnapshot.getKey().toString(), postSnapshot.getValue(String.class));
                        if (postSnapshot.getKey().toString().equals("businessName")) {
                            userProfile.setBusinessName(postSnapshot.getValue(String.class));
                            bsns.setText(postSnapshot.getValue(String.class));
                        }
                        if (postSnapshot.getKey().toString().equals("tik")) {
                            userProfile.setTik(postSnapshot.getValue(String.class));
                            tik.setText(postSnapshot.getValue(String.class));
                        }
                        if (postSnapshot.getKey().toString().equals("phone")) {
                            userProfile.setPhone(postSnapshot.getValue(String.class));
                            phone.setText(postSnapshot.getValue(String.class));
                        }
                        if (postSnapshot.getKey().toString().equals("email")) {
                            userProfile.setEmail(postSnapshot.getValue(String.class));
                        }
                        isTextChanged=false;//only when the user types it counts

                    } catch (Exception ex) {
                        Toast.makeText(getBaseContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
