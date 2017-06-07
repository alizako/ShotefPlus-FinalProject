package finals.shotefplus.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import finals.shotefplus.R;
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

    private FirebaseAuth firebaseAuth;
    private Activity activity;
    private LayoutInflater inflater;
    private Customer customer;
    private PriceOffer priceOffer;
    private Work work;
    private Receipt receipt;
    private List<LinkedObjects> linkedObjectsList;
    private LinkedObjects linkedObjects;
    private List<Expense> expenseList;
    ListView lvExpenses;

    public SearchResultsListAdapter(Activity activity, LinkedObjects linkedObjects) {
        // List<LinkedObjects> linkedObjectsList) {
        this.activity = activity;
      //  this.linkedObjectsList = linkedObjectsList;
        this.linkedObjects = linkedObjects;
        firebaseAuth = FirebaseAuth.getInstance();
        lvExpenses = (ListView) activity.findViewById(R.id.lvExpenses);
        Toast.makeText(activity, "SearchResultsListAdapter", Toast.LENGTH_LONG).show();
    }

    @Override
    public int getCount() {
        return 0;//linkedObjectsList.size();
    }

    @Override
    public Object getItem(int position) {
        return linkedObjectsList.get(position);
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
            convertView = inflater.inflate(R.layout.row_search_result, null);

        return convertView;
    }
}
