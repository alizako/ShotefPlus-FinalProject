package finals.shotefplus.activities;



import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.support.v4.app.FragmentTransaction;
import finals.shotefplus.R;


public class IncomesVsExpensesActivity extends AppCompatActivity {

   // Fragment frgYearlyChart, frgMonthlyChart;
    android.support.v4.app.FragmentManager fm;
    Button btnYear, btnMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incomes_vs_expenses);

        btnMonth = (Button)findViewById(R.id.btnMonth);
        btnYear = (Button)findViewById(R.id.btnYear);

        setFragment( new YearHalfPieChartFragment());

        btnMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment( new MonthsStackedBarChartFragment());
            }
        });

        btnYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment( new YearHalfPieChartFragment());
            }
        });


        /*fm = this.getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putInt("yearFragment",2017);

        //  frgYearlyChart=new YearHalfPieChartFragment();
      //  frgMonthlyChart = new MonthsStackedBarChartFragment();

        MonthsStackedBarChartFragment monthFrg = new MonthsStackedBarChartFragment();
        monthFrg.setArguments(bundle);
        frgMonthlyChart = monthFrg;

        *//*getSupportFragmentManager().beginTransaction()
                .replace(frgMonthlyChart.getId(),monthFrg).commit();*//*
        fm.beginTransaction()
                .replace((R.id.frgMonthlyChart),monthFrg )
                .commit();*/



    }

    protected void setFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.containerFragment, fragment);
        ft.commit();
    }

}
