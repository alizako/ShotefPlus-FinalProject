package finals.shotefplus.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import finals.shotefplus.R;

public class SignInActivity extends AppCompatActivity {

    Button btnSignIn;
    private FirebaseAuth firebaseAuth;//defining firebaseauth object
    private static final String PREFS_NAME = "SIGNING-IN";
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.btnIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //callMainActivity();
                try {
                    dialog = ProgressDialog.show(SignInActivity.this,
                            getString(R.string.connecting),
                            getString(R.string.waitConnecting),
                            true);
                    signInUser();
                } catch (Exception e) {
                    dialog.dismiss();
                    Toast.makeText(SignInActivity.this,
                            "" + e.toString(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void signInUser() {

        //getting email and password from edit texts
        String email = ((EditText) findViewById(R.id.etEmail)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.etPassword)).getText().toString().trim();
        CheckBox cbRmmbrUser = (CheckBox) findViewById(R.id.cbRmmbr);

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, getString(R.string.setEmail), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            Toast.makeText(this, getString(R.string.pswrdCorrectMsg), Toast.LENGTH_LONG).show();
            return;
        }

        if (cbRmmbrUser.isChecked()) {
            SharedPreferences sharedPref = SignInActivity.this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("mail", email);
            editor.putString("password", password);
            editor.putBoolean("isCheckedToSave", true);
            editor.apply();
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //checking if success
                        if (task.isSuccessful()) {
                            //display some message here
                            Toast.makeText(SignInActivity.this, R.string.signInToast, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            dialog.dismiss();
                            //FirebaseCrash.report(exce);//report(new Exception(Exc));
                            //display some message here
                            Toast.makeText(SignInActivity.this, getString(R.string.registrationErr), Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        SignInActivity.this.finish();
    }
}
