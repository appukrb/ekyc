package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.Adapter.RequestMoneyList;

import java.util.ArrayList;


public class RequestMoneyStatementActivity extends Activity {

    private ListView listStatement;
    private ArrayList<RequestMoneyList> requestMoneyLists;
    FontClass fontclass = new FontClass();
    Typeface typeface;
    Typeface custom_font;
    private RelativeLayout linParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_money_statement);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        linParent = (RelativeLayout)findViewById(R.id.linParent);

        custom_font = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(linParent, typeface);

        requestMoneyLists = (ArrayList<RequestMoneyList>) getIntent().getSerializableExtra("statement");
        listStatement = (ListView) findViewById(R.id.listStatement);
        CustomListAdapter customListAdapter = new CustomListAdapter(RequestMoneyStatementActivity.this, requestMoneyLists,listStatement);
        listStatement.setAdapter(customListAdapter);

    }

    public void done(View v)
    {
        finish();
    }


    private class CustomListAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<RequestMoneyList> requestMoneyLists;
        private ListView list;

        public CustomListAdapter(Context context, ArrayList<RequestMoneyList> requestMoneyLists,ListView list) {
            this.context = context;
            this.requestMoneyLists = requestMoneyLists;
            this.list = list;
        }

        @Override
        public int getCount() {
            return requestMoneyLists.size();
        }

        @Override
        public Object getItem(int i) {
            return requestMoneyLists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            View row = inflater.inflate(R.layout.listview_requestmoneystatement, null, true);
            final TextView txtMobile = (TextView) row.findViewById(R.id.txtMobile);
            final TextView txtAmount = (TextView) row.findViewById(R.id.txtAmount);

            final RequestMoneyList requestMoneyList = requestMoneyLists.get(position);
            txtMobile.setText(requestMoneyList.getMobilenumber());
            txtMobile.setTypeface(custom_font);
            txtAmount.setText(getResources().getString(R.string.rupee_symbol)+requestMoneyList.getAmount());
            txtAmount.setTypeface(custom_font);

            return row;
        }


    }

}
