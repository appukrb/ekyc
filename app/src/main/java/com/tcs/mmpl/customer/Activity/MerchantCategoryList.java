package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tcs.mmpl.customer.Adapter.MerchantCategory;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MerchantCategoryList extends Activity {

    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;

    String url;

    ArrayList<String> category;
    private ListView listCategory;
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_merchant_category_list);


        category = new ArrayList<String>();
        url = getIntent().getStringExtra("url");

        connectionDetector = new ConnectionDetector(getApplicationContext());
        if (connectionDetector.isConnectingToInternet()) {
            MerchantImage merchantImage = new MerchantImage(getApplicationContext());
            merchantImage.execute(url);
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();

        }

        mGridView = (GridView)findViewById(R.id.gridView);




    }



    private class MerchantImage extends AsyncTask<String, Void, String> {

        Context context;
        ProgressDialog pDialog;

        ArrayList<MerchantCategory> mGridData;
        public MerchantImage(Context context) {
            this.context = context;

            mGridData  = new ArrayList<MerchantCategory>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MerchantCategoryList.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(MerchantCategoryList.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);


                //// System.out.println(jsonStr);
                jsonStr = "{\"value\":" + jsonStr + "}";

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonMainObj.getJSONArray("value");

                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {

                                MerchantCategory merchantCategory = new MerchantCategory();
                                JSONObject obj = (JSONObject) jsonArray.get(i);
                                merchantCategory.setImage(obj.getString("imageURL"));

                                mGridData.add(merchantCategory);

                            }



                            return "Success";
                        } else
                            return "Failure";


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

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Success")) {
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int width = metrics.widthPixels-20;

                LinearLayout.LayoutParams layoutParams  = new LinearLayout.LayoutParams(width/2,width/2);

                //Initialize with empty data

                GridViewAdapter mGridAdapter = new GridViewAdapter(MerchantCategoryList.this, R.layout.grid_item_layout, mGridData,width,layoutParams);
                mGridView.setAdapter(mGridAdapter);


                //Grid view click event
                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        //Get item at position
                        MerchantCategory item = (MerchantCategory) parent.getItemAtPosition(position);




                    }
                });


            } else if (result.equalsIgnoreCase("Failure")) {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        MerchantCategoryList.this).create();

                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.display_app_name));

                // Setting Dialog Message
                alertDialog.setMessage(getResources().getString(R.string.apidown));

                // Setting Icon to Dialog
                // alertDialog.setIcon(R.drawable.tick);

                // Setting OK Button
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();
                        //  getActivity().finish();
                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

                // Toast.makeText(getApplicationContext(),getResources().getString(R.string.apidown),Toast.LENGTH_LONG).show();;

            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        MerchantCategoryList.this).create();

                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.display_app_name));

                // Setting Dialog Message
                alertDialog.setMessage(getResources().getString(R.string.apidown));

                // Setting Icon to Dialog
                // alertDialog.setIcon(R.drawable.tick);

                // Setting OK Button
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();
                        // getActivity().finish();
                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

            }

        }

    }

    public class GridViewAdapter extends ArrayAdapter<MerchantCategory> {

        //private final ColorMatrixColorFilter grayscaleFilter;
        private Context mContext;
        private int layoutResourceId;
        private ArrayList<MerchantCategory> mGridData = new ArrayList<MerchantCategory>();

        int width;

        LinearLayout.LayoutParams layoutParams;

        public GridViewAdapter(Context mContext, int layoutResourceId, ArrayList<MerchantCategory> mGridData,int width,LinearLayout.LayoutParams layoutParams) {
            super(mContext, layoutResourceId, mGridData);
            this.layoutResourceId = layoutResourceId;
            this.mContext = mContext;
            this.mGridData = mGridData;
            this.width = width;
            this.layoutParams = layoutParams;

        }


        /**
         * Updates grid data and refresh grid items.
         *
         * @param mGridData
         */
        public void setGridData(ArrayList<MerchantCategory> mGridData) {
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

                holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
                // holder.imageView.setLayoutParams(layoutParams);

                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            MerchantCategory item = mGridData.get(position);
            //holder.titleTextView.setText(Html.fromHtml(item.getCategoryHeader()));
            //  holder.titleTextView.setText("");



            Glide.with(mContext).load(item.getImage()).into(holder.imageView);
            return row;
        }

        class ViewHolder {
            TextView grid_item_text;
            ImageView imageView;
        }
    }
}

