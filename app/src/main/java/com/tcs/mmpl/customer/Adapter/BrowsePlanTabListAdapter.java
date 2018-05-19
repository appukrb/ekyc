package com.tcs.mmpl.customer.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
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
 * Created by hp on 11-11-2015.
 */
public class BrowsePlanTabListAdapter extends ArrayAdapter<String> {

    private static final int MODE_PRIVATE = 0;
    Context context;
    ListView listView;
    int layout_id;
    ArrayList<String> packdata = new ArrayList<String>();
    ArrayList<String> packageid=new ArrayList<String>();
    ProgressDialog pDialog;
    String str_json_msg;

    public static final int RESULT_OK = -1;

    public BrowsePlanTabListAdapter(Context context, int resource,
                             ListView lstView,ArrayList<String> packdata,ArrayList<String> packageid) {
        super(context, resource);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.layout_id=resource;
        this.listView= lstView;
        this.packdata=packdata;
        this.packageid=packageid;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return packdata.size() ;
    }



    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        try {


            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            View rowView = inflater.inflate(layout_id, null, true);
            TextView text_button =(TextView)rowView.findViewById(R.id.textView_button_green);
            TextView txt_days = (TextView) rowView.findViewById(R.id.textview_description_addonpacks);
            txt_days.setText(packdata.get(position));
            text_button.setText(packageid.get(position));

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", packageid.get(position));
                    if ( ((Activity)context).getParent() == null) {
                        ((Activity)context).setResult(3, returnIntent);
                    } else {
                        ((Activity)context).getParent().setResult(3, returnIntent);
                    }

                    ((Activity)context).finish();
                }
            });

//            text_button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent returnIntent = new Intent();
//                    returnIntent.putExtra("result", packageid.get(position));
//                    if ( ((Activity)context).getParent() == null) {
//                        ((Activity)context).setResult(3, returnIntent);
//                    } else {
//                        ((Activity)context).getParent().setResult(3, returnIntent);
//                    }
//
//                    ((Activity)context).finish();
//                }
//            });
//
//            txt_days.setOnClickListener(new View.OnClickListener() {
//
//
//                @Override
//                public void onClick(View v) {
//                    Intent returnIntent = new Intent();
//                    returnIntent.putExtra("result",packageid.get(position));
//
//
//                    if ( ((Activity)context).getParent() == null) {
//                        ((Activity)context).setResult(3, returnIntent);
//                    } else {
//                        ((Activity)context).getParent().setResult(3, returnIntent);
//                    }
//
//                   // ((Activity)context).setResult(3, returnIntent);
//                    ((Activity)context).finish();
//                }
//            });


            return rowView;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        }
    }



}
