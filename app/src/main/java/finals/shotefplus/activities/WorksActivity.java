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
import finals.shotefplus.adapters.ReceiptListAdapter;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.Receipt;
import finals.shotefplus.objects.Work;

public class WorksActivity extends AppCompatActivity {
    ListView lvWorks;
    private List<Work> workList;
    //private WorkListAdapter adapter;
    View filter;
    // ImageButton btnAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_works);

        initFilter();
        initList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                boolean isWorkDone = data.getBooleanExtra("cbWorkDone", false);
                boolean isWorkCancelled = data.getBooleanExtra("cbWorkCancelled", false);

                // get from DB
                //set Filter text:
                TextView txtDetails = (TextView) filter.findViewById(R.id.txtDetails);
                txtDetails.setText(
                        (isWorkDone ? "תאריך הפקה | " : "") +
                                (isWorkCancelled ? "נשלח ללקוח | " : "")
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
                Intent intent = new Intent(WorksActivity.this, FilterWorkActivity.class);
                //startActivity(new Intent(PriceOffersActivity.this, FilterPriceOfferActivity.class));
                startActivityForResult(intent, 1);
            }
        });
    }

    private void initList() {
        /*DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
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
        lvReceipts.setAdapter(adapter);*/
    }

}
