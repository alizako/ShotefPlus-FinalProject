package finals.shotefplus.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import finals.shotefplus.R;

public class EntranceActivity extends AppCompatActivity {

    Button btnSignIn;
    Button btnSignUp;
    private static final String PREFS_NAME = "SIGNING-IN";
    FirebaseAuth firebaseAuth;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);

        firebaseAuth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.btnIn);
        btnSignUp = (Button) findViewById(R.id.btnUp);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    checkForSavedSignInDetails();
                       // signIn();

                } catch (Exception e) {
                    dialog.dismiss();
                    Toast.makeText(EntranceActivity.this,
                            "" + e.toString(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

    }

    @Override
    public void onBackPressed() {
        this.finish();
        System.exit(0);
    }

    private void signIn() {
        Intent intent = new Intent(EntranceActivity.this, SignInActivity.class);
        startActivity(intent);

    }

    private void signUp() {
        Intent intent = new Intent(EntranceActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private void checkForSavedSignInDetails() {
        SharedPreferences sharedPref = EntranceActivity.this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isCheckedToSave = sharedPref.getBoolean("isCheckedToSave", false);
        if(!isCheckedToSave)
            signIn();
        else{// (isCheckedToSave) {
            String sharedPrefEmail = sharedPref.getString("mail", "0");
            String sharedPrefPassword = sharedPref.getString("password", "0");

            if (!sharedPrefEmail.equals("0") && !sharedPrefPassword.equals("0")) {

                dialog = ProgressDialog.show(EntranceActivity.this,
                        getString(R.string.connecting),
                        getString(R.string.waitConnecting),
                        true);

                firebaseAuth.signInWithEmailAndPassword(sharedPrefEmail, sharedPrefPassword)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                //checking if success
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    Toast.makeText(EntranceActivity.this, getString(R.string.signInToast), Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(EntranceActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    //FirebaseCrash.report(exce);//report(new Exception(Exc));
                                    //display some message here
                                    dialog.dismiss();
                                    Toast.makeText(EntranceActivity.this, getString(R.string.registrationErr), Toast.LENGTH_LONG).show();
                                }
                                //  progressDialog.dismiss();
                            }
                        });

             //   return true;

            } //else return false;
        }
       // return false;
    }
}
