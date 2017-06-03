package finals.shotefplus.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import finals.shotefplus.R;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.Expense;

import static finals.shotefplus.R.string.customers;

/**
 * Created by Aliza on 15/01/2017.
 */

public class ExpenseListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Expense> expenseList;

    public ExpenseListAdapter(Activity activity, List<Expense> expenseList) {
        this.activity = activity;
        this.expenseList = expenseList;
    }

    @Override
    public int getCount() {
        return expenseList.size();
    }

    @Override
    public Object getItem(int location) {
        return expenseList.get(location);
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
            convertView = inflater.inflate(R.layout.row_expense, null);


        Expense expense = expenseList.get(position);
        TextView sumDetails = (TextView) convertView.findViewById(R.id.txtSumDetails);
        TextView summary = (TextView) convertView.findViewById(R.id.summary);

        sumDetails.setText((expense.getSumDetails().equals("") ? "" : expense.getSumDetails() + " | ") +
                expense.getSumPayment() + " ש''ח ");
        //summary of details
        String tmpSummary = expense.dateToString();
        tmpSummary += (expense.getExpenseType() > 0) ? (" | " + expense.expenseTypeToString()) : "";
        summary.setText(tmpSummary);

        return convertView;

    }
}
