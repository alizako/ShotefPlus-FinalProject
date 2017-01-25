package finals.shotefplus.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import finals.shotefplus.R;
public class FilterReceiptActivity extends AppCompatActivity {
    Button btnFilter;
    CheckBox cbPaid, cbNotPaid,cbCash, cbCheque, cbTrans,cbCredit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_receipt);

        cbPaid = (CheckBox) findViewById(R.id.cbPaid);
        cbNotPaid = (CheckBox) findViewById(R.id.cbNotPaid);
        cbCash = (CheckBox) findViewById(R.id.cbCash);
        cbCheque = (CheckBox) findViewById(R.id.cbCheque);
        cbTrans = (CheckBox) findViewById(R.id.cbTrans);
        cbCredit = (CheckBox) findViewById(R.id.cbCredit);

        btnFilter = (Button) findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(FilterPriceOfferActivity.this, InsertPriceOfferActivity.class));
                Intent returnIntent = getIntent();
                returnIntent.putExtra("cbPaid", cbPaid.isChecked());
                returnIntent.putExtra("cbNotPaid", cbNotPaid.isChecked());
                returnIntent.putExtra("cbCash", cbCash.isChecked());
                returnIntent.putExtra("cbCheque", cbCheque.isChecked());
                returnIntent.putExtra("cbTrans", cbTrans.isChecked());
                returnIntent.putExtra("cbCredit", cbCredit.isChecked());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

    }
}
