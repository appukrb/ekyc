package com.tcs.mmpl.customer.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;

import java.util.ArrayList;

/**
 * Created by hp on 07-10-2015.
 */
public class PlanListAdapter extends ArrayAdapter<String> {

    private static final int MODE_PRIVATE = 0;
    Context context;
    ListView listView;
    int layout_id;

    // ArrayList<String> message;
    ArrayList<String> amount;
    ArrayList<String> desc;
//    ArrayList<String> day;
//    ArrayList<String> date;
//    ArrayList<String> month;
//    ArrayList<String> bestSellerFlag;

    public PlanListAdapter(Context context, int resource,
                              ListView lstView,ArrayList<String> amount,ArrayList<String> desc) {
        super(context, resource);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.layout_id=resource;
        this.listView= lstView;
        // this.message=message;
        this.amount=amount;
        this.desc=desc;


    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return amount.size();
    }



    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        try {


            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            View rowView = inflater.inflate(layout_id, null, true);


            TextView txtDesc = (TextView) rowView.findViewById(R.id.txtPlanDesc);
            TextView txtAmount = (TextView) rowView.findViewById(R.id.txtPlanAmount);



            txtAmount.setText(amount.get(position));
            txtDesc.setText(desc.get(position));



            return rowView;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        }
    }

}
