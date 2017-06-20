package finals.shotefplus.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MsgTemplateActivity extends AppCompatActivity {

    String msg;
    EditText etMsg;
    Button btnUpdate, btnCancel;
    private FirebaseAuth firebaseAuth;//defining firebaseauth object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_template);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        etMsg = ((EditText) findViewById(R.id.etMsg));
        retrieveSavedMsg();
        setEvents();
    }

    private void retrieveSavedMsg() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid()); // + "/MsgTemplate"

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.child("MsgTemplate").exists())
                    if (snapshot.child("MsgTemplate").getValue().toString().length() > 0)
                        etMsg.setText(snapshot.child("MsgTemplate").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(getBaseContext(), "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
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

                msg = etMsg.getText().toString();
                FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid()).updateMsgTemplate(msg);

                setResult(Activity.RESULT_OK, new Intent());
                finish();
            }
        });


    }
}
