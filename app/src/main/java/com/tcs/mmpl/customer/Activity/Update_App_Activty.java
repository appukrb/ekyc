package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;


public class Update_App_Activty extends Activity {
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_update__app__activty);
//        showUpdateDialog();

        View popupView = getLayoutInflater().inflate(R.layout.popup_update, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        TextView update_dialog_txt = (TextView) popupView.findViewById(R.id.update_dialog_txt);
        Button update_dialog_btn_Cancel =(Button)popupView.findViewById(R.id.update_dialog_btn_Cancel);
        Button update_dialog_btn_ok = (Button) popupView.findViewById(R.id.update_dialog_btn_ok);

        update_dialog_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.tcs.mmpl.customer")));
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

    }

    private void showUpdateDialog()
    {

//        View popupView = getLayoutInflater().inflate(R.layout.popup_update, null);
//        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        TextView update_dialog_txt = (TextView) popupView.findViewById(R.id.update_dialog_txt);
//        Button update_dialog_btn_Cancel =(Button)popupView.findViewById(R.id.update_dialog_btn_Cancel);
//        Button update_dialog_btn_ok = (Button) popupView.findViewById(R.id.update_dialog_btn_ok);
//
//        update_dialog_btn_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.tcs.mmpl.customer")));
//                popupWindow.dismiss();
//            }
//        });
//
//        update_dialog_btn_Cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//            }
//        });
//
//            popupWindow.setOutsideTouchable(false);
//            popupWindow.setFocusable(true);
//            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);


//            popupWindow.dismiss();
//            popupWindow.setOutsideTouchable(false);
//            popupWindow.setFocusable(true);
//            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);


//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
////        builder.setTitle("A New Update is Available");
//
//        builder.setTitle("Hey! We have loaded your mRUPEE App with loads of new and exciting features. Upgrade now for experiencing it.");
//
//        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
//                        ("market://details?id=com.tcs.mmpl.customer")));
//                dialog.dismiss();
//            }
//        });
//
////        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
////                background.start();
////            }
////        });
//
//        builder.setCancelable(false);
//        dialog = builder.show();
    }
}
