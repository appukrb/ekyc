package com.tcs.mmpl.customer.Hamburger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.mmpl.customer.Activity.ChangeMPINActivity;
import com.tcs.mmpl.customer.Activity.CheckTransactionsActivity;
import com.tcs.mmpl.customer.Activity.RequestMoneyActivity;
import com.tcs.mmpl.customer.Activity.RessyActivity;
import com.tcs.mmpl.customer.Activity.SelfHelpActivity;
import com.tcs.mmpl.customer.Activity.View_My_Profile;
import com.tcs.mmpl.customer.Activity.WebActivity;
import com.tcs.mmpl.customer.Adapter.GiftVoucher;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.GPSTracker;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.tcs.mmpl.customer.R.layout.popup_generate_otp;

public class HamburgerMenu {

    private static final int MODE_PRIVATE = 0;
    FontClass fontclass = new FontClass();
    Typeface typeface;
    private Context context;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ImageView imgMenu;
    private SharedPreferences pref, userInfoPref;
    private SharedPreferences.Editor editor, userInfoEditor;
    private ExpandableListView expListView;

    public HamburgerMenu(Context context, DrawerLayout mDrawerLayout, ListView mDrawerList, ImageView imgMenu) {
        this.context = context;
        this.mDrawerLayout = mDrawerLayout;
        this.mDrawerList = mDrawerList;
        this.imgMenu = imgMenu;

        pref = context.getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = context.getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();


        typeface = Typeface.createFromAsset(context.getAssets(), "helvetica.otf");

    }


    public HamburgerMenu(Context context, DrawerLayout mDrawerLayout, ExpandableListView expListView, ImageView imgMenu) {
        this.context = context;
        this.mDrawerLayout = mDrawerLayout;
        this.expListView = expListView;
        this.imgMenu = imgMenu;

        pref = context.getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = context.getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();


        typeface = Typeface.createFromAsset(context.getAssets(), "helvetica.otf");

    }

//	public void setHamburger()
//	{
//
//			imgMenu.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
//						mDrawerLayout.closeDrawer(Gravity.LEFT);
//
//					} else {
//						mDrawerLayout.openDrawer(Gravity.LEFT);
//
//					}
//
//				}
//			});
//
//
//
//			DrawerList drawerList = new DrawerList(context);
//			List<ObjectDrawerItem> list = drawerList.getMenuList();
//
//			DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(context, list);
//
//			mDrawerList.setAdapter(adapter);
//			mDrawerList
//					.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//						@Override
//						public void onItemClick(AdapterView<?> adapterView,
//								View view, int position, long l) {
//
//							int itemPosition = position;
//							Intent i = new Intent(context, WebActivity.class);
//
//
////							imgMenu.setImageResource(R.drawable.menu);
//
//							switch (position) {
//								case 0 : Intent intent = new Intent(context, MyAcount.class);
//									context.startActivity(intent);
//									break;
//								case 1 :
//									Intent intent2 = new Intent(context, RequestMoneyActivity.class);
//									context.startActivity(intent2);
//									break;
//								case 2 :  GiftVoucher giftVoucher = new GiftVoucher(context);
//									giftVoucher.execute(context.getResources().getString(R.string.gift_url)+"?MDN="+pref.getString("mobile_number",""));
//									break;
//								case 3 :
//									i.putExtra("option", "FAQ");
//									context.startActivity(i);
//									break;
//
//								case 4 : context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.tcs.mmpl.customer")));
//									break;
//
//								case 5: Intent intent8 = new Intent(context, FeedbackActivity.class);
//									context.startActivity(intent8);
//									break;
//
//								case 6: i.putExtra("option", "TNC");
//									context.startActivity(i);
//									break;
//
//								case 7 :   i.putExtra("option", "PRIVACY");
//									context.startActivity(i);
//									break;
//								case 8 :   i.putExtra("option", "ABOUT");
//									context.startActivity(i);
//									break;
//
//								case 9 : i.putExtra("option", "CONTACT");
//									context.startActivity(i);
//									break;
//
//								case 10 : Intent intent9 = new Intent(context, SelfHelpActivity.class);
//									intent9.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//									context.startActivity(intent9);
//									break;
//
//								case 11 : Intent intent10 = new Intent(context, MyIssuesActivity.class);
//									intent10.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//									context.startActivity(intent10);
//									break;
//
//
//							}
//
//							mDrawerLayout
//									.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
//										@Override
//										public void onDrawerClosed(View drawerView) {
//											super.onDrawerClosed(drawerView);
//
//										}
//									});
//							mDrawerLayout.closeDrawer(mDrawerList);
//						}
//					});
//
//	}


    public void setHamburger() {

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);

                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);

                }

            }
        });


        final ArrayList<String> listDataHeader = new ArrayList<String>();
        final HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add(context.getResources().getString(R.string.myaccount));
        listDataHeader.add(context.getResources().getString(R.string.requestmoney));
        listDataHeader.add(context.getResources().getString(R.string.coupons1));
        listDataHeader.add(context.getResources().getString(R.string.restaurant));
        listDataHeader.add(context.getResources().getString(R.string.homeservices));
        listDataHeader.add(context.getResources().getString(R.string.FAQs));
        listDataHeader.add(context.getResources().getString(R.string.help));
        listDataHeader.add(context.getResources().getString(R.string.rateus1));
        listDataHeader.add(context.getResources().getString(R.string.termsconditions));
        listDataHeader.add(context.getResources().getString(R.string.privacypolicy));
        listDataHeader.add(context.getResources().getString(R.string.aboutus));
        listDataHeader.add(context.getResources().getString(R.string.contactus));

        List<String> subHeader = new ArrayList<String>();
        subHeader.add("View My Profile");
        subHeader.add("View Transaction History");
        subHeader.add("Change mPIN");
        subHeader.add("Generate OTP");
//        subHeader.add("Unlock mPIN");


        List<String> dummy = new ArrayList<String>();


        listDataChild.put(listDataHeader.get(0), subHeader); // Header, Child data
        listDataChild.put(listDataHeader.get(1), dummy);
        listDataChild.put(listDataHeader.get(2), dummy);
        listDataChild.put(listDataHeader.get(3), dummy);
        listDataChild.put(listDataHeader.get(4), dummy);
        listDataChild.put(listDataHeader.get(5), dummy);
        listDataChild.put(listDataHeader.get(6), dummy);
        listDataChild.put(listDataHeader.get(7), dummy);
        listDataChild.put(listDataHeader.get(8), dummy);
        listDataChild.put(listDataHeader.get(9), dummy);
        listDataChild.put(listDataHeader.get(10), dummy);
        listDataChild.put(listDataHeader.get(11), dummy);


        ExpandableListAdapter listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.setGroupIndicator(null);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();


                Intent i = new Intent(context, WebActivity.class);
                switch (groupPosition) {
//					case 0 : Intent intent = new Intent(context, MyAcount.class);
//						context.startActivity(intent);
//						break;
                    case 1:
                        Intent intent2 = new Intent(context, RequestMoneyActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent2);
                        break;
                    case 2:

                        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                            GiftVoucher giftVoucher = new GiftVoucher(context);
                            giftVoucher.execute(context.getResources().getString(R.string.gift_url) + "?MDN=" + pref.getString("mobile_number", ""));
                        } else {
                            AlertBuilder alert = new AlertBuilder(context);
                            alert.newUser();
                        }

                        break;

                    case 3:
                        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {


                            // check if GPS enabled
                            GPSTracker gps = new GPSTracker(context);

                            if (gps.canGetLocation()) {

                                Intent intent = new Intent(context, RessyActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);

                            } else {
                                // can't get location
                                // GPS or Network is not enabled
                                // Ask user to enable GPS/network in settings
                                gps.showSettingsAlert();
                            }
                        } else {
                            AlertBuilder alert = new AlertBuilder(context);
                            alert.newUser();
                        }

                        break;
                    case 4:
                        i.putExtra("option", "HOMESERVICE");
                        context.startActivity(i);
                        break;
                    case 5:
                        i.putExtra("option", "FAQ");
                        context.startActivity(i);
                        break;

                    case 6:
                        Intent intent9 = new Intent(context, SelfHelpActivity.class);
                        intent9.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent9);
                        break;


                    case 7:
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.tcs.mmpl.customer")));
                        break;

//                    case 6:
//                        Intent intent8 = new Intent(context, FeedbackActivity.class);
//                        intent8.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//                        context.startActivity(intent8);
//                        break;

                    case 8:
                        i.putExtra("option", "TNC");
                        context.startActivity(i);
                        break;

                    case 9:
                        i.putExtra("option", "PRIVACY");
                        context.startActivity(i);
                        break;
                    case 10:
                        i.putExtra("option", "ABOUT");
                        context.startActivity(i);
                        break;

                    case 11:
                        i.putExtra("option", "CONTACT");
                        context.startActivity(i);
                        break;


                }


                if (groupPosition != 0)
                    mDrawerLayout.closeDrawer(Gravity.LEFT);


                return false;
            }
        });


        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {


            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub

                switch (childPosition) {
                    case 0:
                        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                            Intent i = new Intent(context, View_My_Profile.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            context.startActivity(i);
                        } else {
                            AlertBuilder alert = new AlertBuilder(context);
                            alert.newUser();
                        }
                        break;
                    case 1:
                        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                            Intent i = new Intent(context, CheckTransactionsActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            context.startActivity(i);
                        } else {
                            AlertBuilder alert = new AlertBuilder(context);
                            alert.newUser();
                        }
                        break;

                    case 2:
                        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                            Intent i1 = new Intent(context, ChangeMPINActivity.class);
                            i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            i1.putExtra("status", "0");
                            context.startActivity(i1);
                        } else {
                            AlertBuilder alert = new AlertBuilder(context);
                            alert.newUser();
                        }
                        break;

                    case 3:
                        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                            LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
                            View popupView = layoutInflater
                                    .inflate(popup_generate_otp, null);
                            final PopupWindow popupWindow = new PopupWindow(popupView,
                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            final EditText edtMpin = (EditText) popupView
                                    .findViewById(R.id.edittext_edit_popup);


                            Button btnCancel = (Button) popupView.findViewById(R.id.button_pop_no);

                            Button btnSubmit = (Button) popupView
                                    .findViewById(R.id.button_pop_yes);

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
                                    if (edtMpin.getText().toString().trim().equalsIgnoreCase("")) {
                                        Toast.makeText(context, context.getResources().getString(R.string.mpin), Toast.LENGTH_LONG).show();
                                    } else {
                                        popupWindow.dismiss();
                                        String generateOTPURL = context.getResources().getString(R.string.generateOTP) + "?MDN=" + pref.getString("mobile_number", "") + "&MPIN=" + edtMpin.getText().toString().trim();
                                        GenerateOTP generateOTP = new GenerateOTP(context);
                                        generateOTP.execute(generateOTPURL);
                                    }

                                }
                            });

                            popupWindow.setOutsideTouchable(false);
                            popupWindow.setFocusable(true);
                            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                        } else {
                            AlertBuilder alert = new AlertBuilder(context);
                            alert.newUser();
                        }
                        break;


//                    case 4:
//                        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
//
//                            Intent unlockIntent = new Intent(context, UnlockMPINActivity.class);
//                            unlockIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            context.startActivity(unlockIntent);
//                        } else {
//                            AlertBuilder alert = new AlertBuilder(context);
//                            alert.newUser();
//                        }
//                        break;
                }
                parent.collapseGroup(0);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                return false;
            }
        });


    }


    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<String> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<String, List<String>> _listDataChild;


        private GroupViewHolder groupViewHolder;
        private ChildViewHolder childViewHolder;

        public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                     HashMap<String, List<String>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_item, null);

                childViewHolder = new ChildViewHolder();

                childViewHolder.txtListChild = (TextView) convertView
                        .findViewById(R.id.lblListItem);


                convertView.setTag(childViewHolder);
            } else {

                childViewHolder = (ChildViewHolder) convertView.getTag();
            }

            childViewHolder.txtListChild.setText(childText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {

            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {


            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);


            if (convertView == null) {


                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_group, null);

                groupViewHolder = new GroupViewHolder();
                groupViewHolder.lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
                groupViewHolder.imgIndicator = (ImageView) convertView.findViewById(R.id.imgIndicator);

                convertView.setTag(groupViewHolder);
            } else {
                groupViewHolder = (GroupViewHolder) convertView.getTag();
            }


            groupViewHolder.lblListHeader.setTypeface(null, Typeface.BOLD);
            groupViewHolder.lblListHeader.setText(headerTitle);


            if (headerTitle.equalsIgnoreCase(context.getResources().getString(R.string.myaccount))) {

                groupViewHolder.imgIndicator.setVisibility(View.VISIBLE);
                if (isExpanded) {
                    groupViewHolder.imgIndicator.setImageResource(R.drawable.exp_minus);

                } else {
                    groupViewHolder.imgIndicator.setImageResource(R.drawable.exp_plus);
                }
            } else
                groupViewHolder.imgIndicator.setVisibility(View.GONE);


            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }


        @Override
        public void onGroupCollapsed(int groupPosition) {
            // TODO Auto-generated method stub

            super.onGroupCollapsed(groupPosition);
        }

        @Override
        public void onGroupExpanded(int groupPosition) {


            super.onGroupExpanded(groupPosition);
        }


        public class GroupViewHolder {

            TextView lblListHeader;
            ImageView imgIndicator;
        }

        public class ChildViewHolder {

            TextView txtListChild;

        }


    }


    private class GenerateOTP extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;


        public GenerateOTP(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(context.getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(context);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {
                            return jsonMainObj.getString("responseMessage");

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE")) {
                            return jsonMainObj.getString("responseMessage");

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

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Failure")) {


                Toast.makeText(context, context.getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {

                AlertBuilder alert = new AlertBuilder(context);
                alert.showAlert(result);

            }

        }

    }

//	public void changeHamburgerImage()
//	{
//		ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
//				(Activity) context, mDrawerLayout, R.drawable.menu, R.string.app_name,
//				R.string.app_name) {
//			public void onDrawerClosed(View view) {
//				super.onDrawerClosed(view);
//				imgMenu.setImageResource(R.drawable.menu);
//			}
//
//			public void onDrawerOpened(View drawerView) {
//				super.onDrawerOpened(drawerView);
//				imgMenu.setImageResource(R.drawable.left_menu);
//			}
//		};
//		mDrawerLayout.setDrawerListener(mDrawerToggle);
//	}

}
