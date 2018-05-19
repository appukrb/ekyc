package com.tcs.mmpl.customer.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tcs.mmpl.customer.Activity.LoadwalletLowBalance;
import com.tcs.mmpl.customer.R;

/**
 * Created by Home1 on 4/16/2016.
 */
public class PopupBuilder {
    private Context context;
    private Activity activity;

    public PopupBuilder(Context context)
    {
        this.context = context;
        this.activity = (Activity)context;
    }

    public void showPopup(final Double Value)
    {
        final String Amount=Double.toString(Value);
        View popupView = activity.getLayoutInflater().inflate(R.layout.popup_delete_favourites, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        TextView update_dialog_txt = (TextView) popupView.findViewById(R.id.txtMessage);
        Button update_dialog_btn_Cancel =(Button)popupView.findViewById(R.id.btnNo);
        Button update_dialog_btn_ok = (Button) popupView.findViewById(R.id.btnYes);

        update_dialog_txt.setText("You have insufficient balance in your wallet. Please load wallet to continue. ");
        update_dialog_btn_ok.setText("Proceed");
        update_dialog_btn_Cancel.setText("Cancel");

        update_dialog_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, LoadwalletLowBalance.class);

                intent.putExtra("Value", Amount);
                activity.startActivity(intent);
                popupWindow.dismiss();
            }
        });

        update_dialog_btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);


//        AlertDialog.Builder alertDialog = new AlertDialog.Builder((Activity)context);
//        // Setting Dialog Title
//        alertDialog.setTitle(context.getString(R.string.display_app_name));
//        // Setting Dialog Message
//        alertDialog.setMessage(message);
//        // Setting Icon to Dialog
//        // alertDialog.setIcon(R.drawable.tick);
//        // Setting OK Button
//        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // Write your code here to execute after dialog closed
//                dialog.cancel();
//            }
//        });
//        // Showing Alert Message
//        alertDialog.setCancelable(false);
//        alertDialog.show();

    }

}
