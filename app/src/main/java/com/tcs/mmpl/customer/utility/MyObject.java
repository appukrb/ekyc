package com.tcs.mmpl.customer.utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.billdesk.sdk.LibraryPaymentStatusProtocol;
import com.tcs.mmpl.customer.Activity.LoadSuccessActivity;
import com.tcs.mmpl.customer.Activity.LoadSuccessForLowBalanceActivity;

public class MyObject implements LibraryPaymentStatusProtocol, Parcelable {
    public static final Creator CREATOR = new Creator() {
        @Override
        public MyObject createFromParcel(Parcel in) {
            return new MyObject(in);
        }

        @Override
        public Object[] newArray(int size) {
            return new MyObject[size];
        }
    };
    private static final int MODE_PRIVATE = 0;
    SharedPreferences pref, userInfoPref;
    SharedPreferences.Editor editor, userInfoEditor;

    public MyObject() {
        //// System.out.println("MyObject Constructor.......");
    }

    public MyObject(Parcel in) {

    }

    @Override
    public void paymentStatus(String status, Activity context) {

        // System.out.println("Status......."+status);

        userInfoPref = context.getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        if (userInfoPref.getString("transaction_flag", "0").equalsIgnoreCase("0")) {

            Intent mIntent = new Intent(context, LoadSuccessActivity.class);
            mIntent.putExtra("status", status);
            context.startActivity(mIntent);
            context.finish();
        } else {
            Intent mIntent = new Intent(context, LoadSuccessForLowBalanceActivity.class);
            mIntent.putExtra("status", status);
            context.startActivity(mIntent);
            context.finish();
        }
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
    }
}
