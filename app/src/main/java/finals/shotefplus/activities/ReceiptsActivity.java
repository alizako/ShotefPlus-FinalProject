package finals.shotefplus.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import finals.shotefplus.R;
import finals.shotefplus.adapters.CustomerListAdapter;
import finals.shotefplus.adapters.PriceOfferListAdapter;
import finals.shotefplus.adapters.ReceiptListAdapter;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.PriceOffer;
import finals.shotefplus.objects.Receipt;
import finals.shotefplus.objects.Work;

public class ReceiptsActivity extends AppCompatActivity {
    ListView lvReceipts;
    private List<Receipt> receiptList;
    private List<Customer> customerList;
    private ReceiptListAdapter adapter;
    View filter, barMonth;
    ImageButton btnAdd;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog dialog;
    DatabaseReference dbRef;
    Date date;
    private TextView txtNext, txtPrev, txtDate;
    static final int REQ_UPD_CUSTOMER = 3;
    static final int REQ_UPD_RECEIPT = 4;
    static final int REQ_FILTER = 1;
    static final int RESULT_CLEAN = 2;
    static final int REQ_OPEN_RECEIPT = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts);

        //init vars:
        firebaseAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseLink) +
                firebaseAuth.getCurrentUser().getUid() +
                getString(R.string.receiptsLink));


        // initList();

        barMonth = findViewById(R.id.barMonth);
        filter = findViewById(R.id.barFilter);
        btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        lvReceipts = (ListView) findViewById(R.id.listViewReceipts);
        txtNext = (TextView) barMonth.findViewById(R.id.txtNext);
        txtPrev = (TextView) barMonth.findViewById(R.id.txtPrev);
        txtDate = (TextView) barMonth.findViewById(R.id.txtMonth);

        date = new Date();
        setCurrentDateBarMonth();
        initFilter();

        try {
            dataRefHandling();

        } catch (Exception e) {
            dialog.dismiss();
            Toast.makeText(ReceiptsActivity.this,
                    "" + e.toString(),
                    Toast.LENGTH_LONG).show();
        }

        setEvents();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        TextView txtDetails = (TextView) filter.findViewById(R.id.txtDetails);

        if (requestCode == REQ_FILTER) {
            if (resultCode == Activity.RESULT_OK) {
                boolean isPaid = data.getBooleanExtra(getString(R.string.rbPaid), false);
                boolean isNotPaid = data.getBooleanExtra(getString(R.string.rbNotPaid), false);
                boolean isCash = data.getBooleanExtra(getString(R.string.cbCash), false);
                boolean isCheque = data.getBooleanExtra(getString(R.string.cbCheque), false);
                boolean isTrans = data.getBooleanExtra(getString(R.string.cbTrans), false);
                boolean isCredit = data.getBooleanExtra(getString(R.string.cbCredit), false);

                String filterTxt = (isPaid ? "שולם | " : "") +
                        (isNotPaid ? "לא שולם | " : "") +
                        (isCash ? "מזומן | " : "") +
                        (isCheque ? "המחאה |" : "") +
                        (isTrans ? "העברה |" : "") +
                        (isCredit ? "אשראי |" : "");

                txtDetails.setText(filterTxt);

                initListReceipts(isPaid, isNotPaid, isCash, isCheque, isTrans, isCredit);
            }

            if (resultCode == RESULT_CLEAN) {
                txtDetails.setText(getString(R.string.noFilter));
                dataRefHandling();
            }
        }
        if (requestCode == REQ_UPD_CUSTOMER) {
            dataRefHandling();
        }

        if (requestCode == REQ_OPEN_RECEIPT) {
            //
        }
    }//onActivityResult


    /**********************************************************************************
     * Events
     **********************************************************************************/

    private void setEvents() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReceiptsActivity.this, InsertReceiptActivity.class));
            }
        });


        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = new Date();
                setCurrentDateBarMonth();
                dataRefHandling();
            }
        });
        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.MONTH, 1);
                date = c.getTime();
                setCurrentDateBarMonth();
                dataRefHandling();
            }
        });
        txtPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.MONTH, -1);
                date = c.getTime();
                setCurrentDateBarMonth();
                dataRefHandling();
            }
        });


    }


    /**********************************************************************************
     * FireBase
     **********************************************************************************/
    private void dataRefHandling() {

        dialog = ProgressDialog.show(ReceiptsActivity.this,
                "",
                getString(R.string.loadMsg)
                , true);

        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormat));
        final String dateReceipt = dateFormat.format(date);

        dbRef.orderByChild(getString(R.string.dateReceiptDB))
                .startAt(dateReceipt)
                .endAt(dateReceipt + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot snapshot) {
                           receiptList = new ArrayList<Receipt>();
                           customerList = new ArrayList<Customer>();
                           final long[] pendingLoadCount = {snapshot.getChildrenCount()};

                           for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                               try {
                                   Receipt receipt = new Receipt();
                                   receipt = postSnapshot.getValue(Receipt.class);
                                   receiptList.add(receipt);

                                   //get Customer of current Receipts
                                   final DatabaseReference currentDdbRef = FirebaseDatabase.getInstance()
                                           .getReferenceFromUrl(getString(R.string.firebaseLink) +
                                                   firebaseAuth.getCurrentUser().getUid() +
                                                   getString(R.string.customersLink) +
                                                   receipt.getCustomerIdNum());

                                   currentDdbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                       public void onDataChange(DataSnapshot dataSnapshot) {
                                           Customer customer = new Customer();
                                           customer = dataSnapshot.getValue(Customer.class);
                                           if (customer != null) customerList.add(customer);
                                           // we loaded a child, check if we're done
                                           pendingLoadCount[0] = pendingLoadCount[0] - 1;
                                           if (pendingLoadCount[0] == 0) {
                                               adapter = new ReceiptListAdapter(ReceiptsActivity.this, receiptList, customerList);
                                               lvReceipts.setAdapter(adapter);
                                               dialog.dismiss();
                                           }
                                       }

                                       @Override
                                       public void onCancelled(DatabaseError firebaseError) {
                                           Toast.makeText(getBaseContext(),
                                                   getString(R.string.errorMsg)+ firebaseError.getMessage(),
                                                   Toast.LENGTH_LONG).show();
                                           dialog.dismiss();
                                       }
                                   });
                                   //END of get Customer of current Receipts


                               } catch (Exception ex) {
                                   Toast.makeText(getBaseContext(),
                                           getString(R.string.errorMsg) + ex.toString(),
                                           Toast.LENGTH_LONG).show();
                                   dialog.dismiss();
                               }
                           }
                           if (pendingLoadCount[0] == 0) {
                               adapter = new ReceiptListAdapter(ReceiptsActivity.this, receiptList, customerList);
                               lvReceipts.setAdapter(adapter);
                               dialog.dismiss();
                           }
                       }

                       @Override
                       public void onCancelled(DatabaseError firebaseError) {
                           Toast.makeText(getBaseContext(),
                                   getString(R.string.errorMsg) + firebaseError.getMessage(),
                                   Toast.LENGTH_LONG).show();
                           dialog.dismiss();
                       }

                   }

                );

    }

    /**********************************************************************************
     * private functions
     **********************************************************************************/
    //filter list of receipts
    private void initListReceipts(boolean isPaid, boolean isNotPaid, boolean isCash,
                                  boolean isCheque, boolean isTrans, boolean isCredit) {
        boolean tmpIsPaid = isPaid ? true : (isNotPaid ? false : false);
        boolean checkIsPaid = (isPaid == false && isNotPaid == false) ? false : true;

        boolean flagPT = false;
        int tmpPaymentType[] = {0, 0, 0, 0};
        if (isCash) {
            tmpPaymentType[0] = 1;
            flagPT = true;
        }
        if (isCredit) {
            tmpPaymentType[1] = 2;
            flagPT = true;
        }
        if (isCheque) {
            tmpPaymentType[2] = 3;
            flagPT = true;
        }
        if (isTrans) {
            tmpPaymentType[3] = 4;
            flagPT = true;
        }
        List<Receipt> listRTmp = new ArrayList<>();
        List<Customer> listCTmp = new ArrayList<>();
        for (int i = 0; i < receiptList.size(); i++) {
            Receipt receipt = receiptList.get(i);
            boolean tmpEqualsPaymentType = checkEqualsPaymentType(receipt, tmpPaymentType);
            boolean addtoList = false;

            if (checkIsPaid && receipt.isPaid() == tmpIsPaid) {
                if (flagPT && tmpEqualsPaymentType) {
                    addtoList = true;
                } else if (!flagPT) {
                    addtoList = true;
                }
            } else if (!checkIsPaid && flagPT && tmpEqualsPaymentType) {
                addtoList = true;
            }

            if (addtoList) {
                listRTmp.add(receipt);
                int j = 0;
                while (j < customerList.size() && !customerList.get(j).getIdNum().equals(receipt.getCustomerIdNum())) {
                    j++;
                }
                if (j < customerList.size()) {
                    listCTmp.add(customerList.get(j));
                }
            }
        }

        adapter = new ReceiptListAdapter(ReceiptsActivity.this, listRTmp, listCTmp);
        lvReceipts.setAdapter(adapter);
        dialog.dismiss();
    }

    private boolean checkEqualsPaymentType(Receipt receipt, int[] tmpPaymentType) {
        for (int i = 0; i < tmpPaymentType.length; i++) {
            if (receipt.getPaymentType() == tmpPaymentType[i])
                return true;
        }
        return false;
    }

    private void initFilter() {
        filter = findViewById(R.id.barFilter);
        ImageButton imgBtnFilter = (ImageButton) filter.findViewById(R.id.imgbtnFilter);
        imgBtnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReceiptsActivity.this, FilterReceiptActivity.class);
                //startActivity(new Intent(PriceOffersActivity.this, FilterPriceOfferActivity.class));
                startActivityForResult(intent, 1);
            }
        });
    }

    private void setCurrentDateBarMonth() {
        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormatMonthBar));
        txtDate.setText(dateFormat.format(date));
    }


}
