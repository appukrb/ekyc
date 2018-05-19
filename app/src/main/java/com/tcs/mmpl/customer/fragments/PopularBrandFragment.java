package com.tcs.mmpl.customer.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tcs.mmpl.customer.Activity.BrandDenominationActivity;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.PopularBrandPojo;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016-09-22.
 */
public class PopularBrandFragment extends Fragment {
    RecyclerView recList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            if(getView()!=null)
            {
//                PopularBrand popularBrand = new PopularBrand(getActivity());
//                popularBrand.execute(getActivity().getResources().getString(R.string.popular_brand_url));
            }
        }
    }

    // create boolean for fetching data
    private boolean isViewShown = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            isViewShown = true;


        } else {
            isViewShown = false;
        }
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isViewShown = false;
    }
    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_popular_brand, container, false);
        setRetainInstance(true);

        recList = (RecyclerView)rootView.findViewById(R.id.cardList);


            PopularBrand popularBrand = new PopularBrand(getActivity());
            popularBrand.execute(getActivity().getResources().getString(R.string.popular_brand_url));


        return rootView;
    }


    private class PopularBrand extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;
        private List<PopularBrandPojo> popularBrandPojoArrayList;

        public PopularBrand(Context context) {
            this.context = context;
            popularBrandPojoArrayList = new ArrayList<>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                WebServiceHandler webServiceHandler = new WebServiceHandler(getActivity());
                String jsonStr = webServiceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST);
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


                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        PopularBrandPojo popularBrandPojo = new PopularBrandPojo();
                        popularBrandPojo.setAccesstoken(jsonObject.getString("accesstoken"));
                        popularBrandPojo.setHash(jsonObject1.getString("hash"));
                        popularBrandPojo.setName(jsonObject1.getString("name"));
                        popularBrandPojo.setDescription(jsonObject1.getString("description"));
                        popularBrandPojo.setTerms(jsonObject1.getString("terms"));
                        popularBrandPojo.setImage(jsonObject1.getString("image"));
                        popularBrandPojo.setIsOnline(jsonObject1.getString("isOnline"));
                        popularBrandPojo.setWebsite(jsonObject1.getString("website"));

                        popularBrandPojoArrayList.add(popularBrandPojo);

                    }

                    if(!popularBrandPojoArrayList.isEmpty()) {
//                        listBookings.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

//                        recList.setHasFixedSize(true);
//                        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
//                        llm.setOrientation(LinearLayoutManager.VERTICAL);
//                        recList.setLayoutManager(llm);

                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
                        recList.setLayoutManager(mLayoutManager);
                        recList.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
                        recList.setItemAnimator(new DefaultItemAnimator());

                        PopularBrandAdapter adapter = new PopularBrandAdapter(popularBrandPojoArrayList);
                        recList.setAdapter(adapter);

                    }
                    else
                    {
                        AlertBuilder alertBuilder = new AlertBuilder(getActivity());
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
                    AlertBuilder alertBuilder = new AlertBuilder(getActivity());
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
                    AlertBuilder alertBuilder = new AlertBuilder(getActivity());
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

            }


        }

    }


    public class PopularBrandAdapter extends RecyclerView.Adapter<PopularBrandAdapter.PopularBrandViewHolder> {

        private List<PopularBrandPojo> popularBrandList;

        public PopularBrandAdapter(List<PopularBrandPojo> popularBrandList) {
            this.popularBrandList = popularBrandList;
        }

        @Override
        public int getItemCount() {
            return popularBrandList.size();
        }

        @Override
        public void onBindViewHolder(PopularBrandViewHolder popularBrandViewHolder, int i) {
            final PopularBrandPojo popularBrand = popularBrandList.get(i);

            Picasso.with(getActivity()).load(popularBrand.getImage()).placeholder(R.drawable.backgroud_default_image).into(popularBrandViewHolder.imgBrand);
            popularBrandViewHolder.imgBrand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), BrandDenominationActivity.class);
                    intent.putExtra("terms",popularBrand.getTerms());
                    intent.putExtra("hash",popularBrand.getHash());
                    intent.putExtra("image",popularBrand.getImage());
                    intent.putExtra("accesstoken",popularBrand.getAccesstoken());
                    intent.putExtra("name",popularBrand.getName());
                    startActivity(intent);
                }
            });
            popularBrandViewHolder.txtBrand.setText(popularBrand.getName());
            popularBrandViewHolder.txtBrandDesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle("Description");
                    alertDialog.setMessage(Html.fromHtml(popularBrand.getDescription()));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();

                        }
                    });

                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            });

            popularBrandViewHolder.txtBrandTerms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle("Terms");
                    alertDialog.setMessage(Html.fromHtml(popularBrand.getTerms()));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();

                        }
                    });

                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            });

        }

        @Override
        public PopularBrandViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.popular_brand_row_layout, viewGroup, false);

            return new PopularBrandViewHolder(itemView);
        }

        public class PopularBrandViewHolder extends RecyclerView.ViewHolder {
            protected ImageView imgBrand;
            protected TextView txtBrand;
            protected TextView txtBrandDesc;
            protected TextView txtBrandTerms;


            public PopularBrandViewHolder(View v) {
                super(v);
                imgBrand =  (ImageView) v.findViewById(R.id.imgBrand);
                txtBrand = (TextView)  v.findViewById(R.id.txtBrandName);
                txtBrandDesc = (TextView)  v.findViewById(R.id.txtBrandDesc);
                txtBrandTerms = (TextView)  v.findViewById(R.id.txtBrandTerms);

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
