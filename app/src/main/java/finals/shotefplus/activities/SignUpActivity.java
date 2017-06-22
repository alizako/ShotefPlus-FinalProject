package finals.shotefplus.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;

import finals.shotefplus.DataAccessLayer.FirebaseHandler;
import finals.shotefplus.R;
import finals.shotefplus.objects.UserProfile;

public class SignUpActivity extends AppCompatActivity {

    Button btnSignUp;
    private FirebaseAuth firebaseAuth;//defining firebaseauth object
    private static final String PREFS_NAME = "SIGNING-IN";
    private static final int PSWD_LEN = 6;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        btnSignUp = (Button) findViewById(R.id.btnUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {

        //getting email and password from edit texts
        final String email = ((EditText) findViewById(R.id.etEmail)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.etPassword)).getText().toString().trim();
        final String bsns = ((EditText) findViewById(R.id.etBsns)).getText().toString().trim();
        final String tik = ((EditText) findViewById(R.id.etTik)).getText().toString().trim();
        final String phone = ((EditText) findViewById(R.id.etPhone)).getText().toString().trim();

        CheckBox cbRmmbrUser = (CheckBox) findViewById(R.id.cbRmmbr);

        if (cbRmmbrUser.isChecked()) {
            SharedPreferences sharedPref = SignUpActivity.this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("mail", email);
            editor.putString("password", password);
            editor.putBoolean("isCheckedToSave", true);
            editor.apply();
        }

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter  email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < PSWD_LEN) {
            Toast.makeText(this, "Please enter correct password", Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

       /* progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();*/

        //creating a new user

        try {
            dialog = ProgressDialog.show(SignUpActivity.this,
                    "מבצע רישום",
                    "אנא המתן בזמן הרישום..",
                    true);


            firebaseAuth.createUserWithEmailAndPassword(email, password)//,bsns,tik,phone)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //checking if success
                            if (task.isSuccessful()) {
                                //display some message here
                                Toast.makeText(SignUpActivity.this, R.string.signUpToast, Toast.LENGTH_LONG).show();

                                addProfileToFireBase(bsns, tik, phone, email);

                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                //FirebaseCrash.report(exce);//report(new Exception(Exc));
                                //display some message here
                                Toast.makeText(SignUpActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                            }
                            //  progressDialog.dismiss();
                        }
                    });

        } catch (Exception e) {
            dialog.dismiss();
            Toast.makeText(SignUpActivity.this,
                    "" + e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void addProfileToFireBase(String bsns, String tik, String phone, String email) {
        UserProfile up = new UserProfile(bsns, tik, phone, email);

        //FirebaseHandler firebaseHandler= new FirebaseHandler(firebaseAuth.getCurrentUser().getUid());
        FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid()).insertUserProfile(up);

    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        SignUpActivity.this.finish();
    }
}
