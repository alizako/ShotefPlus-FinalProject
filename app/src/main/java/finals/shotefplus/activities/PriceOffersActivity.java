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
        lvpriceOffer = (ListView) findViewById(R.id.listViewPriceOffers);


        initFilter();
        initList();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent= new Intent(PriceOffersActivity.this, InsertPriceOfferActivity.class);
                startActivity(new Intent(PriceOffersActivity.this, InsertPriceOfferActivity.class));

            }
        });

        //lvpriceOffer.setOnClickListener();
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
                initListSent();
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
        //Date date = new Date();
        String date = dateFormat.format(new Date());


        //Customer c= new Customer("Aliza",false,"dddfgdf","05555555","dsf@gggg");
        priceOfferList = new ArrayList<PriceOffer>();
        priceOfferList.add(new PriceOffer(new Customer("Ely", true, "הרצליה", "056-9998877", "ely@gmail.com"),
                500, 585, "פלייר", date, true));
        priceOfferList.add(new PriceOffer(new Customer("Aliza", false, "עמק רפאים ירושלים", "052-8657571", "aliza@gmail.com"),
                3500, 3578, "עיצוב אתר", date, false));
        priceOfferList.add(new PriceOffer(new Customer("Sasson", false, "מלחה ירושלים", "052-5555555", "sasson@gmail.com"),
                1200, 1220, "הזמנה", date, false));
        priceOfferList.add(new PriceOffer(new Customer("ADAM", false, "כפר אדומים", "050-5555535", "adam@gmail.com"),
                3500, 3578, "כרטיסי ביקור", date, false));
        priceOfferList.add(new PriceOffer(new Customer("Moran", true, "גילה ירושלים", "055-5533554", "moran@gmail.com"),
                1200, 1220, "עיצוב לוגו", date, true));

        priceOfferList.add(new PriceOffer(new Customer("Shimon", true, "מבשרת ציון", "050-5463723", "shimi@gmail.com"),
                100, 117, "כריכת ספר", date, true));
        priceOfferList.add(new PriceOffer(new Customer("Joe A", false, "אבן גבירול תל אביב-יפו", "050-6662223", "joe.a@gmail.com"),
                1200, 1220, "פנקס קבלות", date, false));


        adapter = new PriceOfferListAdapter(PriceOffersActivity.this, priceOfferList);
        lvpriceOffer.setAdapter(adapter);
    }

    private void initListSent() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        //Date date = new Date();
        String date = dateFormat.format(new Date());


        //Customer c= new Customer("Aliza",false,"dddfgdf","05555555","dsf@gggg");
        priceOfferList = new ArrayList<PriceOffer>();
        priceOfferList.add(new PriceOffer(new Customer("Ely", true, "הרצליה", "056-9998877", "ely@gmail.com"),
                500, 585, "פלייר", date, true));

        priceOfferList.add(new PriceOffer(new Customer("Moran", true, "גילה ירושלים", "055-5533554", "moran@gmail.com"),
                1200, 1220, "עיצוב לוגו", date, true));

        priceOfferList.add(new PriceOffer(new Customer("Shimon", true, "מבשרת ציון", "050-5463723", "shimi@gmail.com"),
                100, 117, "כריכת ספר", date, true));



        adapter = new PriceOfferListAdapter(PriceOffersActivity.this, priceOfferList);
        lvpriceOffer.setAdapter(adapter);
    }
}
