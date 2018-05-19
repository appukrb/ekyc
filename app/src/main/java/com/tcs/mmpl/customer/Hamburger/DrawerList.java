package com.tcs.mmpl.customer.Hamburger;

import android.content.Context;

import com.tcs.mmpl.customer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 01-03-2016.
 */
public class DrawerList {

    Context context;

    public DrawerList(Context context)
    {
        this.context = context;

    }

    public List<ObjectDrawerItem> getMenuList()
    {
        List<ObjectDrawerItem> list = new ArrayList<ObjectDrawerItem>();


        ObjectDrawerItem objectDrawerItem1 = new ObjectDrawerItem(context.getResources().getString(R.string.myaccount));
        ObjectDrawerItem objectDrawerItem2 = new ObjectDrawerItem(context.getResources().getString(R.string.requestmoney));
//        ObjectDrawerItem objectDrawerItem3 = new ObjectDrawerItem(context.getResources().getString(R.string.travel));
//        ObjectDrawerItem objectDrawerItem4 = new ObjectDrawerItem(context.getResources().getString(R.string.shopping));
        ObjectDrawerItem objectDrawerItem5 = new ObjectDrawerItem(context.getResources().getString(R.string.coupons1));
        ObjectDrawerItem objectDrawerItem6 = new ObjectDrawerItem(context.getResources().getString(R.string.FAQs));
        ObjectDrawerItem objectDrawerItem7 = new ObjectDrawerItem(context.getResources().getString(R.string.rateus1));
        ObjectDrawerItem objectDrawerItem8 = new ObjectDrawerItem(context.getResources().getString(R.string.feedback1));
        ObjectDrawerItem objectDrawerItem9 = new ObjectDrawerItem(context.getResources().getString(R.string.termsconditions));
        ObjectDrawerItem objectDrawerItem10 = new ObjectDrawerItem(context.getResources().getString(R.string.privacypolicy));
        ObjectDrawerItem objectDrawerItem11 = new ObjectDrawerItem(context.getResources().getString(R.string.aboutus));
        ObjectDrawerItem objectDrawerItem12 = new ObjectDrawerItem(context.getResources().getString(R.string.contactus));
        ObjectDrawerItem objectDrawerItem13 = new ObjectDrawerItem("Help");
        ObjectDrawerItem objectDrawerItem14 = new ObjectDrawerItem("My Issues");


        list.add(objectDrawerItem1);
        list.add(objectDrawerItem2);
//        list.add(objectDrawerItem3);
//        list.add(objectDrawerItem4);
        list.add(objectDrawerItem5);
        list.add(objectDrawerItem6);
        list.add(objectDrawerItem7);
        list.add(objectDrawerItem8);
        list.add(objectDrawerItem9);
        list.add(objectDrawerItem10);
        list.add(objectDrawerItem11);
        list.add(objectDrawerItem12);
        list.add(objectDrawerItem13);
        list.add(objectDrawerItem14);

        return list;
    }
}
