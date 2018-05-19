package com.tcs.mmpl.customer.Adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tcs.mmpl.customer.R;

import java.util.ArrayList;


public class GridViewAdapter extends ArrayAdapter<MerchantImageAndName> {

    //private final ColorMatrixColorFilter grayscaleFilter;
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<MerchantImageAndName> mGridData = new ArrayList<MerchantImageAndName>();
   
    int width;
    
    LinearLayout.LayoutParams layoutParams;

    public GridViewAdapter(Context mContext, int layoutResourceId, ArrayList<MerchantImageAndName> mGridData,int width,LinearLayout.LayoutParams layoutParams) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
        this.width = width;
        this.layoutParams = layoutParams;
    }


    /**
     * Updates grid data and refresh grid items.
     *
     * @param mGridData
     */
    public void setGridData(ArrayList<MerchantImageAndName> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();

            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
            holder.grid_item_text = (TextView)row.findViewById(R.id.grid_item_text);
           // holder.imageView.setLayoutParams(layoutParams);
           
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        MerchantImageAndName item = mGridData.get(position);
        holder.grid_item_text.setText(item.getCategoryName());
      //  holder.titleTextView.setText("");



        Picasso.with(mContext).load(item.getImage()).into(holder.imageView);
        return row;
    }

    static class ViewHolder {
        TextView grid_item_text;
        ImageView imageView;
    }
}