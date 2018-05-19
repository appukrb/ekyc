package com.tcs.mmpl.customer.Activity;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.mmpl.customer.Adapter.GiftVoucher;
import com.tcs.mmpl.customer.Adapter.SendNotficationClicked;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.Alert;
import com.tcs.mmpl.customer.utility.MyConnectionHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class AlertActivity extends Activity {

    ArrayList<String> deletePos;
    SharedPreferences pref;
    Editor editor;
    TextView textView_alerts_headingBar;
    ListView list;
    ArrayList<Alert> notificationArrayList;
    // AlertAdapter1 adapter;
    AlertAdapterNew adapter;
    MyConnectionHelper db;
    LinearLayout linear_main_ver;
    ImageButton deleteAlert;
    ArrayList<String> list_id, list_alerts, list_alertTime;
    private int flag = 0;
    private CheckBox check_all;
    private LinearLayout linselect;
    private TextView txtInbox;

    private String msg = "" ;
    private SharedPreferences userInfoPref;
    private Editor userInfoEditor;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_alert);

        txtInbox = (TextView) findViewById(R.id.txtInbox);
        list = (ListView) findViewById(R.id.listAlerts);
        check_all = (CheckBox) findViewById(R.id.chkAll);
        linselect = (LinearLayout) findViewById(R.id.linselect);
        linear_main_ver = (LinearLayout) findViewById(R.id.linear_main_ver);
        deleteAlert = (ImageButton) findViewById(R.id.deleteAlert);
        deletePos = new ArrayList<String>();


        list_id = new ArrayList<String>();
        list_alerts = new ArrayList<String>();
        list_alertTime = new ArrayList<String>();

        db = new MyConnectionHelper(getApplicationContext());
        pref = getApplicationContext().getSharedPreferences(
                "name", MODE_PRIVATE);
        editor = pref.edit();
        textView_alerts_headingBar = (TextView) findViewById(R.id.textView_alerts_headingBar);
        textView_alerts_headingBar.setText(pref.getString("user", "Hello "));

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();
        if(userInfoPref.getString("notify","false").trim().equalsIgnoreCase("true"))
        {
            String url = getResources().getString(R.string.saveNotification)+"?MSG="+userInfoPref.getString("msg","|");
            com.tcs.mmpl.customer.Adapter.SendNotficationClicked sendNotficationClicked = new com.tcs.mmpl.customer.Adapter.SendNotficationClicked(AlertActivity.this, userInfoPref.getString("msg","|"));
            sendNotficationClicked.execute(url);
            userInfoEditor.putString("notify","false");
            userInfoEditor.commit();

        }


//		list.setOnItemLongClickListener(new OnItemLongClickListener() {
//		     @Override
//	            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//	                    int position, long arg3) {
//
//
//	                removeItemFromList(position);
//
//	                return true;
//	            }
//	        });


    }

    @Override
    public void onResume() {
        super.onResume();
        getAlert();

    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public void getAlert() {

        notificationArrayList = new ArrayList<Alert>();

        list_id = null;
        list_alerts = null;
        list_alertTime = null;
        list_id = new ArrayList<String>();
        list_alerts = new ArrayList<String>();
        list_alertTime = new ArrayList<String>();

        int count = db.getUnreadCount();
        Cursor c = db.fun_selectDistinct_tbl_alerts();
        if (count == 0) {
            txtInbox.setText("Inbox");
        } else {
            txtInbox.setText("Inbox(" + count + ")");
        }

        if (c.moveToNext()) {

            deleteAlert.setVisibility(View.VISIBLE);
            linselect.setVisibility(View.VISIBLE);
            linear_main_ver.setVisibility(View.GONE);
            do {

                Alert alert = new Alert();
                alert.setNotificationId(c.getInt(c.getColumnIndex(MyConnectionHelper.tbl_alerts_ID)));
                alert.setNotificationMessage(c.getString(c.getColumnIndex(MyConnectionHelper.tbl_alerts_ALERT)));
                alert.setNotificationStatus(c.getString(c.getColumnIndex(MyConnectionHelper.tbl_alerts_ALERT_STATUS)));
                alert.setNotificationHeading(c.getString(c.getColumnIndex(MyConnectionHelper.tbl_alerts_ALERT_HEADING)));
                alert.setNotificationAction(c.getString(c.getColumnIndex(MyConnectionHelper.tbl_alerts_ALERT_ACTION)));
                alert.setNotificationLink(c.getString(c.getColumnIndex(MyConnectionHelper.tbl_alerts_ALERT_LINK)));


                list_id.add(String.valueOf(c.getInt(0)));
                list_alerts.add(c.getString(c.getColumnIndex(MyConnectionHelper.tbl_alerts_ALERT)));

                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss");
                SimpleDateFormat format1 = new SimpleDateFormat("dd MMM yy");
                String strToday = format.format(today);


                Date todayDate = null;// Set start date
                Date alertDate = null; // Set end date

                try {

                    todayDate = format.parse(strToday);
                    alertDate = format.parse(c.getString(c.getColumnIndex(MyConnectionHelper.tbl_alerts_ALERT_TIME)));
                } catch (Exception e) {

                }

                //// System.out.println("Today Date:::::::::" + todayDate);
                //// System.out.println("Alert Date:::::::::" + alertDate);
                long duration = todayDate.getTime() - alertDate.getTime();

                //// System.out.println("Duration:::::::::" + duration);

                long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
                long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);

                //// System.out.println("diffInSeconds:::::::::" + diffInSeconds);
                //// System.out.println("diffInMinutes:::::::::" + diffInMinutes);
                //// System.out.println("diffInHours:::::::::" + diffInHours);


                if (diffInHours == 1) {
                    alert.setNotificationTime(diffInHours + " hour");
                    list_alertTime.add(diffInHours + " hour");
                } else if (diffInHours > 1 && diffInHours < 24) {
                    alert.setNotificationTime(diffInHours + " hours");
                    list_alertTime.add(diffInHours + " hours");
                } else if (diffInHours == 24) {
                    alert.setNotificationTime("Yesterday");
                    list_alertTime.add("Yesterday");
                } else if (diffInHours > 24) {
                    int hours = (int) (diffInHours / 24);
                    if (hours == 1) {
                        alert.setNotificationTime("Yesterday");
                        list_alertTime.add("Yesterday");
                    } else {
                        alert.setNotificationTime(format1.format(alertDate));
                        list_alertTime.add(format1.format(alertDate));
                    }
                } else if (diffInMinutes == 60) {
                    alert.setNotificationTime("1 hour");
                    list_alertTime.add("1 hour");
                } else if (diffInMinutes > 0 && diffInMinutes < 60) {
                    alert.setNotificationTime(diffInMinutes + " min");

                    list_alertTime.add(diffInMinutes + " min");
                } else if (diffInSeconds == 60) {
                    alert.setNotificationTime("1 min");

                    list_alertTime.add("1 min");
                } else if (diffInSeconds < 60) {
                    alert.setNotificationTime(diffInSeconds + " sec");

                    list_alertTime.add(diffInSeconds + " sec");
                }


                notificationArrayList.add(alert);
            } while (c.moveToNext());
        } else {
            deleteAlert.setVisibility(View.GONE);
            linselect.setVisibility(View.GONE);
        }
        c.close();


//        adapter =new AlertAdapter1(this, R.layout.alert_row_layout,list_id,list_alerts,list,list_alertTime);
//        list.setAdapter(adapter);

        adapter = new AlertAdapterNew(this, R.layout.alert_row_layout, list, notificationArrayList);
        list.setAdapter(adapter);
    }

    public void funDeleteAlert(View v) {
        if (deletePos.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please select the alerts to delete", Toast.LENGTH_LONG).show();
        } else {

            LayoutInflater layoutInflater = (AlertActivity.this).getLayoutInflater();
            View popupView = layoutInflater.inflate(R.layout.popup_txt_two_buttons, null);
            final PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);


            Button btn_yes = (Button) popupView.findViewById(R.id.button_pop_yes);
            Button btn_no = (Button) popupView.findViewById(R.id.button_pop_no);

            TextView txt_content = (TextView) popupView.findViewById(R.id.textview_content_popup);
            txt_content.setText(Html.fromHtml("Do you want to delete alert?"));

            btn_no.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    popupWindow.dismiss();
                }
            });

            btn_yes.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    //main code on after clicking yes

                    popupWindow.dismiss();

                    if (flag == 1) {
                        removeAllFromList();
                        deleteAlert.setVisibility(View.GONE);
                        linselect.setVisibility(View.GONE);
                        deletePos.clear();

                    } else {
                        String id = "";

                        for (int i = 0; i < deletePos.size(); i++) {

                            id = id + "'" + deletePos.get(i) + "',";
                        }

                        deletePos.clear();
                        removeItemFromList(id.substring(0, id.length() - 1));
                    }

                }
            });

            popupWindow.setOutsideTouchable(false);
            popupWindow.setFocusable(true);
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        }

    }

    private int getCheckedItemCount() {
        int cnt = 0;
        SparseBooleanArray positions = list.getCheckedItemPositions();
        int itemCount = list.getCount();

        for (int i = 0; i < itemCount; i++) {
            if (positions.get(i))
                cnt++;
        }
        return cnt;
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(getApplicationContext(), HomeScreenActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
    // method to remove list item

    protected void removeAllFromList() {


        db.fun_delete_All_Alerts();
        adapter.notifyDataSetChanged();
        adapter.notifyDataSetInvalidated();

        notificationArrayList = new ArrayList<Alert>();

        adapter = new AlertAdapterNew(this, R.layout.alert_row_layout, list, notificationArrayList);
        list.setAdapter(adapter);

//        if(adapter.getCount() == 0)
//        {
//            finish();
//            Intent i = new Intent(getApplicationContext(),HomeScreenActivity.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(i);
//        }
//        else
//        {
//
//            adapter.notifyDataSetChanged();
//            adapter.notifyDataSetInvalidated();
//
//        }
    }

    protected void removeItemFromList(String ids) {


        db.fun_delete_Alerts(ids);
        if (adapter.getCount() == 0) {
            finish();
            Intent i = new Intent(getApplicationContext(), HomeScreenActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        } else {
            getAlert();
        }
    }

    public class AlertAdapterNew extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {
        int viewid;
        ViewHolder holder = null;
        ArrayList<String> list_id, list_alerts, list_alertTime;
        ListView list;
        boolean[] itemChecked;
        ArrayList<Alert> notificationArrayList;
        private Context context;
        private LayoutInflater Layf;

        public AlertAdapterNew(Context context,
                               int viewid, ListView list, ArrayList<Alert> notificationArrayList) {
            // TODO Auto-generated constructor stub
            this.context = context;
            this.Layf = LayoutInflater.from(context);
            this.viewid = viewid;

            this.list = list;
            this.notificationArrayList = notificationArrayList;

            itemChecked = new boolean[notificationArrayList.size()];
            //// System.out.println("Size of the list"+list_id.size());


        }


        @Override
        public int getCount() {
            return notificationArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return notificationArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return notificationArrayList.indexOf(getItem(position));
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {


            if (convertView == null) {
                holder = new ViewHolder();
                convertView = Layf.inflate(viewid, null);
                holder.txtAlertsHeader = (TextView) convertView.findViewById(R.id.txtAlertsHeader);
                holder.txtAlertsDesc = (TextView) convertView.findViewById(R.id.txtAlertsDesc);
                holder.txtAlertTime = (TextView) convertView.findViewById(R.id.txtAlertTime);
                holder.chkDelete = (CheckBox) convertView.findViewById(R.id.chkDelete);
                holder.linMessageBox = (LinearLayout) convertView.findViewById(R.id.linMessageBox);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Alert alert = (Alert) getItem(position);

            if(alert.getNotificationStatus().equalsIgnoreCase("U"))
                holder.txtAlertsHeader.setTypeface(null, Typeface.BOLD);
            holder.txtAlertsHeader.setText(alert.getNotificationHeading());

            holder.txtAlertsDesc.setText(alert.getNotificationMessage());


            holder.txtAlertTime.setText(alert.getNotificationTime());

            holder.chkDelete.setTag(position);
            check_all.setOnCheckedChangeListener(this);
            holder.chkDelete.setChecked(notificationArrayList.get(position).isSelected());
            holder.chkDelete.setOnCheckedChangeListener(this);


            holder.chkDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub


                    if (((CheckBox) v).isChecked()) {

                        try {
                            deletePos.add(String.valueOf(notificationArrayList.get(position).getNotificationId()));
                        } catch (Exception e) {

                        }
                        itemChecked[position] = true;

                    } else {

                        itemChecked[position] = false;
                        try {
                            deletePos.remove(String.valueOf(notificationArrayList.get(position).getNotificationId()));
                        } catch (Exception e) {

                        }


                    }


                }
            });

            holder.linMessageBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(AlertActivity.this);
                    dialog.setContentView(R.layout.popup_inbox_layout);
                    dialog.setTitle("Notification");


                    final Button dialogBtnOk = (Button) dialog.findViewById(R.id.btnClose);

                    final TextView txtHeader = (TextView) dialog.findViewById(R.id.txtHeader);
                    final TextView txtDesc = (TextView) dialog.findViewById(R.id.txtDesc);

                    try {
                        txtHeader.setText(alert.getNotificationHeading());

                    } catch (Exception e) {

                    }
                    try {
                        txtDesc.setText(alert.getNotificationMessage());
                        msg = alert.getNotificationMessage();

                    } catch (Exception e) {
                        msg = "";
                    }

                    dialogBtnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // System.out.println("alert screen");

                            String msg = alert.getNotificationMessage()+"|"+alert.getNotificationHeading();

                            String url = getResources().getString(R.string.saveNotification)+"?MSG="+Uri.encode(msg,"utf-8");
                            // System.out.println("alert screen"+url);

                            SendNotficationClicked sendNotficationClicked = new SendNotficationClicked(AlertActivity.this,msg);
                            sendNotficationClicked.execute(url);

                            //// System.out.println("Action:::::::::"+notificationArrayList.get(position).getNotificationAction());

                            db.updateAlert(notificationArrayList.get(position)
                                    .getNotificationId());

                            dialog.dismiss();


                            if (notificationArrayList.get(position).getNotificationAction()
                                    .equalsIgnoreCase("HOME")) {

                                Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            }
                            else if (notificationArrayList.get(position).getNotificationAction()
                                    .equalsIgnoreCase("RECHARGE")) {

                                Intent intent = new Intent(getApplicationContext(), Electricity_Payment.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            }
                            else  if (notificationArrayList.get(position).getNotificationAction()
                                    .equalsIgnoreCase("COUPON")) {

                                GiftVoucher giftVoucher = new GiftVoucher(AlertActivity.this);
                                giftVoucher.execute(getResources().getString(R.string.gift_url) + "?MDN=" + pref.getString("mobile_number", ""));
                            }
                            else if (notificationArrayList.get(position).getNotificationAction()
                                    .equalsIgnoreCase("GCI")) {
                                Intent intent = new Intent(getApplicationContext(), GCIActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                            else if(notificationArrayList.get(position).getNotificationAction()
                                    .equalsIgnoreCase("URLIN"))
                            {
                                Intent intent = new Intent(getApplicationContext(), WebActivity.class);
                                intent.putExtra("option", "LINK");
                                intent.putExtra("url", notificationArrayList.get(position).getNotificationLink());
                                startActivity(intent);
                            }
                            else if(notificationArrayList.get(position).getNotificationAction()
                                    .equalsIgnoreCase("URLOUT"))
                            {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(notificationArrayList.get(position).getNotificationLink())));

                            }


                            }
                    });

                    dialog.show();
                }
            });
//            list.setOnClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                    final Dialog dialog = new Dialog(AlertActivity.this);
//                    dialog.setContentView(R.layout.popup_inbox_layout);
//                    dialog.setTitle("Notification");
//
//                    final Button dialogBtnOk = (Button) dialog.findViewById(R.id.btnClose);
//
//                    final TextView txtHeader = (TextView) dialog.findViewById(R.id.txtHeader);
//                    final TextView txtDesc = (TextView) dialog.findViewById(R.id.txtDesc);
//
//                    txtHeader.setText(alert.getNotificationMessage().split("\\|")[0]);
//                    txtDesc.setText(alert.getNotificationMessage().split("\\|")[1]);
//
//                    dialogBtnOk.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//
//                        }
//                    });
//
//                    dialog.show();
//                }
//            });


            return convertView;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == check_all) {


                if (isChecked == true) {
                    flag = 1;
                    deletePos.clear();
                } else {
                    flag = 0;
                }
                check_all.setChecked(isChecked);
                for (int i = 0; i < notificationArrayList.size(); i++) {
                    notificationArrayList.get(i).setSelected(isChecked);
                    if (flag == 1)
                        deletePos.add(String.valueOf(notificationArrayList.get(i).getNotificationId()));


                }
                notifyDataSetChanged();
            } else {
                int position = (Integer) buttonView.getTag();


                if (isChecked) {
                    notificationArrayList.get(position).setSelected(true);

                } else {
                    notificationArrayList.get(position).setSelected(false);

                    if (check_all.isChecked()) {

                        check_all.setChecked(false);
                        for (int i = 0; i < notificationArrayList.size(); i++) {
                            notificationArrayList.get(i).setSelected(true);
                            notificationArrayList.get(position).setSelected(false);

                        }
                    }
                }
                notifyDataSetChanged();
            }
        }

        public class ViewHolder {
            public CheckBox chkDelete;
            TextView txtAlertsHeader, txtAlertTime, txtAlertsDesc;
            LinearLayout linMessageBox;

        }
    }



}

