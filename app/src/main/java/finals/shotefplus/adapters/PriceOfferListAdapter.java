package finals.shotefplus.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import finals.shotefplus.DataAccessLayer.FirebaseHandler;
import finals.shotefplus.R;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.PriceOffer;

/**
 * Created by Aliza on 16/01/2017.
 */

public class PriceOfferListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<PriceOffer> priceOfferList;
    PriceOffer priceOffer;
    private FirebaseAuth firebaseAuth;


    public PriceOfferListAdapter(Activity activity, List<PriceOffer> priceOfferList) {
        this.activity = activity;
        this.inflater = inflater;
        this.priceOfferList = priceOfferList;
        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public int getCount() {
        return priceOfferList.size();
    }

    @Override
    public Object getItem(int location) {
        return priceOfferList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.row_price_offer, null);

        priceOffer = priceOfferList.get(position);
        TextView name = (TextView) convertView.findViewById(R.id.txtName);
        TextView summary = (TextView) convertView.findViewById(R.id.summary);
        final CheckBox cbIsPriceOfferSent = (CheckBox) convertView.findViewById(R.id.cbSent);
        cbIsPriceOfferSent.setTag(position); //MUST!!

        cbIsPriceOfferSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // final boolean isChecked = cbIsPriceOfferSent.isChecked();
                // Do something here.
                int pos = ((int)cbIsPriceOfferSent.getTag());
                priceOffer = priceOfferList.get(pos);
                priceOffer.setPriceOfferSent(cbIsPriceOfferSent.isChecked());
                FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                        .updatePriceOffer(priceOffer, priceOffer.getIdNum());
            }
        });


        // name customer
        //name.setText("לקוח " + priceOffer.getCustomer().getName() + " | " + "מס' " + priceOffer.getCustomer().getIdNum());
        name.setText("לקוח " + priceOffer.getCustomer().getName());

        //summary of details
        summary.setText("הצעה: " + priceOffer.getWorkDetails() + ""+  priceOffer.getSumPayment()+ "ש''ח "+"\n" +
                "תאריך יעד: " + priceOffer.dueDateToString() + "\n" +
                "מיקום: " +((!priceOffer.getLocation().equals(""))?priceOffer.getLocation():"לא הוכנס מיקום") + "\n" +
                ((priceOffer.isPriceOfferApproved()) ? "אושרה לביצוע" : "טרם אושרה לביצוע"));
        // summary.setText("הצעה " + priceOffer.getWorkDetails() + " | " + priceOffer.getDate());
        //isPriceOfferSent
        cbIsPriceOfferSent.setChecked(priceOffer.isPriceOfferSent());

        return convertView;
    }

    private void updateFireBase(PriceOffer priceOffer) {
        String key = priceOffer.getIdNum();

    }
}
