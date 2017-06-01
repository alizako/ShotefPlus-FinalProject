package finals.shotefplus.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import finals.shotefplus.R;
import finals.shotefplus.activities.CustomersActivity;
import finals.shotefplus.activities.InsertCustomerActivity;
import finals.shotefplus.activities.InsertReceiptActivity;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.IconizedMenu;
import finals.shotefplus.objects.PriceOffer;
import finals.shotefplus.objects.Receipt;
import finals.shotefplus.objects.Work;

/**
 * Created by Aliza on 15/01/2017.
 */

public class ReceiptListAdapter extends BaseAdapter {

    private ProgressDialog dialog;
    private Activity activity;
    private LayoutInflater inflater;
    private List<Receipt> receiptsList;
    private List<Customer> customerList;
    static final int REQ_UPD_CUSTOMER = 3;
    static final int REQ_UPD_RECEIPT=4;

    public ReceiptListAdapter(Activity activity, List<Receipt> receiptsList, List<Customer> customerList) {
        this.activity = activity;
        this.receiptsList = receiptsList;
        this.customerList = customerList;
    }

    @Override
    public int getCount() {
        return receiptsList.size();
    }

    @Override
    public Object getItem(int location) {
        return receiptsList.get(location);
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
            convertView = inflater.inflate(R.layout.row_receipt, null);


        final Receipt receipt = receiptsList.get(position);
        TextView txtName = (TextView) convertView.findViewById(R.id.txtName);
        TextView txtReceipt = (TextView) convertView.findViewById(R.id.txtReceipt);
        TextView summary = (TextView) convertView.findViewById(R.id.summary);
        LinearLayout loReceipt = (LinearLayout)convertView.findViewById(R.id.loReceipt);

        int i = 0;
        while (i < customerList.size() && !customerList.get(i).getIdNum().equals(receipt.getCustomerIdNum())) {
            i++;
        }
        if (i < customerList.size()) {
            final Customer customer = customerList.get(i);

            //events with customer:
            handleMenu(receipt, customer, convertView);

            txtName.setText("לקוח " + customer.getName());
            txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, InsertCustomerActivity.class);
                    intent.putExtra("customerIdNum", customer.getIdNum());
                    activity.startActivityForResult(intent,REQ_UPD_CUSTOMER);
                }
            });
        }

        txtReceipt.setText("קבלה " + receipt.getReceiptNum());
        loReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, InsertReceiptActivity.class);
                intent.putExtra("receiptIdNum", receipt.getIdNum());
                activity.startActivityForResult(intent,REQ_UPD_RECEIPT);
            }
        });



        //summary of details
        summary.setText(receipt.dateReceiptToString() + " | " +
                //       +receipt.getSumPaymentMaam() + " | " +
                " שולם ב" + receipt.getStringPaymentType(receipt.getPaymentType()));

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

    private void handleMenu(final Receipt receipt, final Customer customer, View convertView) {
        //popup menu- Receipt:
        final ImageView menu = (ImageView) convertView.findViewById(R.id.imgVwMenuReceipt);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                IconizedMenu popupMenu;
                popupMenu = new IconizedMenu(activity, menu);
                Menu menu = popupMenu.getMenu();
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_popup_receipt, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new IconizedMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //delete
                        if (item.getTitle().equals(activity.getResources().getString(R.string.rDelete))) {
                            //TODO: Confirmation and deletion
                        }
                        //see customer
                        if (item.getTitle().equals(activity.getResources().getString(R.string.rCustomer))) {
                            //TODO: open customer
                        }
                        //see Contact subMenu
                        if (item.getTitle().equals(activity.getResources().getString(R.string.rContact))) {
                            //  item.getSubMenu().
                        }
                        //Todo: continue.....

                        Toast.makeText(activity, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }
        });

        //popup menu- Customer    :
        final ImageView menuC = (ImageView) convertView.findViewById(R.id.imgVwMenuCustomer);
        menuC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                IconizedMenu popupMenu;
                popupMenu = new IconizedMenu(activity, menuC);
                Menu menu = popupMenu.getMenu();
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_popup_customer, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new IconizedMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        // call number:
                        if (item.getTitle().equals(activity.getResources().getString(R.string.cCall))) {
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
                        if (item.getTitle().equals(activity.getResources().getString(R.string.cSms))) {
                            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                            smsIntent.setData(Uri.parse("smsto:" + customer.getPhoneNum()));
                            activity.startActivity(smsIntent);
                        }
                        // email
                        if (item.getTitle().equals(activity.getResources().getString(R.string.cEmail))) {
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
