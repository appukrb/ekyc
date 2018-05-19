package com.tcs.mmpl.customer.Activity;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.tcs.mmpl.customer.Adapter.BrowsePlanTabListAdapter;
import com.tcs.mmpl.customer.utility.MyDBHelper;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.Adapter.BrowsePlanMain;

import java.util.ArrayList;


public class BrowsePlanTabListActivity extends ListActivity {

    Cursor cursor;
    MyDBHelper dbHelper;

    BrowsePlanTabListAdapter adapter;
    String str_tab_selected;
    ArrayList<String> packdata;
    ArrayList<String> packageid;
    ArrayList<BrowsePlanMain> classAs;
    TextView textView_category;
    Spinner spinner_category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_plan_tab_list);



        //// System.out.println("tab selected in list activity: "+ this.getIntent().getExtras().getString("selected_tab"));
        str_tab_selected=this.getIntent().getExtras().getString("selected_tab");
        classAs=(ArrayList<BrowsePlanMain>) this.getIntent().getSerializableExtra("dataObj");
        //dbHelper= new MyDBHelper(this);

        //// System.out.println("...in add on pack list activity........................."+classAs.size());
        //    oThread.start();
        final ListView lstView = getListView();
        packdata = new ArrayList<String>();
        packageid=new ArrayList<String>();
        for(int i=0;i<classAs.size();i++)
        {
            if(classAs.get(i).getSegName().toString()==str_tab_selected)
            {
                for(int j=0;j<classAs.get(i).getPackDetails().size();j++)
                {
                    packdata.add(classAs.get(i).getPackDetails().get(j).getPackName());
                    packageid.add(classAs.get(i).getPackDetails().get(j).getPackId());
                }
            }

        }


        lstView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        adapter = new BrowsePlanTabListAdapter(BrowsePlanTabListActivity.this,R.layout.browse_plannew_layout,lstView,packdata,packageid);
        setListAdapter(adapter);


//        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                Intent returnIntent = new Intent();
//                returnIntent.putExtra("result", "25");
//
//                if (getParent() == null) {
//                    setResult(3, returnIntent);
//                } else {
//                    getParent().setResult(3, returnIntent);
//                }
//
//                finish();
//            }
//        });


    }



}
