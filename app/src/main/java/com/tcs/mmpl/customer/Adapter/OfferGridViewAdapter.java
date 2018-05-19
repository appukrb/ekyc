package com.tcs.mmpl.customer.Adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tcs.mmpl.customer.utility.Offers;
import com.tcs.mmpl.customer.R;

import java.util.ArrayList;

//import com.squareup.picasso.Picasso;

public class OfferGridViewAdapter extends ArrayAdapter<Offers> {

    //private final ColorMatrixColorFilter grayscaleFilter;
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<Offers> mGridData = new ArrayList<Offers>();
    int width;

    LinearLayout.LayoutParams layoutParams;

    public OfferGridViewAdapter(Context mContext, int layoutResourceId, ArrayList<Offers> mGridData, int width, LinearLayout.LayoutParams layoutParams) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
        this.width = width;
        this.layoutParams = layoutParams;
    }


    public void setGridData(ArrayList<Offers> mGridData) {
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
            holder.titleTextView = (TextView) row.findViewById(R.id.txtOffer);
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
            holder.imageView.setLayoutParams(layoutParams);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Offers item = mGridData.get(position);
        holder.titleTextView.setText(Html.fromHtml(item.getMerchantAboutMe()));

        Glide.with(mContext).load(item.getCouponImage()).into(holder.imageView);
        return row;
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }
}