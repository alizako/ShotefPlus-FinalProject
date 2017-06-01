package finals.shotefplus.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import finals.shotefplus.DataAccessLayer.FirebaseHandler;
import finals.shotefplus.R;
import finals.shotefplus.objects.PriceOffer;
import finals.shotefplus.objects.Work;

/**
 * Created by Aliza on 04/05/2017.
 */

public class WorkListAdapter extends BaseAdapter {

    Context context;
    private Activity activity;
    private LayoutInflater inflater;
    private List<Work> workList;
    Work work;
    private FirebaseAuth firebaseAuth;

    public WorkListAdapter(Activity activity, List<Work> workList, Context context) {
        this.activity = activity;
        this.inflater = inflater;
        this.workList = workList;
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public int getCount() {
        return workList.size();
    }

    @Override
    public Object getItem(int location) {
        return workList.get(location);
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
            convertView = inflater.inflate(R.layout.row_work, null);

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

        return convertView;
    }

}
