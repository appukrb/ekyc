package com.tcs.mmpl.customer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;

import java.util.ArrayList;

public class AlertAdapter extends BaseAdapter{
    private Context context;
    private LayoutInflater Layf;
    int viewid;
    ViewHolder holder = null;
    ArrayList<String> list_id,list_alerts,list_alertTime;
    ListView list;


    public AlertAdapter(Context context,
                        int viewid,	ArrayList<String> list_id , ArrayList<String> list_alerts,ListView list,ArrayList<String> list_alertTime) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.Layf = LayoutInflater.from(context);
        this.viewid= viewid;
        this.list_id=list_id;
        this.list = list;
        this.list_alerts=list_alerts;
        this.list_alertTime=list_alertTime;

    }



    @Override
    public int getCount() {
        return list_id.size();
    }

    @Override
    public Object getItem(int position) {
        return list_id.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = Layf.inflate(viewid, null);
            holder.txtAlerts = (TextView)convertView.findViewById(R.id.txtAlerts);
            holder.txtAlertTime = (TextView)convertView.findViewById(R.id.txtAlertTime);
            holder.chkDelete = (CheckBox)convertView.findViewById(R.id.chkDelete);



            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.txtAlerts.setText(list_alerts.get(position));
        holder.txtAlertTime.setText(list_alertTime.get(position));


//       list.setOnLongClickListener(new OnLongClickListener() {
//
//			@Override
//			public boolean onLongClick(View v) {
//				// TODO Auto-generated method stub
//
//				holder.btnDelete.setVisibility(View.VISIBLE);
//				return false;
//			}
//		});
//
//        holder.txtAlerts.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				holder.btnDelete.setVisibility(View.GONE);
//			}
//		});


        holder.chkDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                ListAdapter adapter = list.getAdapter();

                list_id.remove(position);
                list_alerts.remove(position);
                ((BaseAdapter) adapter).notifyDataSetChanged();
                ((BaseAdapter) adapter).notifyDataSetInvalidated();

            }
        });


        return convertView;
    }

    public class ViewHolder
    {
        TextView txtAlerts,txtAlertTime;
        public CheckBox chkDelete;

    }
}
