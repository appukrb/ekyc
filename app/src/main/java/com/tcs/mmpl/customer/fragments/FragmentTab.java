package com.tcs.mmpl.customer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;

/**
 * Created by Admin on 9/9/2015.
 */
public class FragmentTab extends Fragment
{
    Spinner spnr;
    Button btnPayment;
    Context context;

    public String location[];
    int tabxml[] ;
    View view;
    Fragment fragment_proceed_payment;
    int positonTab = 0;

    public FragmentTab()
    {

    }
    public FragmentTab(Context context, int position, int[] tabxml)
    {
        this.context = context;
        positonTab = position;
        this.tabxml = tabxml;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View view = inflater.inflate(tabxml[positonTab], container, false);
        spnr = (Spinner) view.findViewById(R.id.spinner);
        location = getResources().getStringArray(R.array.location);


       spnr.setAdapter(new MyCustomAdapter(view.getContext(),
                R.layout.textfile, location));

        spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {

				/*Toast.makeText(parentView.getContext(),
						"You have selected " + location[+position],
						Toast.LENGTH_LONG).show();*/


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        return view;

    }

    public class MyCustomAdapter extends ArrayAdapter<String> {


        private Context context;
//		LayoutInflater inflater;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               String[] objects) {
            super(context, textViewResourceId, objects);

        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {
            // return super.getView(position, convertView, parent);

            LayoutInflater inflater = getActivity().getLayoutInflater();
            convertView = inflater.inflate(R.layout.textfile, parent, false);
            TextView label = (TextView) convertView.findViewById(R.id.txting);
            label.setText(location[position]);
            return convertView;
        }
    }



}
