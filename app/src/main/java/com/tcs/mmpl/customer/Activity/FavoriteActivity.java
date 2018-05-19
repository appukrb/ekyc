package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.Adapter.FavoritePojo;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.Bingo;
import com.tcs.mmpl.customer.utility.CheckBalance;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.MyDBHelper;
import com.tcs.mmpl.customer.utility.PopupBuilder;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
public class FavoriteActivity extends Activity {

    ListView listview;
    Typeface custom_font;
    LinearLayout linParent, txtErrorFav;
    TextView fav_txt;
    FontClass fontclass = new FontClass();
    Typeface typeface;

    ProgressDialog pDialog;
    SharedPreferences pref, userInfoPref;
    SharedPreferences.Editor editor, userInfoEditor;

    ArrayList<FavoritePojo> favoritePojoList;
    MyDBHelper db;
    String fav_url = "";

    ConnectionDetector connectionDetector;

    ImageButton deleteAlert;


    private ArrayList<String> deletePos;
    private CheckBox check_all;
    private int flag = 0;
    private String fav_id = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        linParent = (LinearLayout) findViewById(R.id.linParent);
        txtErrorFav = (LinearLayout) findViewById(R.id.txtErrorFav);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(linParent, typeface);

        deleteAlert = (ImageButton)findViewById(R.id.deleteAlert);

        deletePos = new ArrayList<String>();

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        db = new MyDBHelper(getApplicationContext());
        connectionDetector = new ConnectionDetector(getApplicationContext());

        fav_txt =(TextView)findViewById(R.id.fav_txt);
        String getFavourites_url = getApplicationContext().getResources().getString(R.string.getfavourites);
        GetFavourites getFavourites = new GetFavourites(getApplicationContext());
        getFavourites.execute(getFavourites_url);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    public void funDeleteFavourites(View v) {
        if (deletePos.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please select the favourites to delete", Toast.LENGTH_LONG).show();
        } else {

            fav_id = "";
            for (int i = 0; i < deletePos.size(); i++) {

                fav_id = fav_id  + deletePos.get(i) + "|" ;

            }



            fav_id = fav_id.substring(0, fav_id.length() - 1);


                               LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                            .getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = layoutInflater
                            .inflate(R.layout.popup_delete_favourites, null);
                    final PopupWindow popupWindow = new PopupWindow(popupView,
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                    Button btnCancel = (Button) popupView.findViewById(R.id.btnNo);

                    Button btnSubmit = (Button) popupView
                            .findViewById(R.id.btnYes);


                    btnCancel.setTypeface(typeface);
                    btnSubmit.setTypeface(typeface);

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });

                    btnSubmit.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            String url = getApplicationContext().getResources().getString(R.string.delfavourites);
                            DeleteFavourites deleteFavourites = new DeleteFavourites(getApplicationContext(),fav_id );
                            deleteFavourites.execute(url);
                        }
                    });

                    popupWindow.setOutsideTouchable(false);
                    popupWindow.setFocusable(true);
                    popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        }

    }

    class CustomListAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener{
        private Context context;

        private ListView list;
        List<FavoritePojo> alertList;
        ViewHolder holder = null;


        private LayoutInflater Layf;
        int viewid;
        boolean[] itemChecked;
        public class ViewHolder {
            TextView operatorname, txtType,txtAmount,personname;
            public CheckBox chkDelete;
            LinearLayout linFavourites;

        }

        public CustomListAdapter(Context context, ArrayList<FavoritePojo> favoritePojoList,ListView list) {
            this.context = context;

            this.list = list;

            this.Layf = LayoutInflater.from(context);
            this.viewid = R.layout.listview_favorite;

            alertList = new ArrayList<FavoritePojo>();
            this.alertList = favoritePojoList;

            itemChecked = new boolean[this.alertList.size()];
        }

        @Override
        public int getCount() {
            return alertList.size();
        }

        @Override
        public Object getItem(int i) {
            return alertList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public View getView(final int position, View view, ViewGroup parent) {


            if(view  == null) {

                holder = new ViewHolder();
                view = Layf.inflate(viewid, null);
                holder.personname = (TextView) view.findViewById(R.id.personname);

                holder.linFavourites = (LinearLayout) view.findViewById(R.id.linFavourites);
                holder.operatorname = (TextView) view.findViewById(R.id.operatorname);
                holder.txtType = (TextView) view.findViewById(R.id.txtType);
                holder.txtAmount = (TextView) view.findViewById(R.id.txtAmount);
                holder.chkDelete = (CheckBox) view.findViewById(R.id.chkDelete);

                view.setTag(holder);

            }
            else
            {
                holder = (ViewHolder) view.getTag();
            }

            final FavoritePojo contactDetails = favoritePojoList.get(position);
            holder.personname.setText(contactDetails.getPersonname());
            holder.personname.setTypeface(custom_font);
            holder.operatorname.setText(Uri.decode(contactDetails.getOperatorname()));
            holder.operatorname.setTypeface(custom_font);
            holder.txtType.setText(contactDetails.getType());
            holder.txtType.setTypeface(custom_font);
            holder.txtAmount.setText(getResources().getString(R.string.rupee_symbol)+contactDetails.getAmount());
            holder.txtAmount.setTypeface(custom_font);


            holder.chkDelete.setTag(position);
            holder.chkDelete.setChecked(alertList.get(position).isSelected());
            holder.chkDelete.setOnCheckedChangeListener(this);




//            fav_del.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//
//                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
//                            .getSystemService(LAYOUT_INFLATER_SERVICE);
//                    View popupView = layoutInflater
//                            .inflate(R.layout.popup_delete_favourites, null);
//                    final PopupWindow popupWindow = new PopupWindow(popupView,
//                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//
//                    Button btnCancel = (Button) popupView.findViewById(R.id.btnNo);
//
//                    Button btnSubmit = (Button) popupView
//                            .findViewById(R.id.btnYes);
//
//
//                    btnCancel.setTypeface(typeface);
//                    btnSubmit.setTypeface(typeface);
//
//                    btnCancel.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            popupWindow.dismiss();
//                        }
//                    });
//
//                    btnSubmit.setOnClickListener(new Button.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            popupWindow.dismiss();
//                            String url = getApplicationContext().getResources().getString(R.string.delfavourites);
//                            DeleteFavourites deleteFavourites = new DeleteFavourites(getApplicationContext(), contactDetails.getFavID());
//                            deleteFavourites.execute(url);
//                        }
//                    });
//
//                    popupWindow.setOutsideTouchable(false);
//                    popupWindow.setFocusable(true);
//                    popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
//
//
//                }
//            });


            holder.chkDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub


                    if (((CheckBox) v).isChecked()) {

                        try {

                            //// System.out.println("Add::"+alertList.get(position).getFavID());
                            deletePos.add(alertList.get(position).getFavID());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        itemChecked[position] = true;

                    } else {

                        itemChecked[position] = false;
                        try {
                            //// System.out.println("Remove::"+alertList.get(position).getFavID());
                            deletePos.remove(alertList.get(position).getFavID());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }


                }
            });

            holder.linFavourites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                            .getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = layoutInflater
                            .inflate(R.layout.popup_favourite_mpin_layout, null);
                    final PopupWindow popupWindow = new PopupWindow(popupView,
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                    final EditText edtAmount = (EditText) popupView
                            .findViewById(R.id.edtAmount);
                    final EditText edtMpin = (EditText) popupView
                            .findViewById(R.id.edittext_edit_popup);

                    edtAmount.setText(contactDetails.getAmount());
                    fav_url = favoritePojoList.get(position).getUrl();

                    Button btnCancel = (Button) popupView.findViewById(R.id.button_pop_no);

                    Button btnSubmit = (Button) popupView
                            .findViewById(R.id.button_pop_yes);

                    edtAmount.setTypeface(typeface);
                    edtMpin.setTypeface(typeface);
                    btnCancel.setTypeface(typeface);
                    btnSubmit.setTypeface(typeface);

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });

                    btnSubmit.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String amount = edtAmount.getText().toString().trim().replaceFirst("^0+(?!$)", "");

                            //// System.out.println(amount);
                            if(amount.equalsIgnoreCase("") || amount.equalsIgnoreCase("0")) {
                                AlertBuilder alertBuilder = new AlertBuilder(FavoriteActivity.this);
                                alertBuilder.showAlert(getResources().getString(R.string.invalid_amount));
                            }
                            else if (edtMpin.getText().toString().trim().equalsIgnoreCase("")) {

                                AlertBuilder alertBuilder = new AlertBuilder(FavoriteActivity.this);
                                alertBuilder.showAlert(getResources().getString(R.string.mpin));

                            } else {
                                    String mpin=edtMpin.getText().toString().trim();
                                    String url = fav_url + "&MPIN=" + edtMpin.getText().toString().trim();
                                    //// System.out.println(url);

                                    if(!contactDetails.getAmount().trim().equalsIgnoreCase(amount))
                                    {
                                        url = url.replaceAll("Amount="+contactDetails.getAmount(),"Amount="+amount);
                                    }


                                    String[] splitUrl = url.split("\\?");
                                    String[] parameterSplit = splitUrl[1].split("\\&");

                                    if(splitUrl[0].contains("billPayment"))
                                    {String mobilerecharge_url = "";
                                        String mobileNo;
                                        String category;
                                        String rechargeMobile;
                                        String billerCode;
                                        String BU = "";
                                        String Region="";
                                        String amount1="";
                                         mobileNo = parameterSplit[0].split("\\=")[1];
                                         Region = parameterSplit[1].split("\\=")[1];
                                         category = Uri.decode(parameterSplit[2].split("\\=")[1]);
                                         billerCode = parameterSplit[3].split("\\=")[1];
                                         rechargeMobile = parameterSplit[4].split("\\=")[1];
                                         amount1= parameterSplit[5].split("\\=")[1];
                                         BU = parameterSplit[6].split("\\=")[1];

                                        String BingoString = mobileNo + "|"+""+"|"+mpin+"|"+category+ "|" + ""+"|"+""+"|"+""+"|"+billerCode+"|"+rechargeMobile+"|"+amount1+"|"+Region+"|"+BU;
                                        String res = Bingo.Bingo_one(BingoString);
//                                        // System.out.println("BingoString"+BingoString);
                                        url=url+"&Checksum="+res;
                                    }

                                CheckBalance checkBalance = new CheckBalance(FavoriteActivity.this);
                                if(checkBalance.getBalanceCheck(amount))
                                {

                                    PopupBuilder popup = new PopupBuilder(FavoriteActivity.this);
                                    popup.showPopup(checkBalance.getAmount(amount));

                                    userInfoEditor.putString("transaction_url", url);
                                    userInfoEditor.putString("transaction_method","POST2");
                                    userInfoEditor.putString("transaction_flag","1");
                                    userInfoEditor.commit();

                                }
                                else {

                                    ExcecuteFavourites ex = new ExcecuteFavourites(getApplicationContext(), popupWindow);
                                    ex.execute(url);
                                }
                            }

                        }
                    });

                    popupWindow.setOutsideTouchable(false);
                    popupWindow.setFocusable(true);
                    popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                }
            });


            return view;
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
                for (int i = 0; i < alertList.size(); i++) {
                    alertList.get(i).setSelected(isChecked);
                    if (flag == 1)
                        deletePos.add(alertList.get(i).getFavID());


                }
                notifyDataSetChanged();
            } else {
                int position = (Integer) buttonView.getTag();


                if (isChecked) {
                    alertList.get(position).setSelected(true);

                } else {
                    alertList.get(position).setSelected(false);

//                    if (check_all.isChecked()) {
//
//                        check_all.setChecked(false);
//                        for (int i = 0; i < alertList.size(); i++) {
//                            alertList.get(i).setSelected(true);
//                            alertList.get(position).setSelected(false);
//
//                        }
//                    }
                }
                notifyDataSetChanged();
            }
        }


    }

    private class GetFavourites extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;
        int flag = 0;

        public GetFavourites(Context context) {
            this.context = context;

            favoritePojoList = null;
            favoritePojoList = new ArrayList<FavoritePojo>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(FavoriteActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("MDN", pref.getString("mobile_number", ""));
                jsonObject.accumulate("parameterType", "");

                // 4. convert JSONObject to JSON to String
                String json = jsonObject.toString();

                //// System.out.println(json);
                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);

                WebServiceHandler serviceHandler = new WebServiceHandler(FavoriteActivity.this, se);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST);

//                // System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            if (jsonMainObj.getString("count").equalsIgnoreCase("0")) {
                                flag = 0;
                            } else {

                                flag = 1;

                                JSONArray jsonArray = jsonMainObj
                                        .getJSONArray("favouriteDetailList");
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject j1 = jsonArray.getJSONObject(i);

                                    String[] data = j1.getString("parameterValue").split("\\|");

                                    //FavoritePojo favoritePojo = new FavoritePojo(data[2], j1.getString("parameterType"),j1.getString("favouriteURL"),data[1],"",j1.getString("favouriteID"));

                                    FavoritePojo favoritePojo = new FavoritePojo();
                                    favoritePojo.setPersonname(data[2]);
                                    favoritePojo.setOperatorname(j1.getString("parameterType"));
                                    favoritePojo.setUrl(j1.getString("favouriteURL"));
                                    favoritePojo.setAmount(data[1]);
                                    favoritePojo.setFavID(j1.getString("favouriteID"));
                                    favoritePojoList.add(favoritePojo);

                                    db.fun_insert_tbl_favourites_details(j1.getString("favouriteID"), j1.getString("parameterType"), j1.getString("parameterValue"), j1.getString("favouriteURL"));


                                }
                            }

                            return "Success";


                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE")) {
                            return "Failure";


                        } else {
                            return "Failure";
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Failure";
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                    return "Failure";
                }


            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }


        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            if (result.equalsIgnoreCase("Failure")) {
                deleteAlert.setVisibility(View.INVISIBLE);

                fav_txt.setText("No Favourites Found");
                listview = (ListView) findViewById(R.id.listviewforfavorites);
                CustomListAdapter customListAdapter = new CustomListAdapter(FavoriteActivity.this, favoritePojoList,listview);
                listview.setAdapter(customListAdapter);

            } else {

                if (flag == 1) {
                    deleteAlert.setVisibility(View.VISIBLE);
                    fav_txt.setText("Favourites");
                    listview = (ListView) findViewById(R.id.listviewforfavorites);
                    CustomListAdapter customListAdapter = new CustomListAdapter(FavoriteActivity.this, favoritePojoList,listview);
                    listview.setAdapter(customListAdapter);
                } else {

                    deleteAlert.setVisibility(View.INVISIBLE);
                    fav_txt.setText("No Favourites Found");
                    listview = (ListView) findViewById(R.id.listviewforfavorites);
                    CustomListAdapter customListAdapter = new CustomListAdapter(FavoriteActivity.this, favoritePojoList,listview);
                    listview.setAdapter(customListAdapter);


                    // Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                }

            }

        }

    }

    private class ExcecuteFavourites extends AsyncTask<String, Void, String> {

        Context context;

        String responseMessage;
        PopupWindow popupWindow;

        public ExcecuteFavourites(Context context,PopupWindow popupWindow) {
            this.context = context;
            this.popupWindow = popupWindow;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(FavoriteActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();



                WebServiceHandler serviceHandler = new WebServiceHandler(context);
                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());

                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {


                            responseMessage = jsonMainObj.getString("responseMessage");



                            return "Success";

                        }

                        else
                        {
                            responseMessage = jsonMainObj.getString("responseMessage");
                            return "Failure";
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Failure";
                    }
                }
                else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                    return "Failure";
                }


            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }



        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();


            if (result.equalsIgnoreCase("Success")) {

                try
                {
                    popupWindow.dismiss();
                }
                catch (Exception e)
                {

                }
                Toast.makeText(getApplicationContext(), responseMessage, Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(), responseMessage, Toast.LENGTH_LONG).show();
            }

        }

    }

    private class DeleteFavourites extends AsyncTask<String, Void, String> {

        Context context;

        String favID, lastName, walletBalance;
        int flag = 0;


        public DeleteFavourites(Context context,String favID) {
            this.context = context;
            this.favID=favID;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(FavoriteActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("favouriteID", favID);


                // 4. convert JSONObject to JSON to String
                String json = jsonObject.toString();

                //// System.out.println(json);
                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);

                WebServiceHandler serviceHandler = new WebServiceHandler(FavoriteActivity.this, se);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            return "Success";


                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE")) {
                            return "Failure";

                        } else {
                            return "Failed";
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Failed";
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                    return "Failed";
                }


            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failed";
            }


        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Success")) {

                deletePos.clear();
                String getFavourites_url = getApplicationContext().getResources().getString(R.string.getfavourites);
                GetFavourites getFavourites = new GetFavourites(getApplicationContext());
                getFavourites.execute(getFavourites_url);

            }else
            {
             Toast.makeText(getApplicationContext(),getResources().getString(R.string.apidown),Toast.LENGTH_SHORT);
            }

        }

    }
}
