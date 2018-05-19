package com.tcs.mmpl.customer.utility;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class SetNotificationCounter {
	
	
	private static final int MODE_PRIVATE = 0;
	private Context context;
	private TextView txtCounter;
	MyConnectionHelper db;
	public SetNotificationCounter(Context context, TextView txtCounter) {
		
		this.context = context;
		this.txtCounter = txtCounter;

		db = new MyConnectionHelper(context);

		int count = db.getUnreadCount();
		String strI = Integer.toString(count);

		if(count == 0)
		this.txtCounter.setVisibility(View.GONE);
		else
		{
		this.txtCounter.setVisibility(View.VISIBLE);
			this.txtCounter.setText(strI);

			//// System.out.println("notifcation_counter"+count);
		}
		
		
	}

}
