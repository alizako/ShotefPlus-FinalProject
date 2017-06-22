package finals.shotefplus.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import finals.shotefplus.DataAccessLayer.FirebaseHandler;
import finals.shotefplus.R;

public class BusinessTypeActivity extends AppCompatActivity {

RadioButton rbPatur,rbMurshe;
    Button btnUpdate, btnCancel;
    private FirebaseAuth firebaseAuth;//defining firebaseauth object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_type);
        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        rbPatur = ((RadioButton) findViewById(R.id.rbPatur));
        rbMurshe = ((RadioButton) findViewById(R.id.rbMurshe));

        retrieveSavedType();
        setEvents();
    }

    private void retrieveSavedType() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseLink) +
                        firebaseAuth.getCurrentUser().getUid());

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    if (postSnapshot.getKey().toString().equals("BusinessType")) {
                        rbPatur.setChecked(postSnapshot.getValue(String.class)
                                .equals(rbPatur.getTag().toString()));
                        rbMurshe.setChecked(postSnapshot.getValue(String.class)
                                .equals(rbMurshe.getTag().toString()));
                    }
                    else break;
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
                FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                        .updateBusinessType(
                                rbPatur.isChecked() ? rbPatur.getTag().toString():
                                (rbMurshe.isChecked() ? rbMurshe.getTag().toString() : " ")
                        );

                setResult(Activity.RESULT_OK, new Intent());
                finish();
            }
        });


    }
}
