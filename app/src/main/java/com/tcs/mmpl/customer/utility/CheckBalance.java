package com.tcs.mmpl.customer.utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hp on 18-04-2016.
 */
public class CheckBalance {

    private static final int MODE_PRIVATE = 0 ;
    private String walletBalance,Req_Balance;
    private double R_Balance,W_Balance,Sum_Amount;
    private SharedPreferences userInfoPref;
    private SharedPreferences.Editor userInfoEditor;

    private Context context;
    public CheckBalance(Context context)
    {
        this.context = context;

        userInfoPref = context.getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();


    }

    public boolean getBalanceCheck(String amount)
    {
        walletBalance=userInfoPref.getString("walletbalance", "");
        W_Balance = Float.valueOf(walletBalance);
        R_Balance=Float.valueOf(amount);
        Sum_Amount=W_Balance - R_Balance ;


        if(R_Balance > W_Balance && R_Balance > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public Double getAmount(String amount)
    {
        walletBalance=userInfoPref.getString("walletbalance", "");
        W_Balance = Float.valueOf(walletBalance);
        R_Balance=Float.valueOf(amount);

        Sum_Amount=W_Balance - R_Balance ;
        Double Sum_Amount_1=(Math.abs(Sum_Amount));
        return  Sum_Amount_1;

    }
}
