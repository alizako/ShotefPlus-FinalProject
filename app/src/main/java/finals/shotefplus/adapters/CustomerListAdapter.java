package finals.shotefplus.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;

import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.view.menu.MenuPopupHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

import finals.shotefplus.activities.SignInActivity;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.R;
import finals.shotefplus.objects.IconizedMenu;

import static finals.shotefplus.R.*;


/**
 * Created by Aliza on 14/01/2017.
 */

public class CustomerListAdapter extends BaseAdapter {

    private ProgressDialog dialog;
    private Activity activity;
    private LayoutInflater inflater;
    private List<Customer> customers;
    //ImageLoader imageLoader ;
    //ImageView menu;
    Random rand;

    public CustomerListAdapter(Activity activity, List<Customer> customers) {
        this.activity = activity;
        this.customers = customers;
        rand = new Random();
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
            convertView = inflater.inflate(layout.row_customer, null);

        // getting show data for the row
        final Customer customer = customers.get(position);
        TextView name = (TextView) convertView.findViewById(id.txtName);
        TextView summary = (TextView) convertView.findViewById(id.summary);

        //Initials
        TextView tvInitials = (TextView) convertView.findViewById(id.tvInitials);
        LinearLayout loInitials = (LinearLayout) convertView.findViewById(id.loInitials);

        int color = Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        loInitials.setBackgroundTintList(ColorStateList.valueOf(color));
        if (customer.getName().length() > 0)
            tvInitials.setText(customer.getName().substring(0, 1).toUpperCase());

        handleMenu(customer, convertView);

        // picture
        /*final ImageView imgVwPic = (ImageView) convertView.findViewById(id.imgVwPic);
        imgVwPic.setTag(position);
        imgVwPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //TODO: open camera/gallery
            }
        });*/

        // name
        name.setText(customer.getName());

        //summary of details
        String type = (customer.getCustomerTypeToString().equals("0")) ? ("") :
                (customer.getCustomerTypeToString());
        if (type.length() > 0)
            type += "\n";

        String cName = (customer.getCustomerContactName().equals("")) ? ("") : (customer.getCustomerContactName());
        if (cName.length() > 0)
            cName += "\n";

        String adrs = (customer.getAdrs().equals("") ? "" : ("\n" + customer.getAdrs()));

        summary.setText(type + cName +
                customer.getPhoneNum() + " | " +
                customer.getEmail() +
                adrs
        );

        return convertView;
    }



    /* ************************************************************************************************ */

    private void goToSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.parse("package:" + activity.getPackageName());
        intent.setData(uri);
        activity.startActivity(intent);
        Toast.makeText(activity, "Please Allow Calling Permissions for this Application",
                Toast.LENGTH_LONG).show();
    }


    private void handleMenu(final Customer customer, View convertView) {
//popup menu:
        final ImageView menu = (ImageView) convertView.findViewById(id.imgVwMenu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                IconizedMenu popupMenu;
                popupMenu = new IconizedMenu(activity, menu);
                Menu menu = popupMenu.getMenu();
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_popup_customer, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new IconizedMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //TODO: redirect the user to call/msg/email

                        // call number:
                        if (item.getTitle().equals(activity.getResources().getString(string.cCall))) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + customer.getPhoneNum()));
                            dialog = ProgressDialog.show(activity,
                                    "בודק הרשאות",
                                    "אנא המתן בזמן קבלת הרשאות..",
                                    true);
                            if (ActivityCompat.checkSelfPermission(activity,
                                    android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                                //no permission allowed for this app- change settings:
                                goToSettings();
                                dialog.dismiss();
                                return false;
                            }
                            dialog.dismiss();
                            activity.startActivity(callIntent);
                        }
                        // sms
                        if (item.getTitle().equals(activity.getResources().getString(string.cSms))) {
                            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                            smsIntent.setData(Uri.parse("smsto:" + customer.getPhoneNum()));
                            activity.startActivity(smsIntent);
                        }
                        // email
                        if (item.getTitle().equals(activity.getResources().getString(string.cEmail))) {
                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            emailIntent.setType("message/rfc822");
                            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{customer.getEmail()});
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "retype subject");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, "insert body\n");
                            try {
                                activity.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(activity, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                            }
                            activity.startActivity(emailIntent);
                        }

                        Toast.makeText(activity, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

            }
        });
    }
}

