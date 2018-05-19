package com.tcs.mmpl.customer.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.Activity.RegisterActivity;

/**
 * Created by hp on 17-11-2015.
 */
public class AlertBuilder {

    private Context context;
    private Activity activity;
    public AlertBuilder(Context context)
    {
        this.context = context;
        this.activity = (Activity)context;
    }

    public void newUser()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder((Activity)context);
        // Setting Dialog Title
        alertDialog.setTitle(context.getString(R.string.display_app_name));
        // Setting Dialog Message
        alertDialog.setMessage(context.getString(R.string.new_user));
        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable.tick);
        // Setting OK Button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                dialog.cancel();
                Intent i = new Intent(activity, RegisterActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(i);
            }
        });
        // Showing Alert Message
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void showAlert(String message)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder((Activity)context);
        // Setting Dialog Title
        alertDialog.setTitle(context.getString(R.string.display_app_name));
        // Setting Dialog Message
        alertDialog.setMessage(message);
        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable.tick);
        // Setting OK Button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    public AlertDialog.Builder showRetryAlert(String message)
    {

        AlertDialog.Builder  alertDialog = new AlertDialog.Builder((Activity)context);
        // Setting Dialog Title
        alertDialog.setTitle(context.getString(R.string.display_app_name));
        // Setting Dialog Message
        alertDialog.setMessage(message);

        return  alertDialog;

    }


}
