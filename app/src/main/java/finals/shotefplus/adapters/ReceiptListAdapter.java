package finals.shotefplus.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import finals.shotefplus.R;
import finals.shotefplus.objects.Receipt;

/**
 * Created by Aliza on 15/01/2017.
 */

public class ReceiptListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Receipt> receiptsList;

    public ReceiptListAdapter(Activity activity,  List<Receipt> receiptsList) {
        this.activity = activity;
        this.receiptsList = receiptsList;
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


        Receipt receipt = receiptsList.get(position);
        TextView txtName = (TextView) convertView.findViewById(R.id.txtName);
        TextView txtReceipt = (TextView) convertView.findViewById(R.id.txtReceipt);
        TextView summary = (TextView) convertView.findViewById(R.id.summary);

       txtName.setText(receipt.getWork().getPriceOffer().getCustomer().getName() + " | " +
               receipt.getWork().getPriceOffer().getCustomer().getIdNum());
        txtReceipt.setText("קבלה "+receipt.getIdNum());
        //summary of details
        summary.setText(receipt.getDate()+ " | " +
                +receipt.getSumPaymentMaam() + " | " +
        " שולם ב" + receipt.getStringPaymentType(receipt.getPaymentType()));

        return convertView;

    }
}
