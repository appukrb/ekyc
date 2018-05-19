package com.tcs.mmpl.customer.Hamburger;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;

import java.util.List;

public class DrawerItemCustomAdapter extends BaseAdapter {
	private Context context;
	private List<ObjectDrawerItem> list;

	public DrawerItemCustomAdapter(Context context,
			List<ObjectDrawerItem> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int i) {

		return list.get(i);
	}

	@Override
	public long getItemId(int i) {

		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		View row;
		row = inflater.inflate(R.layout.listview_item_row, parent, false);
		ImageView image = (ImageView) row.findViewById(R.id.image);
		TextView textViewName = (TextView) row.findViewById(R.id.textViewName);
		final ObjectDrawerItem objectDrawerItem = list.get(position);
		textViewName.setText(objectDrawerItem.getName());
//		image.setImageResource(objectDrawerItem.getImage());
		return (row);
	}
}