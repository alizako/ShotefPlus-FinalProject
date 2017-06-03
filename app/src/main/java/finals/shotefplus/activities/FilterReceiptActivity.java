package finals.shotefplus.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

import finals.shotefplus.R;
public class FilterReceiptActivity extends AppCompatActivity {
    Button btnFilter, btnCancel, btnNoFilter;
    CheckBox cbCash, cbCheque, cbTrans,cbCredit;
    RadioButton rbPaid, rbNotPaid;
    static final int RESULT_CLEAN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_receipt);

        rbPaid = (RadioButton) findViewById(R.id.rbPaid);
        rbNotPaid = (RadioButton) findViewById(R.id.rbNotPaid);
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
                returnIntent.putExtra("rbPaid", rbPaid.isChecked());
                returnIntent.putExtra("rbNotPaid", rbNotPaid.isChecked());
                returnIntent.putExtra("cbCash", cbCash.isChecked());
                returnIntent.putExtra("cbCheque", cbCheque.isChecked());
                returnIntent.putExtra("cbTrans", cbTrans.isChecked());
                returnIntent.putExtra("cbCredit", cbCredit.isChecked());
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
