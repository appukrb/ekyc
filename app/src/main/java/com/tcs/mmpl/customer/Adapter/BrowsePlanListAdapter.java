package com.tcs.mmpl.customer.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;

import java.util.ArrayList;

/**
 * Created by hp on 23-10-2015.
 */
public class BrowsePlanListAdapter extends ArrayAdapter<String> {

    private static final int MODE_PRIVATE = 0;
    Context context;
    ListView listView;
    int layout_id;


    ArrayList<String> amount,description,validity ;


    public BrowsePlanListAdapter(Context context, int resource,
                              ListView lstView,ArrayList<String> amount,ArrayList<String> description,ArrayList<String> validity ) {
        super(context, resource);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.layout_id=resource;
        this.listView= lstView;
        // this.message=message;
        this.amount=amount;
        this.description=description;
        this.validity=validity;


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


            TextView txtPlanAmount = (TextView) rowView.findViewById(R.id.txtPlanAmount);
            TextView txtPlanDesc = (TextView) rowView.findViewById(R.id.txtPlanDesc);

            txtPlanAmount.setText(amount.get(position));
            txtPlanDesc.setText(description.get(position));
//
//            txtPlanAmount.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//
//                    Intent returnIntent = new Intent();
//                    returnIntent.putExtra("result", amount.get(position));
//                    ((Activity) context).setResult(1, returnIntent);
//                    ((Activity) context).finish();
//                }
//            });
//
//            txtPlanDesc.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Intent returnIntent = new Intent();
//                    returnIntent.putExtra("result",amount.get(position));
//                    ((Activity) context).setResult(1, returnIntent);
//                    ((Activity) context).finish();
//
//
//                }
//            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",amount.get(position));
                    ((Activity) context).setResult(1, returnIntent);
                    ((Activity) context).finish();
                }
            });


            return rowView;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        }
    }

}
