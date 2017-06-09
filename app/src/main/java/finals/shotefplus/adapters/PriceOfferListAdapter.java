package finals.shotefplus.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import finals.shotefplus.DataAccessLayer.FirebaseHandler;
import finals.shotefplus.R;
import finals.shotefplus.activities.PriceOffersActivity;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.Expense;
import finals.shotefplus.objects.PriceOffer;

/**
 * Created by Aliza on 16/01/2017.
 */

public class PriceOfferListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<PriceOffer> priceOfferList;
    private List<Customer> customerList;
    PriceOffer priceOffer;
    private FirebaseAuth firebaseAuth;
    DatabaseReference dbRef;
    private boolean endFlag = false;
    Random rand;

    public PriceOfferListAdapter(Activity activity, List<PriceOffer> priceOfferList, List<Customer> customerList) {
        this.activity = activity;
        this.inflater = inflater;
        this.priceOfferList = priceOfferList;
        this.customerList = customerList;
        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        rand = new Random();
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

        //Initials
        TextView tvInitials = (TextView) convertView.findViewById(R.id.tvInitials);
        LinearLayout loInitials = (LinearLayout) convertView.findViewById(R.id.loInitials);

        int color = Color.argb(140, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        loInitials.setBackgroundTintList(ColorStateList.valueOf(color));

        final TextView name = (TextView) convertView.findViewById(R.id.txtName);
        final TextView summary = (TextView) convertView.findViewById(R.id.summary);
        final CheckBox cbIsPriceOfferSent = (CheckBox) convertView.findViewById(R.id.cbSent);
        cbIsPriceOfferSent.setTag(position); //MUST!!

        cbIsPriceOfferSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // final boolean isChecked = cbIsPriceOfferSent.isChecked();
                // Do something here.
                int pos = ((int) cbIsPriceOfferSent.getTag());
                priceOffer = priceOfferList.get(pos);
                priceOffer.setPriceOfferSent(cbIsPriceOfferSent.isChecked());
                FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                        .updatePriceOffer(priceOffer, priceOffer.getIdNum());
            }
        });

        int i = 0;
        while (i < customerList.size() && !customerList.get(i).getIdNum().equals(priceOffer.getCustomerIdNum())) {
            i++;
        }
        if (i < customerList.size()) {
            Customer customer = new Customer();
            customer = customerList.get(i);
            name.setText("לקוח " + customer.getName());
            if (customer.getName().length() > 0)
                tvInitials.setText(customer.getName().substring(0, 1).toUpperCase());
        } else //no customer found
            tvInitials.setText("NA");


        summary.setText("הצעה: " + priceOffer.getWorkDetails() + "\n"
                + priceOffer.getSumPayment() + " ש''ח " + "\n" +
                "תאריך יעד: " + priceOffer.dueDateToString() + "\n" +
                "מיקום: " + ((!priceOffer.getLocation().equals("")) ?
                priceOffer.getLocation() : "לא הוכנס מיקום") + "\n" +
                ((priceOffer.isPriceOfferApproved()) ? "אושרה לביצוע" : "טרם אושרה לביצוע"));
        cbIsPriceOfferSent.setChecked(priceOffer.isPriceOfferSent());

        convertView.setTag(endFlag);
        return convertView;
    }


}
