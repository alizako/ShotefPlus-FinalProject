package finals.shotefplus.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import finals.shotefplus.R;

public class FilterWorkActivity extends AppCompatActivity {
    Button btnFilter;
    CheckBox cbWorkDone, cbWorkCancelled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_work);

        cbWorkDone = (CheckBox) findViewById(R.id.cbWorkDone);
        cbWorkCancelled = (CheckBox) findViewById(R.id.cbWorkCancelled);

        btnFilter = (Button) findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(FilterPriceOfferActivity.this, InsertPriceOfferActivity.class));
                Intent returnIntent = getIntent();
                returnIntent.putExtra("cbWorkDone", cbWorkDone.isChecked());
                returnIntent.putExtra("cbWorkCancelled", cbWorkCancelled.isChecked());

                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });


    }
}
