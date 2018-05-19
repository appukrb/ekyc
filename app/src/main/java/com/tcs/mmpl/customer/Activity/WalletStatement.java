package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;

import java.util.ArrayList;


public class WalletStatement extends ListActivity {


    private ArrayList<String> amount,transid,date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_statement);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent i = getIntent();

        transid = (ArrayList<String>)i.getSerializableExtra("transid");
        amount = (ArrayList<String>)i.getSerializableExtra("amount");
        date = (ArrayList<String>)i.getSerializableExtra("date");

        //// System.out.println("Transid:::::::"+transid.size());
        //// System.out.println("Amount:::::::"+amount.size());

        //// System.out.println("Date:::::::"+date.size());



        final ListView lstView = getListView();

        lstView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        WalletStatementAdapter adapter = new WalletStatementAdapter(this, R.layout.wallet_statement_layout,lstView);
        setListAdapter(adapter);
    }

    public class WalletStatementAdapter  extends ArrayAdapter<String> {

        private static final int MODE_PRIVATE = 0;
        Context context;
        ListView listView;
        int layout_id;





        public WalletStatementAdapter(Context context, int resource,
                                  ListView lstView) {
            super(context, resource);
            // TODO Auto-generated constructor stub
            this.context=context;
            this.layout_id=resource;
            this.listView= lstView;
            // this.message=message;


        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return transid.size();
        }



        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            try {


                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                View rowView = inflater.inflate(layout_id, null, true);


                TextView txtTransid = (TextView) rowView.findViewById(R.id.txtTransid);
                TextView txtAmount = (TextView) rowView.findViewById(R.id.txtAmount);
                TextView txtDate = (TextView) rowView.findViewById(R.id.txtDate);

                txtTransid.setText(transid.get(position));
                txtAmount.setText(amount.get(position));
                txtDate.setText(date.get(position));


                return rowView;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                return null;
            }
        }

    }



}
