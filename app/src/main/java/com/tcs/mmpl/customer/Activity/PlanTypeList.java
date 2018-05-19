package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;

import java.util.ArrayList;


public class PlanTypeList extends Activity {

    ListView listPlanType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_plan_type_list);

        ArrayList<String> type = new ArrayList<String>();
        type.add("Tariff Packs");
        type.add("Top Ups");
        type.add("Data Packs");
        type.add("Combo Packs");


        listPlanType = (ListView)findViewById(R.id.listPlanType);
        ArrayAdapter<String> adp=new ArrayAdapter<String> (getBaseContext(),
                android.R.layout.simple_dropdown_item_1line,type);
        adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        listPlanType.setAdapter(adp);


        listPlanType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String plan = ((TextView) view).getText().toString();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", plan);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

}
