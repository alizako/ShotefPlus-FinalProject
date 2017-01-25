package finals.shotefplus.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

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

    public PriceOfferListAdapter(Activity activity, List<PriceOffer> priceOfferList) {
        this.activity = activity;
        this.inflater = inflater;
        this.priceOfferList = priceOfferList;
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

        // getting show data for the row
        PriceOffer priceOffer = priceOfferList.get(position);
        TextView name = (TextView) convertView.findViewById(R.id.txtName);
        TextView summary = (TextView) convertView.findViewById(R.id.summary);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imgVw);
        CheckBox isPriceOfferSent = (CheckBox) convertView.findViewById(R.id.cbSent);



        /*// image-  Picasso does Automatic memory and disk caching:
        if (customer.getImgUrl().equals("")) {
            Picasso.with(convertView.getContext()).
                    load(R.drawable.img_not_fnd).into(imageView);
        }
        else{
            Picasso.with(convertView.getContext()).
                    load(customer.getImgUrl()).into(imageView);
        }*/


        // name customer
        name.setText("לקוח " + priceOffer.getCustomer().getName() + " | " + "מס' " + priceOffer.getCustomer().getIdNum());
        //summary of details
        //summary.setText("הצעה " + priceOffer.getSumPaymentMaam() + " | " + priceOffer.getDate());
        //isPriceOfferSent
        isPriceOfferSent.setChecked(priceOffer.isPriceOfferSent());


        return convertView;
    }
}
