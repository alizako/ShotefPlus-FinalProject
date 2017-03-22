package finals.shotefplus.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;

import finals.shotefplus.R;

public class FilterPriceOfferActivity extends AppCompatActivity {

    Button btnFilter, btnCancel, btnNoFilter;
    CheckBox cbSent;
    RadioButton rbApproved, rbNotApproved;
    static final int RESULT_CLEAN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_price_offer);

        // cbDate = (CheckBox) findViewById(R.id.cbDate);
        cbSent = (CheckBox) findViewById(R.id.cbSent);
        //  cbContact = (CheckBox) findViewById(R.id.cbContact);
        rbApproved = (RadioButton) findViewById(R.id.rbApproved);
        rbNotApproved = (RadioButton) findViewById(R.id.rbNotApproved);


        btnFilter = (Button) findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(FilterPriceOfferActivity.this, InsertPriceOfferActivity.class));
                Intent returnIntent = getIntent();
                //  returnIntent.putExtra("cbDate", cbDate.isChecked());
                returnIntent.putExtra("cbSent", cbSent.isChecked());
                // returnIntent.putExtra("cbContact", cbContact.isChecked());
                returnIntent.putExtra("rbApproved", rbApproved.isChecked());
                returnIntent.putExtra("rbNotApproved", rbNotApproved.isChecked());
                // returnIntent.putExtra("rbNoFilter", rbNoFilter.isChecked());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setResult(Activity.RESULT_CANCELED, new Intent());
                finish();
            }
        });

        btnNoFilter = (Button) findViewById(R.id.btnNoFilter);
        btnNoFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setResult(RESULT_CLEAN, new Intent());
                finish();
            }
        });
    }
}

