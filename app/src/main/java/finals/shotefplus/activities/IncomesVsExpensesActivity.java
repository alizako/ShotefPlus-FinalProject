package finals.shotefplus.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Display;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import finals.shotefplus.R;
import finals.shotefplus.adapters.ReceiptListAdapter;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.Receipt;

public class IncomesVsExpensesActivity extends AppCompatActivity {

    private PieChart chartHalfPie;
    protected String[] mParties = new String[]{
            "הכנסה ששולמה", "הכנסה שטרם שולמה", "הוצאות"};

    public static final int[] LIBERTY_COLORS = {
            /*Color.rgb(207, 248, 246), Color.rgb(148, 212, 212), */Color.rgb(136, 180, 187),
            Color.rgb(118, 174, 175), Color.rgb(42, 109, 130)
    };
    public static final int[] MATERIAL_COLORS = {
            ColorTemplate.rgb("#2ecc71"), /*ColorTemplate.rgb("#f1c40f"),*/ ColorTemplate.rgb("#e74c3c"), ColorTemplate.rgb("#3498db")
    };

    private List<Receipt> receiptList;
    private FirebaseAuth firebaseAuth;
    DatabaseReference dbRef;
    private ProgressDialog dialog;

    private double yearlyPayments, yearlyNotPaid, yearlyAvg;
    TextView txtEntryIncome, txtEntryAvg, txtEntryNotPaid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_incomes_vs_expenses);

        firebaseAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://shotefplus-72799.firebaseio.com/Users/" +
                        firebaseAuth.getCurrentUser().getUid() + "/Receipts/");

        getValuesFireBase();
        // setChart();
    }

    private void getValuesFireBase() {
        dialog = ProgressDialog.show(IncomesVsExpensesActivity.this,
                "", "טוען נתונים..", true);
        int year = 2017;//Todo: change tmp year..
        dbRef.orderByChild("yearPay").equalTo(year)
                .addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot snapshot) {
                           receiptList = new ArrayList<Receipt>();
                           final long[] pendingLoadCount = {snapshot.getChildrenCount()};
                           for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                               try {
                                   Receipt receipt = new Receipt();
                                   receipt = postSnapshot.getValue(Receipt.class);
                                   //if(receipt.getYearPay().)
                                   receiptList.add(receipt);
                                   yearlyPayments += receipt.isPaid() ? receipt.getSumPayment() : 0;
                                   yearlyNotPaid += receipt.isPaid() ? 0 : receipt.getSumPayment();

                                   pendingLoadCount[0] = pendingLoadCount[0] - 1;
                                   if (pendingLoadCount[0] == 0) {
                        /*adapter = new ReceiptListAdapter(IncomesVsExpensesActivity.this, receiptList, customerList);
                        lvReceipts.setAdapter(adapter);*/
                                       yearlyAvg = (yearlyNotPaid + yearlyPayments) / 12;

                                       setChart();
                                       setValuesToTV();
                                       dialog.dismiss();
                                   }
                               } catch (Exception ex) {
                                   Toast.makeText(getBaseContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
                               }
                           }
                           if (pendingLoadCount[0] == 0) {
                        /*adapter = new ReceiptListAdapter(IncomesVsExpensesActivity.this, receiptList, customerList);
                        lvReceipts.setAdapter(adapter);*/
                               yearlyAvg = (yearlyNotPaid + yearlyPayments) / 12;

                               setChart();
                               setValuesToTV();
                               dialog.dismiss();
                           }
                       }

                       @Override
                       public void onCancelled(DatabaseError firebaseError) {
                           Toast.makeText(getBaseContext(), "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                           dialog.dismiss();
                       }
                   }
                );
    }

    private void setValuesToTV() {
        txtEntryIncome = (TextView) findViewById(R.id.txtEntryIncome);
        txtEntryNotPaid = (TextView) findViewById(R.id.txtEntryNotPaid);
        txtEntryAvg = (TextView) findViewById(R.id.txtEntryAvg);

        txtEntryIncome.setText(String.format("%.2f", yearlyPayments) + "  ש''ח");
        txtEntryNotPaid.setText(String.format("%.2f", yearlyNotPaid) + "  ש''ח");
        txtEntryAvg.setText(String.format("%.2f", yearlyAvg) + "  ש''ח");

        /*txtEntryIncome.setText(Double.toString(yearlyPayments) + "  ש''ח");
        txtEntryNotPaid.setText(Double.toString(yearlyNotPaid) + "  ש''ח");
        txtEntryAvg.setText(Double.toString(yearlyAvg) + "  ש''ח");*/
    }

    private void setChart() {
        chartHalfPie = (PieChart) findViewById(R.id.chartHalfPie);
        chartHalfPie.setBackgroundColor(Color.WHITE);

        moveOffScreen();

        chartHalfPie.setUsePercentValues(true);
        chartHalfPie.getDescription().setEnabled(false);

        //  mChart.setCenterTextTypeface(mTfLight);
        chartHalfPie.setCenterText(generateCenterSpannableText());

        chartHalfPie.setDrawHoleEnabled(true);
        chartHalfPie.setHoleColor(Color.WHITE);

        chartHalfPie.setTransparentCircleColor(Color.WHITE);
        chartHalfPie.setTransparentCircleAlpha(110);

        chartHalfPie.setHoleRadius(50f);
        chartHalfPie.setTransparentCircleRadius(55f);

        chartHalfPie.setDrawCenterText(true);

        chartHalfPie.setRotationEnabled(false);
        chartHalfPie.setHighlightPerTapEnabled(true);

        chartHalfPie.setMaxAngle(180f); // HALF CHART
        chartHalfPie.setRotationAngle(180f);
        chartHalfPie.setCenterTextOffset(0, -20);

        setData(mParties.length, 100);

        chartHalfPie.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = chartHalfPie.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        chartHalfPie.setEntryLabelColor(Color.WHITE);
        //   mChart.setEntryLabelTypeface(mTfRegular);
        chartHalfPie.setEntryLabelTextSize(12f);
    }

    private void setData(int count, float range) {

        ArrayList<PieEntry> values = new ArrayList<PieEntry>();

        /*for (int i = 0; i < count; i++) {
            values.add(new PieEntry(33, mParties[i % mParties.length]));
            //values.add(new PieEntry((float) ((Math.random() * range) + range / 5), mParties[i % mParties.length]));
        }*/

        values.add(new PieEntry((float) yearlyPayments, mParties[0 % mParties.length]));
        values.add(new PieEntry((float) yearlyNotPaid, mParties[1 % mParties.length]));
        values.add(new PieEntry((float) yearlyAvg, mParties[2 % mParties.length]));

        PieDataSet dataSet = new PieDataSet(values, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(MATERIAL_COLORS);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        // data.setValueTypeface(mTfLight);
        chartHalfPie.setData(data);

        chartHalfPie.invalidate();
    }

    private SpannableString generateCenterSpannableText() {
        int i = 14;
        int j = 15;
        String str = "MPAndroidChart\ndeveloped by Philipp Jahoda";
        SpannableString s = new SpannableString(str);
        s.setSpan(new RelativeSizeSpan(1f), 0, i, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), i, s.length() - j, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), i, s.length() - j, 0);
        s.setSpan(new RelativeSizeSpan(.6f), i, s.length() - j, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - j, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - i, s.length(), 0);
        return s;
    }

    private void moveOffScreen() {

        Display display = getWindowManager().getDefaultDisplay();
        int height = display.getHeight();  // deprecated

        int offset = (int) (height * 0.4); /* percent to move */

        RelativeLayout.LayoutParams rlParams =
                (RelativeLayout.LayoutParams) chartHalfPie.getLayoutParams();
        rlParams.setMargins(5, 5, 5, -offset);
        chartHalfPie.setLayoutParams(rlParams);
    }

    /*public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }*/
}
