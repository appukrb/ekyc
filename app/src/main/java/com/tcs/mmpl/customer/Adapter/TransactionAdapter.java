package com.tcs.mmpl.customer.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;

import java.util.ArrayList;

/**
 * Created by hp on 22-09-2015.
 */
public class TransactionAdapter extends ArrayAdapter<String> {

    private static final int MODE_PRIVATE = 0;
    Context context;
    ListView listView;
    int layout_id;

    // ArrayList<String> message;
    ArrayList<String> amount;
    ArrayList<String> desc;
    ArrayList<String> day;
    ArrayList<String> date;
    ArrayList<String> month;
    ArrayList<String> year;
    ArrayList<String> type;
//    ArrayList<String> bestSellerFlag;

    public TransactionAdapter(Context context, int resource,
                              ListView lstView, ArrayList<String> amount, ArrayList<String> desc, ArrayList<String> date, ArrayList<String> day, ArrayList<String> month, ArrayList<String> year, ArrayList<String> type) {
        super(context, resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.layout_id = resource;
        this.listView = lstView;
        // this.message=message;
        this.amount = amount;
        this.desc = desc;
        this.day = day;
        this.date = date;
        this.month = month;
        this.year = year;
        this.type = type;


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


            TextView txtDesc = (TextView) rowView.findViewById(R.id.txtDesc);
            TextView txtAmount = (TextView) rowView.findViewById(R.id.txtAmount);
            //  TextView txtDay=(TextView)rowView.findViewById(R.id.txtDay);
            TextView txtDate = (TextView) rowView.findViewById(R.id.txtDate);
            ImageView imgMode = (ImageView) rowView.findViewById(R.id.imgMode);
            // TextView txtMonth=(TextView)rowView.findViewById(R.id.txtMonth);


            txtAmount.setText(context.getResources().getString(R.string.rupee_symbol)+amount.get(position));
            txtDesc.setText(desc.get(position));
            txtDate.setText(date.get(position) + " " + month.get(position) + " " + year.get(position));
            if(type.isEmpty())
                imgMode.setImageResource(R.drawable.minus_mode);
            else if (type.get(position).equalsIgnoreCase("credit"))
                imgMode.setImageResource(R.drawable.plus_mode);
            else if (type.get(position).equalsIgnoreCase("debit"))
                imgMode.setImageResource(R.drawable.minus_mode);


//            txtDay.setText(day.get(position));
//            txtMonth.setText(month.get(position));


            return rowView;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        }
    }

}
