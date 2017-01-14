package finals.shotefplus;

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

/**
 * Created by Aliza on 14/01/2017.
 */

public class CustomerListAdapter extends BaseAdapter {


    private Activity activity;
    private LayoutInflater inflater;
    private List<Customer> customers;
    //ImageLoader imageLoader ;


    public CustomerListAdapter(Activity activity, List<Customer> customers) {
        this.activity = activity;
        this.customers = customers;
    }

    @Override
    public int getCount() {
        return customers.size();
    }

    @Override
    public Object getItem(int location) {
        return customers.get(location);
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
            convertView = inflater.inflate(R.layout.row_customer, null);

        // getting show data for the row
        Customer customer = customers.get(position);
        TextView name = (TextView) convertView.findViewById(R.id.txtName);
        TextView summary = (TextView) convertView.findViewById(R.id.summary);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.imgVw);
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


        // name
        name.setText(customer.getName());
        //summary of details
        summary.setText(customer.getSummary());
        //isPriceOfferSent
        isPriceOfferSent.setChecked(customer.getIsPriceOfferSent());


        return convertView;
    }
}

