package com.tcs.mmpl.customer.utility;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;

/**
 * Created by hp on 2016-12-15.
 */
public class CityListDialog extends Dialog implements View.OnClickListener {

    private ListView list;
    private EditText filterText = null;
    ArrayAdapter<String> adapter = null;
    private static final String TAG = "CityList";

    public CityListDialog(Context context, String[] cityList, final TextView textView) {
        super(context);


        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_citylist);
        dialog.setTitle("");

        filterText = (EditText)dialog.findViewById(R.id.EditBox);
        filterText.addTextChangedListener(filterTextWatcher);
        list = (ListView) dialog.findViewById(R.id.List);
        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, cityList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Log.d(TAG, "Selected Item is = " + (String)list.getItemAtPosition(position));
                textView.setText((String)list.getItemAtPosition(position));
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    @Override
    public void onClick(View v) {

    }
    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            adapter.getFilter().filter(s);
        }
    };
    @Override
    public void onStop(){
        filterText.removeTextChangedListener(filterTextWatcher);
    }}
