package finals.shotefplus.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    ImageView imgBtnUserProfile, imgBtnChangePass, imgBtnForgetUser, imgBtnEmailContent, imgBtnMsgContent, imgBtnBusinessType;
    static final int REQ_PROFILE = 1;
    static final int REQ_REGISTER = 2;
    static final int REQ_MSG = 3;
    static final int REQ_MAIL = 4;
    static final int REQ_BSN = 5;
    static final int RESULT_EXP = 2;
    private static final String PREFS_NAME = "SIGNING-IN";
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
        View rowForgetUser = findViewById(R.id.forgetUser);
        View rowEmailContent = findViewById(R.id.emailContent);
        View rowMsgContent = findViewById(R.id.msgContent);
        View rowBusinessType = findViewById(R.id.businessType);

        imgBtnUserProfile = (ImageView) rowUserProfile.findViewById(R.id.imgbtn);
        imgBtnChangePass = (ImageView) rowChangePass.findViewById(R.id.imgbtn);
        imgBtnForgetUser = (ImageView) rowForgetUser.findViewById(R.id.imgbtn);
        imgBtnEmailContent = (ImageView) rowEmailContent.findViewById(R.id.imgbtn);
        imgBtnMsgContent = (ImageView) rowMsgContent.findViewById(R.id.imgbtn);
        imgBtnBusinessType = (ImageView) rowBusinessType.findViewById(R.id.imgbtn);


        txtTitle = (TextView) rowUserProfile.findViewById(R.id.txtTitle);
        txtDetails = (TextView) rowUserProfile.findViewById(R.id.txtDetails);
        txtTitle.setText("פרופיל משתמש");
        txtDetails.setText("שם עסק  |  מס' תיק  |  מס' טלפון");

        txtTitle = (TextView) rowChangePass.findViewById(R.id.txtTitle);
        txtDetails = (TextView) rowChangePass.findViewById(R.id.txtDetails);
        txtTitle.setText("שינוי סיסמא ו/או דוא''ל");
        txtDetails.setText("");

        txtTitle = (TextView) rowForgetUser.findViewById(R.id.txtTitle);
        txtDetails = (TextView) rowForgetUser.findViewById(R.id.txtDetails);
        //txtTitle.setText(R.string.paymentType);
        txtTitle.setText("שכח אותי ממכשיר זה");
        txtDetails.setText(""); //TODO: set if it remembered on this cell


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
            if (resultCode == RESULT_EXP) {
                Toast.makeText(this, "Sorry, Something weng wrong. Please try again later", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == REQ_REGISTER) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Credential updated successfully!", Toast.LENGTH_LONG).show();
            }
            if (resultCode == RESULT_EXP) {
                Toast.makeText(this, "Sorry, Something weng wrong. Please try again later", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == REQ_MSG) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Message updated successfully!", Toast.LENGTH_LONG).show();
            }
            if (resultCode == RESULT_EXP) {
                Toast.makeText(this, "Sorry, Something weng wrong. Please try again later", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == REQ_MAIL) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Email message updated successfully!", Toast.LENGTH_LONG).show();
            }
            if (resultCode == RESULT_EXP) {
                Toast.makeText(this, "Sorry, Something weng wrong. Please try again later", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == REQ_BSN) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Business Type updated successfully!", Toast.LENGTH_LONG).show();
            }
            if (resultCode == RESULT_EXP) {
                Toast.makeText(this, "Sorry, Something weng wrong. Please try again later", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void setEvents() {

        imgBtnUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(SettingsActivity.this, UserProfileActivity.class);
                    startActivityForResult(intent, REQ_PROFILE);
                } catch (Exception ex) {
                    Toast.makeText(SettingsActivity.this,
                            "Sorry, Something weng wrong. Please try again later",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        imgBtnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(SettingsActivity.this, ChangeRegistrationInfoActivity.class);
                    startActivityForResult(intent, REQ_REGISTER);
                } catch (Exception ex) {
                    Toast.makeText(SettingsActivity.this,
                            "Sorry, Something weng wrong. Please try again later",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        imgBtnForgetUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    SharedPreferences sharedPref = SettingsActivity.this.
                            getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    sharedPref.edit().remove("mail").commit();
                    sharedPref.edit().remove("password").commit();
                    sharedPref.edit().remove("isCheckedToSave").commit();
                    Toast.makeText(SettingsActivity.this, "Credential dismissed from this device", Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    Toast.makeText(SettingsActivity.this,
                            "Sorry, Something weng wrong. Please try again later",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        imgBtnMsgContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(SettingsActivity.this, MsgTemplateActivity.class);
                    startActivityForResult(intent, REQ_MSG);
                } catch (Exception ex) {
                    Toast.makeText(SettingsActivity.this,
                            "Sorry, Something weng wrong. Please try again later",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        imgBtnEmailContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(SettingsActivity.this, MailTemplateActivity.class);
                    startActivityForResult(intent, REQ_MAIL);
                } catch (Exception ex) {
                    Toast.makeText(SettingsActivity.this,
                            "Sorry, Something weng wrong. Please try again later",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        imgBtnBusinessType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(SettingsActivity.this, BusinessTypeActivity.class);
                    startActivityForResult(intent, REQ_BSN);
                } catch (Exception ex) {
                    Toast.makeText(SettingsActivity.this,
                            "Sorry, Something weng wrong. Please try again later",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
