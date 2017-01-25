package finals.shotefplus.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

import finals.shotefplus.R;

public class FilterPriceOfferActivity extends AppCompatActivity {

    Button btnFilter;
    CheckBox cbDate, cbSent, cbContact, cbApproved,cbNotApproved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_price_offer);

        cbDate = (CheckBox) findViewById(R.id.cbDate);
        cbSent = (CheckBox) findViewById(R.id.cbSent);
        cbContact = (CheckBox) findViewById(R.id.cbContact);
        cbApproved = (CheckBox) findViewById(R.id.cbApproved);
        cbNotApproved = (CheckBox) findViewById(R.id.cbNotApproved);

        btnFilter = (Button) findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(FilterPriceOfferActivity.this, InsertPriceOfferActivity.class));
                Intent returnIntent = getIntent();
                returnIntent.putExtra("cbDate", cbDate.isChecked());
                returnIntent.putExtra("cbSent", cbSent.isChecked());
                returnIntent.putExtra("cbContact", cbContact.isChecked());
                returnIntent.putExtra("cbApproved", cbApproved.isChecked());
                returnIntent.putExtra("cbNotApproved", cbNotApproved.isChecked());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}
