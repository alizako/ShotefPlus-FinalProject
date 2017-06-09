package finals.shotefplus.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Random;

import finals.shotefplus.DataAccessLayer.FirebaseHandler;
import finals.shotefplus.R;
import finals.shotefplus.activities.CustomersActivity;
import finals.shotefplus.activities.InsertCustomerActivity;
import finals.shotefplus.activities.InsertReceiptActivity;
import finals.shotefplus.activities.ReceiptImageActivity;
import finals.shotefplus.activities.ReceiptsActivity;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.EmailTemplate;
import finals.shotefplus.objects.EnumPaymentType;
import finals.shotefplus.objects.IconizedMenu;
import finals.shotefplus.objects.Receipt;

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
    static final int REQ_UPD_RECEIPT = 4;
    static final int REQ_OPEN_RECEIPT = 5;
    private FirebaseAuth firebaseAuth;
    Random rand;
    String subject = "retype subject", body = "insert content";

    public ReceiptListAdapter(Activity activity, List<Receipt> receiptsList, List<Customer> customerList) {
        this.activity = activity;
        this.receiptsList = receiptsList;
        this.customerList = customerList;
        firebaseAuth = FirebaseAuth.getInstance();
        rand = new Random();
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
        LinearLayout loReceipt = (LinearLayout) convertView.findViewById(R.id.loReceipt);

        //Initials
        TextView tvInitials = (TextView) convertView.findViewById(R.id.tvInitials);
        LinearLayout loInitials = (LinearLayout) convertView.findViewById(R.id.loInitials);

        int color = Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        loInitials.setBackgroundTintList(ColorStateList.valueOf(color));

        int i = 0;
        while (i < customerList.size() && !customerList.get(i).getIdNum().equals(receipt.getCustomerIdNum())) {
            i++;
        }
        if (i < customerList.size()) {
            final Customer customer = customerList.get(i);

            //events with customer:
            handleMenu(receipt, customer, convertView, position);

            txtName.setText("לקוח " + customer.getName());
            txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, InsertCustomerActivity.class);
                    intent.putExtra("customerIdNum", customer.getIdNum());
                    activity.startActivityForResult(intent, REQ_UPD_CUSTOMER);
                }
            });

            if (customer.getName().length() > 0)
                tvInitials.setText(customer.getName().substring(0, 1).toUpperCase());
        }

        txtReceipt.setText("קבלה " + receipt.getReceiptNum());
        loReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, InsertReceiptActivity.class);
                intent.putExtra("receiptIdNum", receipt.getIdNum());
                activity.startActivityForResult(intent, REQ_UPD_RECEIPT);
            }
        });

        String tmpPaid = receipt.getPaymentType() > 0 ? " שולם ב"
                + receipt.getStringPaymentType(receipt.getPaymentType())
                : "לא שולם";

        //summary of details
        summary.setText(receipt.dateReceiptToString() + " | " + tmpPaid);

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

    /* ************************************************************************************************ */
    private void handleMenu(final Receipt receipt, final Customer customer,
                            final View convertView, final int position) {
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
                            AlertDialog confirmationDialog = openConfirmationDialog(receipt);
                            confirmationDialog.show();
                        }
                        if (item.getTitle().equals(activity.getResources().getString(R.string.rPaidCash))) {
                            receipt.setPaid(true);
                            receipt.setPaymentType(EnumPaymentType.CASH.getValue());
                            FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                                    .updateReceipt(receipt, receipt.getIdNum());
                            Toast.makeText(activity, "הקבלה עודכנה", Toast.LENGTH_LONG).show();
                        }
                        if (item.getTitle().equals(activity.getResources().getString(R.string.rPaidCredit))) {
                            receipt.setPaid(true);
                            receipt.setPaymentType(EnumPaymentType.CREDIT.getValue());
                            FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                                    .updateReceipt(receipt, receipt.getIdNum());
                            Toast.makeText(activity, "הקבלה עודכנה", Toast.LENGTH_LONG).show();
                        }
                        if (item.getTitle().equals(activity.getResources().getString(R.string.rPaidCheque))) {
                            receipt.setPaid(true);
                            receipt.setPaymentType(EnumPaymentType.CHEQUE.getValue());
                            FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                                    .updateReceipt(receipt, receipt.getIdNum());
                            Toast.makeText(activity, "הקבלה עודכנה", Toast.LENGTH_LONG).show();
                        }
                        if (item.getTitle().equals(activity.getResources().getString(R.string.rPaidTrans))) {
                            receipt.setPaid(true);
                            receipt.setPaymentType(EnumPaymentType.TRANS.getValue());
                            FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                                    .updateReceipt(receipt, receipt.getIdNum());
                            Toast.makeText(activity, "הקבלה עודכנה", Toast.LENGTH_LONG).show();
                        }
                        //open receipt
                        if (item.getTitle().equals(activity.getResources().getString(R.string.rReceipt))) {
                            if (receipt.isPicReceiptExist()) {
                                Intent intent = new Intent(activity, ReceiptImageActivity.class);
                                intent.putExtra("isFromStorage", true);
                                intent.putExtra("receiptPictureIdNum", receipt.getIdNum());
                                intent.putExtra("receiptNum", receipt.getReceiptNum());
                                activity.startActivityForResult(intent, REQ_OPEN_RECEIPT);
                            } else
                                Toast.makeText(activity,
                                        "לא נשמרה תמונה עבור קבלה זו",
                                        Toast.LENGTH_LONG).show();
                        }

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
                            getMailTemplateFromFirebase(customer);
                        }

                        Toast.makeText(activity, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }
        });
    }
    private void getMailTemplateFromFirebase(final Customer customer) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/EmailTemplate/");

        dialog = ProgressDialog.show(activity,
                "",
                "אנא המתן",
                true);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                EmailTemplate emailTemplate = snapshot.getValue(EmailTemplate.class);
                subject = emailTemplate.getSubject();
                body = emailTemplate.getBody();

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{customer.getEmail()});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject); //"retype subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, body);//"insert body\n");
                try {
                    activity.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(activity, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
               // activity.startActivity(emailIntent);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(activity, "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

    }

    /* ************************************************************************************************ */
    private AlertDialog openConfirmationDialog(final Receipt receipt) {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(activity)
                .setTitle("מחיקה")
                .setMessage("האם אתה בטוח כי ברצונך למחוק קבלה זו?")
                .setIcon(R.drawable.alert_32)
                .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                                .deleteReceipt(receipt, receipt.getIdNum());

                        //delete picture if exist in firebase storage
                        if (receipt.isPicReceiptExist()) {
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReferenceFromUrl("gs://shotefplus-72799.appspot.com/" + firebaseAuth.getCurrentUser().getUid());

                            storageRef.child(receipt.getIdNum() + ".jpg").delete();
                        }
                        Toast.makeText(activity, "הקבלה נמחקה", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("לא", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;
    }


}
