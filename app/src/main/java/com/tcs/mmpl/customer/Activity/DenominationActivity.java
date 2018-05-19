package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.DenominationPojo;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DenominationActivity extends Activity {


    private RecyclerView recList;
    private String brandname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_denomination);

        brandname = getIntent().getStringExtra("name");
        recList = (RecyclerView)findViewById(R.id.cardList);

        BrandDeno brandDeno = new BrandDeno(getApplicationContext());
        brandDeno.execute(getResources().getString(R.string.brand_denomination_url)+"?accessToken="+getIntent().getStringExtra("accesstoken")+"&hash="+getIntent().getStringExtra("hash"));

    }
    private class BrandDeno extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;
        private List<DenominationPojo> popularBrandPojoArrayList;

        public BrandDeno(Context context) {
            this.context = context;
            popularBrandPojoArrayList = new ArrayList<>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(DenominationActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                WebServiceHandler webServiceHandler = new WebServiceHandler(DenominationActivity.this);
                String jsonStr = webServiceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST);

                //// System.out.println("Deno::::"+jsonStr);
                return jsonStr;


            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }


        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("resposeStatus").trim().equalsIgnoreCase("Success")) {


                    JSONArray jsonArray = jsonObject.getJSONArray("denominations");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        DenominationPojo denominationPojo = new DenominationPojo();
                        denominationPojo.setId(jsonObject1.getString("id"));
                        denominationPojo.setName(jsonObject1.getString("name"));
                        denominationPojo.setSkuId(jsonObject1.getString("skuId"));
                        denominationPojo.setType(jsonObject1.getString("type"));
                        denominationPojo.setImage(getIntent().getStringExtra("image"));

                        popularBrandPojoArrayList.add(denominationPojo);

                    }

                    if(!popularBrandPojoArrayList.isEmpty()) {
//                        listBookings.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

//                        recList.setHasFixedSize(true);
//                        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
//                        llm.setOrientation(LinearLayoutManager.VERTICAL);
//                        recList.setLayoutManager(llm);

                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(DenominationActivity.this, 2);
                        recList.setLayoutManager(mLayoutManager);
                        recList.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
                        recList.setItemAnimator(new DefaultItemAnimator());

                        PopularBrandAdapter adapter = new PopularBrandAdapter(popularBrandPojoArrayList);
                        recList.setAdapter(adapter);

                    }
                    else
                    {
                        AlertBuilder alertBuilder = new AlertBuilder(DenominationActivity.this);
                        AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(getResources().getString(R.string.apidown));
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog closed
                                dialog.cancel();
                                //getActivity().finish();
                            }
                        });
                        // Showing Alert Message
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                    }


                } else if (jsonObject.getString("resposeStatus").trim().equalsIgnoreCase("Failure")) {
                    AlertBuilder alertBuilder = new AlertBuilder(DenominationActivity.this);
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(jsonObject.getString("resposeMessage"));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            //getActivity().finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
                else
                {
                    AlertBuilder alertBuilder = new AlertBuilder(DenominationActivity.this);
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(getResources().getString(R.string.apidown));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            //getActivity().finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            } catch (Exception e) {

                e.printStackTrace();

            }


        }

    }


    public class PopularBrandAdapter extends RecyclerView.Adapter<PopularBrandAdapter.PopularBrandViewHolder> {

        private List<DenominationPojo> popularBrandList;

        public PopularBrandAdapter(List<DenominationPojo> popularBrandList) {
            this.popularBrandList = popularBrandList;
        }

        @Override
        public int getItemCount() {
            return popularBrandList.size();
        }

        @Override
        public void onBindViewHolder(PopularBrandViewHolder popularBrandViewHolder, int i) {
            final DenominationPojo popularBrand = popularBrandList.get(i);

            Picasso.with(DenominationActivity.this).load(popularBrand.getImage()).placeholder(R.drawable.backgroud_default_image).into(popularBrandViewHolder.imgBrand);
            popularBrandViewHolder.imgBrand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(DenominationActivity.this, DenominationTransactionActivity.class);
                    intent.putExtra("image", getIntent().getStringExtra("image"));
                    intent.putExtra("name", popularBrand.getName());
                    intent.putExtra("skuid", popularBrand.getSkuId());
                    intent.putExtra("accesstoken", getIntent().getStringExtra("accesstoken"));
                    intent.putExtra("brandname", brandname);

                    startActivity(intent);
                }
            });

            popularBrandViewHolder.txtAmount.setText(getResources().getString(R.string.rupee_symbol)+" "+popularBrand.getName());

        }

        @Override
        public PopularBrandViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.denomination_row_layout, viewGroup, false);

            return new PopularBrandViewHolder(itemView);
        }

        public class PopularBrandViewHolder extends RecyclerView.ViewHolder {
            protected ImageView imgBrand;
            protected TextView txtAmount;



            public PopularBrandViewHolder(View v) {
                super(v);
                imgBrand =  (ImageView) v.findViewById(R.id.imgBrand);
                txtAmount = (TextView)  v.findViewById(R.id.txtAmount);

            }
        }
    }


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
