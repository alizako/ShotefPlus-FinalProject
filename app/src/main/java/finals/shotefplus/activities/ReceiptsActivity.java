package finals.shotefplus.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import finals.shotefplus.R;
import finals.shotefplus.adapters.CustomerListAdapter;
import finals.shotefplus.adapters.ReceiptListAdapter;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.Receipt;

public class ReceiptsActivity extends AppCompatActivity {
    ListView lvReceipts;
    private List<Receipt> receiptList;
    private ReceiptListAdapter adapter;


    ImageButton btnAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        lvReceipts = (ListView) findViewById(R.id.listViewReceipts);
        receiptList = new ArrayList<Receipt>();
        receiptList.add(new Receipt(
                new Customer("Aliza",false,"dddfgdf","05555555","dsf@gggg"),
                30, "פירוטטטט",dateFormat.format(date), 3000, 3007
        ));
        receiptList.add(new Receipt(
                new Customer("ששון",false,"בהבעע עיכי","05555555","dsf@gggg"),
                60, "פירוטטטט", dateFormat.format(date),1000, 1007.3
        ));

        receiptList.add(new Receipt(
                new Customer("ADAM",false,"עעג כעיכע","05555555","dsf@gggg"),
                90, "פירוטטטט",dateFormat.format(date), 2222, 2229.6
        ));

        adapter = new ReceiptListAdapter(ReceiptsActivity.this,receiptList);
        lvReceipts.setAdapter(adapter);



        btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReceiptsActivity.this,InsertReceiptActivity.class));
            }
        });
    }
}
