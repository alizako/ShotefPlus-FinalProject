package finals.shotefplus.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import finals.shotefplus.DataAccessLayer.FirebaseHandler;
import finals.shotefplus.R;
import finals.shotefplus.objects.PriceOffer;
import finals.shotefplus.objects.UserProfile;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    DatabaseReference dbRef;
    TextView txtTitle, txtDetails;
    ImageView imgBtnUserProfile, imgBtnChangePass, imgBtnEmailContent, imgBtnMsgContent, imgBtnBusinessType;
    static final int REQ_PROFILE = 1;
    static final int RESULT_EXP= 2;
    UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/UserProfile/");


        View rowUserProfile = findViewById(R.id.userProfile);
        View rowChangePass = findViewById(R.id.changePass);
        View rowEmailContent = findViewById(R.id.emailContent);
        View rowMsgContent = findViewById(R.id.msgContent);
        View rowBusinessType = findViewById(R.id.businessType);

        imgBtnUserProfile = (ImageView) rowUserProfile.findViewById(R.id.imgbtn);
        imgBtnChangePass = (ImageView) rowChangePass.findViewById(R.id.imgbtn);
        imgBtnEmailContent = (ImageView) rowEmailContent.findViewById(R.id.imgbtn);
        imgBtnMsgContent = (ImageView) rowMsgContent.findViewById(R.id.imgbtn);
        imgBtnBusinessType = (ImageView) rowBusinessType.findViewById(R.id.imgbtn);


        txtTitle = (TextView) rowUserProfile.findViewById(R.id.txtTitle);
        txtDetails = (TextView) rowUserProfile.findViewById(R.id.txtDetails);
        txtTitle.setText("פרופיל משתמש");
        txtDetails.setText("");

        txtTitle = (TextView) rowChangePass.findViewById(R.id.txtTitle);
        txtDetails = (TextView) rowChangePass.findViewById(R.id.txtDetails);
        txtTitle.setText("שינוי סיסמא");
        txtDetails.setText("");

        txtTitle = (TextView) rowEmailContent.findViewById(R.id.txtTitle);
        txtDetails = (TextView) rowEmailContent.findViewById(R.id.txtDetails);
        //txtTitle.setText(R.string.paymentType);
        txtTitle.setText("הגדרת תבנית לדוא''ל");
        txtDetails.setText("");

        txtTitle = (TextView) rowMsgContent.findViewById(R.id.txtTitle);
        txtDetails = (TextView) rowMsgContent.findViewById(R.id.txtDetails);
        txtTitle.setText("הגדרת תבנית להודעה");
        txtDetails.setText("");

        txtTitle = (TextView) rowBusinessType.findViewById(R.id.txtTitle);
        txtDetails = (TextView) rowBusinessType.findViewById(R.id.txtDetails);
        txtTitle.setText("הגדרת עסק");
        txtDetails.setText("פטור  |  מורשה");


        setEvents();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_LONG).show();
            }
            if(resultCode == RESULT_EXP){
                Toast.makeText(this, "Sorry, Something weng wrong. Please try again later", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setEvents() {

        imgBtnUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsActivity.this, UserProfileActivity.class);
                startActivityForResult(intent, REQ_PROFILE);
            }
        });
    }

}
