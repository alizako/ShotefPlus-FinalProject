package finals.shotefplus.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import finals.shotefplus.R;

public class ChangeRegistrationInfoActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SIGNING-IN";
    static final int RESULT_EXP = 2;
    String oldPassword, newPassword, newEmail;
    Button btnUpdate, btnCancel;
    private FirebaseAuth firebaseAuth;//defining firebaseauth object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_registration_info);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        setEvents();
    }

    private void setEvents() {

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED, new Intent());
                finish();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newPassword = ((EditText) findViewById(R.id.etNewPassword)).getText().toString().trim();
                oldPassword = ((EditText) findViewById(R.id.etOldPassword)).getText().toString().trim();
                newEmail = ((EditText) findViewById(R.id.etEmail)).getText().toString().trim();

                //if no new password and no new email OR no old password
                if ((TextUtils.isEmpty(oldPassword) || oldPassword.length() < 6)) {
                    Toast.makeText(ChangeRegistrationInfoActivity.this,
                            "Please enter your old password",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if ((TextUtils.isEmpty(newPassword) || newPassword.length() < 6)
                        && TextUtils.isEmpty(newEmail)) {
                    Toast.makeText(ChangeRegistrationInfoActivity.this,
                            "Please enter correct password",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                final FirebaseUser user = firebaseAuth.getCurrentUser();
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        //in case the user asked to save his password+mail-
                        // need to update it when he asks to change the credential:
                        final SharedPreferences sharedPref = ChangeRegistrationInfoActivity.this.
                                getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        //  final SharedPreferences.Editor editor = sharedPref.edit();


                        if (task.isSuccessful()) {

                            if (newPassword.length() > 0) {
                                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            //check if user asked to save his credential and replace it
                                            if (sharedPref.contains("password")) {
                                                SharedPreferences.Editor editor = sharedPref.edit();
                                                editor.putString("password", newPassword);
                                                editor.commit();//update
                                            }
                                            //no need to update mail (that coded ahead)
                                            if (TextUtils.isEmpty(newEmail)) {
                                                setResult(RESULT_OK, new Intent());
                                                finish();
                                            }
                                        } else {
                                            setResult(RESULT_EXP, new Intent());
                                            finish();
                                        }
                                    }
                                });
                            }
                            if (newEmail.length() > 0) {
                                user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //check if user asked to save his credential and replace it
                                            if (sharedPref.contains("mail")) {
                                                SharedPreferences.Editor editor = sharedPref.edit();
                                                editor.putString("mail", newEmail);
                                                editor.commit();//update
                                            }
                                            setResult(RESULT_OK, new Intent());
                                            finish();
                                        } else {
                                            setResult(RESULT_EXP, new Intent());
                                            finish();
                                        }
                                    }
                                });
                            }
                        } else {
                            setResult(RESULT_EXP, new Intent());
                            finish();
                        }
                    }
                });
            }
        });

    }
}
