package com.tcs.mmpl.customer.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;

import java.util.ArrayList;

/**
 * Created by hp on 04-10-2015.
 */
public class BeneficiaryAdapter  extends ArrayAdapter<String> {

    private static final int MODE_PRIVATE = 0;
    Context context;
    ListView listView;
    int layout_id;


    ArrayList<String> beneficiaryID,beneficiaryNickName,mobileNumber,accountNumber;


    public BeneficiaryAdapter(Context context, int resource,
                              ListView lstView,ArrayList<String> beneficiaryID,ArrayList<String> beneficiaryNickName,ArrayList<String> mobileNumber,ArrayList<String> accountNumber) {
        super(context, resource);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.layout_id=resource;
        this.listView= lstView;
        // this.message=message;
        this.beneficiaryID=beneficiaryID;
        this.beneficiaryNickName=beneficiaryNickName;
        this.mobileNumber=mobileNumber;
        this.accountNumber = accountNumber;

    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return beneficiaryID.size();
    }



    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        try {


            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            View rowView = inflater.inflate(layout_id, null, true);


            TextView txtBeneficiaryNickName = (TextView) rowView.findViewById(R.id.txtBeneficiaryNickName);
            TextView txtmobileNumber = (TextView) rowView.findViewById(R.id.txtMobileNumber);

            txtBeneficiaryNickName.setText(beneficiaryNickName.get(position));
            txtmobileNumber.setText(accountNumber.get(position));


            return rowView;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        }
    }

}
