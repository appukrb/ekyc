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


public class AccountTypeActivity extends Activity {

    ListView listAccountType;

    String[] accountType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_account_type);
        listAccountType = (ListView)findViewById(R.id.listAccountType);

        accountType = getResources().getStringArray(R.array.Account);

        ArrayAdapter<String> adp=new ArrayAdapter<String> (getBaseContext(),
                android.R.layout.simple_dropdown_item_1line,accountType);
        adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);


        listAccountType.setAdapter(adp);

        listAccountType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String dthOperatorName = ((TextView)view).getText().toString();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",dthOperatorName);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });
    }
}
