package finals.shotefplus.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import finals.shotefplus.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        View rowSetting1 = findViewById(R.id.row1);
        View rowSetting2 = findViewById(R.id.row2);
        View rowSetting3 = findViewById(R.id.row3);
        View rowSetting4 = findViewById(R.id.row4);
        View rowSetting5 = findViewById(R.id.row5);

        ImageView imgBtnEditSetting1 = (ImageView) rowSetting1.findViewById(R.id.imgbtn);
        ImageView imgBtnEditSetting2 = (ImageView) rowSetting2.findViewById(R.id.imgbtn);
        ImageView imgBtnEditSetting3 = (ImageView) rowSetting3.findViewById(R.id.imgbtn);
        ImageView imgBtnEditSetting4 = (ImageView) rowSetting4.findViewById(R.id.imgbtn);
        ImageView imgBtnEditSetting5 = (ImageView) rowSetting5.findViewById(R.id.imgbtn);

        TextView txtTitle = (TextView) rowSetting1.findViewById(R.id.txtTitle);
        TextView txtDetails = (TextView) rowSetting1.findViewById(R.id.txtDetails);
        //txtTitle.setText(R.string.paymentType);
        txtTitle.setText("הגדרת תבנית לדוא''ל");
        txtDetails.setText("");

        txtTitle = (TextView) rowSetting2.findViewById(R.id.txtTitle);
        txtDetails = (TextView) rowSetting2.findViewById(R.id.txtDetails);
        txtTitle.setText("הגדרת תבנית להודעה");
        txtDetails.setText("");

        txtTitle = (TextView) rowSetting3.findViewById(R.id.txtTitle);
        txtDetails = (TextView) rowSetting3.findViewById(R.id.txtDetails);
        txtTitle.setText("הגדרת עסק");
        txtDetails.setText("פטור  |  מורשה");

        txtTitle = (TextView) rowSetting4.findViewById(R.id.txtTitle);
        txtDetails = (TextView) rowSetting4.findViewById(R.id.txtDetails);
        txtTitle.setText("הגדרת סוגי תשלום");
        txtDetails.setText("מזומן  |  צ'ק  |  העברה  | אשראי");

        txtTitle = (TextView) rowSetting5.findViewById(R.id.txtTitle);
        txtDetails = (TextView) rowSetting5.findViewById(R.id.txtDetails);
        txtTitle.setText("הגדרת אופן תשלום");
        txtDetails.setText("שוטף  |  30+  |  60+  |  90+");

    }
}
