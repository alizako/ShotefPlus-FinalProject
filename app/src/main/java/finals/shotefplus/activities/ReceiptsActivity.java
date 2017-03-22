package finals.shotefplus.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import finals.shotefplus.R;
import finals.shotefplus.adapters.CustomerListAdapter;
import finals.shotefplus.adapters.ReceiptListAdapter;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.PriceOffer;
import finals.shotefplus.objects.Receipt;
import finals.shotefplus.objects.Work;

public class ReceiptsActivity extends AppCompatActivity {
    ListView lvReceipts;
    private List<Receipt> receiptList;
    private ReceiptListAdapter adapter;
    View filter;
    ImageButton btnAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts);

        initFilter();
        initList();


        btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReceiptsActivity.this,InsertReceiptActivity.class));
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                boolean isPaid = data.getBooleanExtra("cbPaid", false);
                boolean isNotPaid = data.getBooleanExtra("cbNotPaid", false);
                boolean isCash = data.getBooleanExtra("cbCash", false);
                boolean isCheque = data.getBooleanExtra("cbCheque", false);
                boolean isTrans = data.getBooleanExtra("cbTrans", false);
                boolean isCredit = data.getBooleanExtra("cbCredit", false);
                // get from DB
                //set Filter text:
                TextView txtDetails = (TextView) filter.findViewById(R.id.txtDetails);
                txtDetails.setText(
                        (isPaid ? "שולם | " : "") +
                                (isNotPaid ? "לא שולם | " : "") +
                                (isCash ? "מזומן | " : "") +
                                (isCheque ? "המחאה |" : "")+
                                (isTrans ? "העברה |" : "")+
                                (isCredit ? "אשראי |" : "")

                );
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private void initFilter() {
        filter = findViewById(R.id.barFilter);
        ImageButton imgBtnFilter = (ImageButton) filter.findViewById(R.id.imgbtnFilter);
        imgBtnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReceiptsActivity.this, FilterReceiptActivity.class);
                //startActivity(new Intent(PriceOffersActivity.this, FilterPriceOfferActivity.class));
                startActivityForResult(intent, 1);
            }
        });
    }

    private void initList() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
       // String date = dateFormat.format(new Date());

        lvReceipts = (ListView) findViewById(R.id.listViewReceipts);
        receiptList = new ArrayList<Receipt>();


        /*receiptList.add(new Receipt(0,1, "פלייר",dateFormat.format(date),500, 585,
                new Work(
                new PriceOffer(
                        new Customer("Ely",  "הרצליה", "056-9998877", "ely@gmail.com"),
                   500, 585, "פלייר", dateFormat.format(date), true),
                false,false,1))
        );

        receiptList.add(new Receipt(30,2, "עיצוב לוגו",dateFormat.format(date),1200, 1220,
                new Work(
                        new PriceOffer(
                                new Customer("Moran",  "גילה ירושלים", "055-5533554", "moran@gmail.com"),
                                1200, 1220, "עיצוב לוגו", dateFormat.format(date), true),
                        false,false,2))
        );

        receiptList.add(new Receipt(30,3, "כריכת ספר",dateFormat.format(date),100, 117,
                new Work(
                        new PriceOffer(
                                new Customer("Shimon", "מבשרת ציון", "050-5463723", "shimi@gmail.com"),
                                3000, 3040, "כריכת ספר", dateFormat.format(date), true),
                        false,false,3))
        );*/



        adapter = new ReceiptListAdapter(ReceiptsActivity.this,receiptList);
        lvReceipts.setAdapter(adapter);
    }



}
