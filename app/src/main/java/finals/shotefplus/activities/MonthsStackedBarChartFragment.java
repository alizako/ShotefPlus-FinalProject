package finals.shotefplus.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import finals.shotefplus.MPcharts.DayAxisValueFormatter;
import finals.shotefplus.MPcharts.MyAxisValueFormatter;
import finals.shotefplus.MPcharts.MyValueFormatter;
import finals.shotefplus.R;
import finals.shotefplus.objects.Expense;
import finals.shotefplus.objects.Receipt;


public class MonthsStackedBarChartFragment extends Fragment implements OnChartValueSelectedListener {

    public static final int[] MATERIAL_COLORS = {
            ColorTemplate.rgb("#2ecc71"), /*ColorTemplate.rgb("#f1c40f"),*/ ColorTemplate.rgb("#e74c3c"), ColorTemplate.rgb("#3498db")
    };

    private BarChart mChart;
    private List<Receipt> receiptList;
    private FirebaseAuth firebaseAuth;
    DatabaseReference dbRef;
    private ProgressDialog dialog;
    private double[] monthPayments, monthNotPaid, monthExpense;
    TextView txtEntryMonth, txtEntryIncome, txtEntryNotPaid, txtEntryExpense, txtYear, txtNext, txtPrev;
    View view, barYear;
    int year;
     boolean flagIsValExists = false;
    static final String TITLE ="תצוגה חודשית   |   שנת ";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_months_stacked_bar_chart, container, false);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);


        firebaseAuth = FirebaseAuth.getInstance();

        txtEntryMonth = (TextView) view.findViewById(R.id.txtEntryMonth);
        txtEntryIncome = (TextView) view.findViewById(R.id.txtEntryIncome);
        txtEntryNotPaid = (TextView) view.findViewById(R.id.txtEntryNotPaid);
        txtEntryExpense = (TextView) view.findViewById(R.id.txtEntryExpense);

        barYear =  view.findViewById(R.id.barYear);

        txtYear = (TextView) barYear.findViewById(R.id.txtYear);
        txtPrev = (TextView) barYear.findViewById(R.id.txtPrev);
        txtNext = (TextView) barYear.findViewById(R.id.txtNext);

        monthPayments = new double[12];
        monthNotPaid = new double[12];
        monthExpense = new double[12];

        /*if (container != null) {
            Bundle args = this.getArguments();
            year = args.getInt("yearFragment",2017);*/
        Calendar now = Calendar.getInstance();   // Gets the current date and time
        year = now.get(Calendar.YEAR);
        txtYear.setText(TITLE + year);
        getValuesFireBaseIncome();

        setEvents();

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setEvents() {
        txtYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                year = now.get(Calendar.YEAR);
                txtYear.setText(TITLE + year);
                resetMonthsArrays();
                getValuesFireBaseIncome();
            }
        });
        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year += 1;
                txtYear.setText(TITLE + year);
                resetMonthsArrays();
                getValuesFireBaseIncome();
            }
        });
        txtPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year -= 1;
                txtYear.setText(TITLE + year);
                resetMonthsArrays();
                getValuesFireBaseIncome();
            }
        });
    }

    private void resetMonthsArrays() {
        for (int i=0;i<12;i++) {
            monthNotPaid[i] = 0;
            monthPayments[i] = 0;
            monthExpense[i] = 0;
        }
    }

    private void setChart() {

        mChart = (BarChart) view.findViewById(R.id.chartMonths);
        mChart.setOnChartValueSelectedListener(this);
        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(40);

        // scaling can  be done on x- and y-axis together
        mChart.setPinchZoom(true);

        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);

        mChart.setDrawValueAboveBar(false);
        mChart.setHighlightFullBarEnabled(false);

        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);


        IAxisValueFormatter custom = new MyAxisValueFormatter();


        // change the position of the y-labels
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setValueFormatter(custom);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setStartAtZero(false);
        mChart.getAxisRight().setEnabled(false);

        /*XAxis xLabels = mChart.getXAxis();
        xLabels.setPosition(XAxis.XAxisPosition.TOP);*/

        // mChart.setDrawXLabels(false);
        // mChart.setDrawYLabels(false);


        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);

        setData();
    }

    private void setData() {
        {
            ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

            for (int i = 0; i < 12; i++) {
                float mult = (5000); //Todo!
               /* float val1 = (float) (Math.random() * mult) + mult / 3;
                float val2 = (float) (Math.random() * mult) + mult / 3;
                float val3 = (float) (Math.random() * mult) + mult / 3;
*/
                float valMonthPayments = (float) (monthPayments[i]);
                float valMonthNotPaid = (float) (monthNotPaid[i]);
                float valMonthExpense = (float) (monthExpense[i]);

                yVals1.add(new BarEntry(i, new float[]{valMonthPayments, valMonthNotPaid, valMonthExpense}));
            }
            BarDataSet set1;

            if (mChart.getData() != null &&
                    mChart.getData().getDataSetCount() > 0) {
                set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
                set1.setValues(yVals1);
                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();
            } else {
                set1 = new BarDataSet(yVals1, "  ");
                set1.setColors(getColors());
                set1.setStackLabels(new String[]{"הכנסה ששולמה", "הכנסה שטרם שולמה", "הוצאות"});

                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                dataSets.add(set1);

                BarData data = new BarData(dataSets);
                data.setValueFormatter(new MyValueFormatter());
                data.setValueTextColor(Color.WHITE);

                mChart.setData(data);
            }

            mChart.setFitBars(true);
            mChart.invalidate();
        }
    }

    private int[] getColors() {

        int stacksize = 3;
        int[] colors = new int[stacksize];

        for (int i = 0; i < colors.length; i++) {
            colors[i] = MATERIAL_COLORS[i];
        }

        return colors;
    }

    private void getValuesFireBaseIncome() {

        dialog = ProgressDialog.show(getActivity(),
                "", "טוען נתונים..", true);

        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseLink) +
                        firebaseAuth.getCurrentUser().getUid() +
                        getString(R.string.receiptsLink));

        flagIsValExists = false;//only here needed!

        dbRef.orderByChild("yearPay").equalTo(year)
                .addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot snapshot) {

                               final long[] pendingLoadCount = {snapshot.getChildrenCount()};
                               for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                                   try {
                                       Receipt receipt = new Receipt();
                                       receipt = postSnapshot.getValue(Receipt.class);

                                       if (receipt.isPaid()) {
                                           monthPayments[receipt.getMonthPay() - 1] += receipt.getSumPayment();
                                       } else {
                                           monthNotPaid[receipt.getMonthPay() - 1] += receipt.getSumPayment();
                                       }
                                       pendingLoadCount[0] = pendingLoadCount[0] - 1;
                                       /*if (pendingLoadCount[0] == 0) {
                                           flagIsValExists=true;
                                           getValuesFireBaseExpense(); //only when thread ends can call next firebase retrieve
                                       }*/

                                   } catch (Exception ex) {
                                       Toast.makeText(getActivity(),
                                               getString(R.string.errorMsg) + ex.toString(),
                                               Toast.LENGTH_LONG).show();
                                       dialog.dismiss();
                                   }
                               }
                               if (pendingLoadCount[0] == 0) {
                                   getValuesFireBaseExpense();
                               }
                           }

                           @Override
                           public void onCancelled(DatabaseError firebaseError) {
                               Toast.makeText(getActivity(),
                                       getString(R.string.errorMsg)+ firebaseError.getMessage(),
                                       Toast.LENGTH_LONG).show();
                               dialog.dismiss();
                           }
                       }
                );

    }

    private void getValuesFireBaseExpense() {
        //get Expenses:
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getString(R.string.firebaseLink) +
                        firebaseAuth.getCurrentUser().getUid() +
                        getString(R.string.expensesLink));

        dbRef.orderByChild("yearPay").equalTo(year)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        final long[] pendingLoadCountExp = {snapshot.getChildrenCount()};
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Expense expense = new Expense();
                            expense = postSnapshot.getValue(Expense.class);
                            flagIsValExists=true;
                            monthExpense[expense.getMonthPay() - 1] += expense.getSumPayment();

                            pendingLoadCountExp[0] = pendingLoadCountExp[0] - 1;
                            if (pendingLoadCountExp[0] == 0) {
                                setChart();
                                dialog.dismiss();
                            }
                        }
                        if (pendingLoadCountExp[0] == 0) {
                            if (!flagIsValExists) {
                                setChart();
                            }
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Toast.makeText(getActivity(),
                                getString(R.string.errorMsg)+ firebaseError.getMessage(),
                                Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }

                });
        //END Expenses
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        BarEntry entry = (BarEntry) e;
        int month = (int) entry.getX();

        txtEntryMonth.setText(getMonthToString(month + 1));
        txtEntryIncome.setText(Double.toString(monthPayments[month]) + "  ש''ח");
        txtEntryNotPaid.setText(Double.toString(monthNotPaid[month]) + "  ש''ח");
        txtEntryExpense.setText(Double.toString(monthExpense[month]) + "  ש''ח");
    }

    private String getMonthToString(int month) {
        switch (month) {
            case 1:
                return "ינואר";
            case 2:
                return "פברואר";
            case 3:
                return "מרץ";
            case 4:
                return "אפריל";
            case 5:
                return "מאי";
            case 6:
                return "יוני";
            case 7:
                return "יולי";
            case 8:
                return "אוגוסט";
            case 9:
                return "ספטמבר";
            case 10:
                return "אוקטובר";
            case 11:
                return "נובמבר";
            case 12:
                return "דצמבר";
            default:
                return "";

        }
    }

    @Override
    public void onNothingSelected() {

    }


}
