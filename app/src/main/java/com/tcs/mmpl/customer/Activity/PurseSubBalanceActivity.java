package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.Purse;

import java.util.ArrayList;

public class PurseSubBalanceActivity extends Activity {

    private ArrayList<Purse> purseArrayList;
    private TextView txtWalletBalance;
    private SharedPreferences pref, userInfoPref;
    private SharedPreferences.Editor editor, userInfoEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_purse_sub_balance);

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        txtWalletBalance = (TextView) findViewById(R.id.txtWalletBalance);

        purseArrayList = (ArrayList<Purse>) getIntent().getSerializableExtra("purseArrayList");

        ListView list = (ListView) findViewById(R.id.listmEnvelop);
        EnvelopAdapter envelopAdapter = new EnvelopAdapter(this, R.layout.menvelop_row_layout, purseArrayList, list);
        list.setAdapter(envelopAdapter);

        //  txtWalletBalance.setText(Html.fromHtml("<u><b>Wallet Balance - Rs "+userInfoPref.getString("walletbalance","0")+"</b></u>"));

    }

public void knowMore(View v)
{
    Intent s6 = new Intent(getApplicationContext(), WebActivity.class);
    s6.putExtra("option", "purse");
    startActivity(s6);
}
    public class EnvelopAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater Layf;
        int viewid;
        ArrayList<Purse> purseArrayList;
        ListView list;

        public EnvelopAdapter(Context context, int viewid, ArrayList<Purse> purseArrayList, ListView list) {
            // TODO Auto-generated constructor stub
            this.context = context;
            this.Layf = LayoutInflater.from(context);
            this.viewid = viewid;
            this.purseArrayList = purseArrayList;
            this.list = list;

        }

        @Override
        public int getCount() {
            return purseArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return purseArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = Layf.inflate(viewid, null);

                holder.linEnvelopParent = (LinearLayout)convertView.findViewById(R.id.linEnvelopParent);
                holder.imgEnvelop = (ImageView) convertView
                        .findViewById(R.id.imgEnvelop);
                holder.txtEnvelop = (TextView) convertView
                        .findViewById(R.id.txtEnvelop);
                holder.txtEnvelopBalance = (TextView) convertView
                        .findViewById(R.id.txtEnvelopBalance);
                holder.txtBalShow = (TextView)convertView.findViewById(R.id.txtBalShow);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Log.d("Purse Name:::", purseArrayList.get(position).getPurseName());
           // holder.linEnvelopParent.setBackgroundColor(Color.parseColor(purseArrayList.get(position).getPurseBackgroundColor()));
            GradientDrawable shape = customView(convertView,Color.parseColor(purseArrayList.get(position).getPurseBackgroundColor()),Color.parseColor(purseArrayList.get(position).getPurseBackgroundColor()));
            holder.linEnvelopParent.setBackgroundDrawable(shape);
            holder.txtEnvelop.setText(purseArrayList.get(position).getPurseName());
            holder.txtEnvelop.setTextColor(Color.parseColor(purseArrayList.get(position).getPurseTextColor()));
            holder.txtEnvelopBalance.setText(context.getResources().getString(R.string.rupee_symbol) + purseArrayList.get(position).getPurseBalance());
            holder.txtEnvelopBalance.setTextColor(Color.parseColor(purseArrayList.get(position).getPurseTextColor()));
            holder.txtBalShow.setTextColor(Color.parseColor(purseArrayList.get(position).getPurseTextColor()));
            Picasso.with(PurseSubBalanceActivity.this).load(purseArrayList.get(position).getPurseImage()).networkPolicy(NetworkPolicy.NO_CACHE).into(holder.imgEnvelop);


            return convertView;
        }

        public class ViewHolder {
            ImageView imgEnvelop;
            TextView txtEnvelop, txtEnvelopBalance,txtBalShow;
            LinearLayout linEnvelopParent;

        }

        public  GradientDrawable customView(View v, int backgroundColor, int borderColor)
        {
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadii(new float[] { 10,10, 10, 10, 0, 0, 0, 0});
            shape.setColor(backgroundColor);
            shape.setStroke(3, borderColor);
            return  shape;

        }
    }


}
