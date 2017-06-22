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
import android.support.v4.content.res.ResourcesCompat;
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
import android.widget.ListView;
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
import finals.shotefplus.activities.InsertCustomerActivity;
import finals.shotefplus.activities.InsertReceiptActivity;
import finals.shotefplus.activities.ReceiptImageActivity;
import finals.shotefplus.objects.EmailTemplate;
import finals.shotefplus.objects.EnumObjectType;
import finals.shotefplus.objects.EnumPaymentType;
import finals.shotefplus.objects.IconizedMenu;
import finals.shotefplus.objects.LinkedObjects;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.Expense;
import finals.shotefplus.objects.PriceOffer;
import finals.shotefplus.objects.Receipt;
import finals.shotefplus.objects.Work;

/**
 * Created by Aliza on 05/06/2017.
 */

public class SearchResultsListAdapter extends BaseAdapter {

    Context context;
    private FirebaseAuth firebaseAuth;
    private Activity activity;
    private LayoutInflater inflater;
    private PriceOffer priceOffer;
    private Work work;
    private Receipt receipt;
    List<Customer> customerOfPOList;
    List<Customer> customerOfReceiptList;
    List<Work> workList;
    List <PriceOffer> priceOfferList;
    List<Receipt>receiptList;
    List<Expense>expenseList;
    int objectType;
    static final int REQ_UPD_CUSTOMER = 3;
    static final int REQ_UPD_RECEIPT = 4;
    static final int REQ_OPEN_RECEIPT = 5;
    Random rand;
    String subject = "retype subject", body = "insert content";
    private boolean endFlag = false;
    private ProgressDialog dialog;

    public SearchResultsListAdapter(Activity activity, Context context, int objectType,
                                    List<PriceOffer> priceOfferList, List<Customer> customerOfPOList,
                                    List<Work> workList, List <Receipt> receiptList,List<Customer> customerOfReceiptList, List<Expense>expenseList) {
        this.activity = activity;
        firebaseAuth = FirebaseAuth.getInstance();
        this.context = context;
        this.objectType = objectType;
        this.priceOfferList = priceOfferList;
        this.customerOfPOList =customerOfPOList;
        this.workList = workList;
        this.receiptList=receiptList;
        this.customerOfReceiptList=customerOfReceiptList;
        this.expenseList=expenseList;
        rand = new Random();
      //  Toast.makeText(activity, "SearchResultsListAdapter", Toast.LENGTH_LONG).show();
    }


    @Override
    public int getCount() {
        if (EnumObjectType.WORK.getValue() == objectType)
            return workList.size();
        if (EnumObjectType.PRICE_OFFER.getValue() == objectType)
            return priceOfferList.size();
        if (EnumObjectType.RECEIPT.getValue() == objectType)
            return receiptList.size();
        if (EnumObjectType.EXPENSE.getValue() == objectType)
            return expenseList.size();
        return 0;
    }

    @Override
    public Object getItem(int location) {
        if (EnumObjectType.PRICE_OFFER.getValue() == objectType)
            return priceOfferList.get(location);
        if (EnumObjectType.WORK.getValue() == objectType)
            return workList.get(location);
        if (EnumObjectType.RECEIPT.getValue() == objectType)
            return receiptList.get(location);
        if (EnumObjectType.EXPENSE.getValue() == objectType)
            return expenseList.get(location);
        return null;
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
        //    if (convertView == null) {
        //set inflater per object
        if (EnumObjectType.WORK.getValue() == objectType) {
            convertView = inflater.inflate(R.layout.row_work, null);
            setWorks(position, convertView);
        }
        if (EnumObjectType.PRICE_OFFER.getValue() == objectType) {
            convertView = inflater.inflate(R.layout.row_price_offer, null);
            setPriceOffers(position, convertView);
        }
        if (EnumObjectType.RECEIPT.getValue() == objectType) {
            convertView = inflater.inflate(R.layout.row_receipt, null);
            setReceipts(position, convertView);
        }
        if (EnumObjectType.EXPENSE.getValue() == objectType) {
            convertView = inflater.inflate(R.layout.row_expense, null);
            setExpenses(position, convertView);
        }
        //convertView = inflater.inflate(R.layout.row_search_result, null);
        //    }

        return convertView;
    }

    private void setReceipts(int position, View convertView) {
     //   Toast.makeText(context, "receiptList.size()"+receiptList.size(), Toast.LENGTH_LONG).show();

        final Receipt receipt = receiptList.get(position);
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
        while (i < customerOfReceiptList.size() && !customerOfReceiptList.get(i).getIdNum().equals(receipt.getCustomerIdNum())) {
            i++;
        }
        if (i < customerOfReceiptList.size()) {
            final Customer customer = customerOfReceiptList.get(i);

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
    }

    private void setExpenses(int position, View convertView) {
        Expense expense = expenseList.get(position);
        TextView sumDetails = (TextView) convertView.findViewById(R.id.txtSumDetails);
        TextView summary = (TextView) convertView.findViewById(R.id.summary);
        ImageView imgVw = (ImageView) convertView.findViewById(R.id.imgVw);

        int color = Color.argb(100, rand.nextInt(250), rand.nextInt(250), rand.nextInt(250));
        imgVw.setImageTintList(ColorStateList.valueOf(color));

        sumDetails.setText((expense.getSumDetails().equals("") ? "" : expense.getSumDetails() + " | ") +
                expense.getSumPayment() + " ש''ח ");
        //summary of details
        String tmpSummary = expense.dateToString();
        tmpSummary += (expense.getExpenseType() > 0) ? (" | " + expense.expenseTypeToString()) : "";
        summary.setText(tmpSummary);
    }

    private void setPriceOffers(int position, View convertView) {
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
        while (i < customerOfPOList.size() && !customerOfPOList.get(i).getIdNum().equals(priceOffer.getCustomerIdNum())) {
            i++;
        }
        if (i < customerOfPOList.size()) {
            Customer customer = new Customer();
            customer = customerOfPOList.get(i);
            name.setText("לקוח " + customer.getName());
            if (customer.getName().length() > 0)
                tvInitials.setText(customer.getName().substring(0, 1).toUpperCase());
        } else //no customer found
        {
            name.setText("לא נבחר לקוח");
            tvInitials.setText("NA");
        }


        summary.setText("הצעה: " + priceOffer.getWorkDetails() + "\n"
                + priceOffer.getSumPayment() + " ש''ח " + "\n" +
                "תאריך יעד: " + priceOffer.dueDateToString() + "\n" +
                "מיקום: " + ((!priceOffer.getLocation().equals("")) ?
                priceOffer.getLocation() : "לא הוכנס מיקום") + "\n" +
                ((priceOffer.isPriceOfferApproved()) ? "אושרה לביצוע" : "טרם אושרה לביצוע"));
        cbIsPriceOfferSent.setChecked(priceOffer.isPriceOfferSent());

        convertView.setTag(endFlag);
    }

    private void setWorks(int position, View convertView) {

        work = workList.get(position);
        LinearLayout rowLayout = (LinearLayout) convertView.findViewById(R.id.rowLayout);
        final ImageView imgCancelWork = (ImageView) convertView.findViewById(R.id.imgVwCancel);
        TextView txtWorkID = (TextView) convertView.findViewById(R.id.txtWorkID);
        TextView summary = (TextView) convertView.findViewById(R.id.summary);
        final CheckBox cbIsWorkDone = (CheckBox) convertView.findViewById(R.id.cbWorkDone);
        cbIsWorkDone.setTag(position); //MUST!!

        cbIsWorkDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                int pos = ((int) cbIsWorkDone.getTag());
                work = workList.get(pos);
                if (work.isWorkCanceled()) {
                    cbIsWorkDone.setChecked(false);
                    Toast.makeText(context, "לא ניתן לאשר ביצוע עבודה שבוטלה", Toast.LENGTH_LONG).show();
                } else {
                    work.setWorkDone(cbIsWorkDone.isChecked());
                    FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                            .updateWork(work, work.getIdNum());
                }
            }
        });

        imgCancelWork.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int pos = ((int) cbIsWorkDone.getTag());
                work = workList.get(pos);

                if (work.isWorkDone())
                    Toast.makeText(context, "לא ניתן לבטל עבודה שבוצעה", Toast.LENGTH_LONG).show();
                else if (work.isWorkCanceled())
                    Toast.makeText(context, "העבודה כבר מבוטלת", Toast.LENGTH_LONG).show();
                else {
                    work.setWorkCanceled(true);
                    FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                            .updateWork(work, work.getIdNum());
                    imgCancelWork.setBackgroundResource(R.drawable.disabled_cancel32); //set image to be disabled
                    Toast.makeText(context, "העבודה בוטלה", Toast.LENGTH_LONG).show();
                    AlertDialog confirmationDialog = openConfirmationDialog(imgCancelWork);
                    confirmationDialog.show();
                }
            }
        });

        //set values in rows:
        if (work.isWorkCanceled()) {
            txtWorkID.setText("שים לב - עבודה בוטלה");
            imgCancelWork.setBackgroundResource(R.drawable.disabled_cancel32); //set image to be disabled
            rowLayout.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.lightGrey, null));
        } else {
            txtWorkID.setText("");
            imgCancelWork.setBackgroundResource(R.drawable.cancel32); //set image to be enable
            rowLayout.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.colorWhite, null));
        }

        summary.setText("פרטים: " + work.getWorkDetails() + "\n" +
                "סה''כ: " + work.getSumPayment() + " ש''ח " + "\n" +
                "תאריך יעד: " + work.dueDateToString() + "\n" +
                "מיקום: " + ((!work.getLocation().equals("")) ? work.getLocation() : "לא הוכנס מיקום")
        );

        cbIsWorkDone.setChecked(work.isWorkDone());
    }


    private AlertDialog openConfirmationDialog(final ImageView imgCancelWork) {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(activity)
                .setTitle("מחיקה")
                .setMessage("האם אתה בטוח כי ברצונך לבטל את העבודה?")
                .setIcon(R.drawable.alert_32)
                .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        work.setWorkCanceled(true);
                        FirebaseHandler.getInstance(firebaseAuth.getCurrentUser().getUid())
                                .updateWork(work, work.getIdNum());
                        imgCancelWork.setBackgroundResource(R.drawable.disabled_cancel32); //set image to be disabled
                        Toast.makeText(context, "העבודה בוטלה", Toast.LENGTH_LONG).show();

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
                            AlertDialog confirmationDialog = openConfirmationDeleteDialog(receipt);
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

                    //    Toast.makeText(activity, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
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

                      //  Toast.makeText(activity, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
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
    private AlertDialog openConfirmationDeleteDialog(final Receipt receipt) {
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
