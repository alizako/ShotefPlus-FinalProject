package finals.shotefplus.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import finals.shotefplus.R;

public class InsertPriceOfferActivity extends AppCompatActivity {


    Button btnAdd, btnSendPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_price_offer);

        btnSendPdf = (Button) findViewById(R.id.btnSendPdf);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"הצעת מחיר התווספה",Toast.LENGTH_LONG).show();
            }
        });

        btnSendPdf.setOnClickListener(new View.OnClickListener() {
            //open gmail to edit the msg before sending
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"הצעת מחיר נשלחה ללקוח במייל",Toast.LENGTH_LONG).show();
            }
        });


    }
}
