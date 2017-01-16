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
import finals.shotefplus.adapters.PriceOfferListAdapter;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.PriceOffer;

public class PriceOffersActivity extends AppCompatActivity {

    ImageButton btnAdd;
    ListView lvpriceOffer;
    private List<PriceOffer> priceOfferList;
    private PriceOfferListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_offers);

        btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PriceOffersActivity.this,InsertPriceOfferActivity.class));
            }
        });


        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        lvpriceOffer = (ListView) findViewById(R.id.listViewPriceOffers);
        //Customer c= new Customer("Aliza",false,"dddfgdf","05555555","dsf@gggg");
        priceOfferList = new ArrayList<PriceOffer>();
        priceOfferList.add(new PriceOffer(new Customer("Aliza",false,"dddfgdf","05555555","dsf@gggg"),
                3500,3578,"פירוט...1", date.toString(),false));
        priceOfferList.add(new PriceOffer(new Customer("ששון",false,"בהבעע עיכי","05555555","dsf@gggg"),
                1200,1220,"פירוט...2", date.toString(),false));
        priceOfferList.add(new PriceOffer(new Customer("ADAM",false,"עעג כעיכע","05555555","dsf@gggg"),
                3500,3578,"פירוט...3", date.toString(),false));
        priceOfferList.add(new PriceOffer(new Customer("Moran",false,"dddfgdf","05555555","dsf@gggg"),
                1200,1220,"פירוט...4", date.toString(),true));
        priceOfferList.add(new PriceOffer(new Customer("AlizaKo",false,"dddfgdf","05555555","dsf@gggg"),
                1200,1220,"פירוט...5", date.toString(),false));
        priceOfferList.add(new PriceOffer(new Customer("Shimon",false,"xfdfg dfdfg","05555555","dsf@gggg"),
                100,117,"פירוט...6", date.toString(),true));
        priceOfferList.add(new PriceOffer(new Customer("Joe A",false,"dddfgdf","05555555","dsf@gggg"),
                1200,1220,"פירוט...7", date.toString(),false));
        priceOfferList.add(new PriceOffer(new Customer("Ely",false,"dddfgdf","05555555","dsf@gggg"),
                500,585,"פירוט...8", date.toString(),true));

        adapter = new PriceOfferListAdapter(PriceOffersActivity.this,priceOfferList);
        lvpriceOffer.setAdapter(adapter);

    }
}
