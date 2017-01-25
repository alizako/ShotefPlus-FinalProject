package finals.shotefplus.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import finals.shotefplus.R;
import finals.shotefplus.adapters.CustomerListAdapter;
import finals.shotefplus.adapters.PriceOfferListAdapter;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.PriceOffer;

public class PriceOffersActivity extends AppCompatActivity {

    ImageButton btnAdd;
    ListView lvpriceOffer;
    private List<PriceOffer> priceOfferList;
    private PriceOfferListAdapter adapter;
    View filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_offers);

        filter = findViewById(R.id.barFilter);
        btnAdd = (ImageButton) findViewById(R.id.btnAdd);


        initFilter();
        initList();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent= new Intent(PriceOffersActivity.this, InsertPriceOfferActivity.class);
                startActivity(new Intent(PriceOffersActivity.this, InsertPriceOfferActivity.class));

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                boolean isDate = data.getBooleanExtra("cbDate", false);
                boolean isSent = data.getBooleanExtra("cbSent", false);
                boolean isContact = data.getBooleanExtra("cbContact", false);
                boolean isApproved = data.getBooleanExtra("cbApproved", false);
                boolean isNotApproved = data.getBooleanExtra("cbNotApproved", false);
                // get from DB
                //set Filter text:
                TextView txtDetails = (TextView) filter.findViewById(R.id.txtDetails);
                txtDetails.setText(
                        (isDate ? "תאריך הפקה | " : "") +
                                (isSent ? "נשלח ללקוח | " : "") +
                                (isContact ? "איש קשר | " : "") +
                                (isApproved ? "אושר לביצוע |" : "")+
                                (isNotApproved ? "לא אושר לביצוע |" : "")

                );
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private void initFilter() {

        ImageButton imgBtnFilter = (ImageButton) filter.findViewById(R.id.imgbtnFilter);
        imgBtnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PriceOffersActivity.this, FilterPriceOfferActivity.class);
                //startActivity(new Intent(PriceOffersActivity.this, FilterPriceOfferActivity.class));
                startActivityForResult(intent, 1);
            }
        });
    }

    private void initList() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        lvpriceOffer = (ListView) findViewById(R.id.listViewPriceOffers);
        //Customer c= new Customer("Aliza",false,"dddfgdf","05555555","dsf@gggg");
        priceOfferList = new ArrayList<PriceOffer>();
        priceOfferList.add(new PriceOffer(new Customer("Aliza", false, "dddfgdf", "05555555", "dsf@gggg"),
                3500, 3578, "פירוט...1", date.toString(), false));
        priceOfferList.add(new PriceOffer(new Customer("ששון", false, "בהבעע עיכי", "05555555", "dsf@gggg"),
                1200, 1220, "פירוט...2", date.toString(), false));
        priceOfferList.add(new PriceOffer(new Customer("ADAM", false, "עעג כעיכע", "05555555", "dsf@gggg"),
                3500, 3578, "פירוט...3", date.toString(), false));
        priceOfferList.add(new PriceOffer(new Customer("Moran", false, "dddfgdf", "05555555", "dsf@gggg"),
                1200, 1220, "פירוט...4", date.toString(), true));
        priceOfferList.add(new PriceOffer(new Customer("AlizaKo", false, "dddfgdf", "05555555", "dsf@gggg"),
                1200, 1220, "פירוט...5", date.toString(), false));
        priceOfferList.add(new PriceOffer(new Customer("Shimon", false, "xfdfg dfdfg", "05555555", "dsf@gggg"),
                100, 117, "פירוט...6", date.toString(), true));
        priceOfferList.add(new PriceOffer(new Customer("Joe A", false, "dddfgdf", "05555555", "dsf@gggg"),
                1200, 1220, "פירוט...7", date.toString(), false));
        priceOfferList.add(new PriceOffer(new Customer("Ely", false, "dddfgdf", "05555555", "dsf@gggg"),
                500, 585, "פירוט...8", date.toString(), true));

        adapter = new PriceOfferListAdapter(PriceOffersActivity.this, priceOfferList);
        lvpriceOffer.setAdapter(adapter);
    }
}
