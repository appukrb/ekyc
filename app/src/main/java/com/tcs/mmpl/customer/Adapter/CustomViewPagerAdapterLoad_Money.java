package com.tcs.mmpl.customer.Adapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tcs.mmpl.customer.fragments.Manage_Beneficiary;
import com.tcs.mmpl.customer.fragments.MoneyTransfer;
import com.tcs.mmpl.customer.fragments.QuickTransfer;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.fragments.RechargeAndPay;
import com.tcs.mmpl.customer.fragments.RequestMoney;
import com.tcs.mmpl.customer.Activity.TransferToBank;
import com.tcs.mmpl.customer.fragments.TransferToWallet;

public class CustomViewPagerAdapterLoad_Money extends FragmentPagerAdapter
{
	
	Context context;
	public String tabtitles_loadMoney[];
    private int tabxml_loadMoney[] = {R.layout.fragment_tab_recharge_pay,R.layout.fragment_tab_money_transfer,R.layout.fragment_tab_offers,R.layout.fragment_tab_quick_transfer,R.layout.fragment_tab_transfer_to_bank,R.layout.fragment_tab_request_money,R.layout.fragment_tab_check_transaction,R.layout.fragment_tab_manage_beneficiary,R.layout.fragment_tab_transfer_to_wallet} ;

	public CustomViewPagerAdapterLoad_Money(FragmentManager fm, Context context)
	{
		super(fm);
		this.context = context;
        tabtitles_loadMoney=context.getResources().getStringArray(R.array.operationTypeList1);
		// TODO Auto-generated constructor stub
	}
    @Override
    public int getCount()
    {
        return tabtitles_loadMoney.length;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {

            case 0:
                // Top Rated fragment activity
                return new RechargeAndPay();
            case 1:
                // Games fragment activity
                return new MoneyTransfer();

            case 2:
                // Top Rated fragment activity
               return new QuickTransfer();
            case 3:
                // Games fragment activity
                return new QuickTransfer();
            case 4:
                // Movies fragment activity
                return new TransferToBank();

            case 5:
                // Movies fragment activity
                return new RequestMoney();

            case 7:
                // Movies fragment activity
                return new Manage_Beneficiary();
            case 8:
                // Movies fragment activity
                return new TransferToWallet();

        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return tabtitles_loadMoney[position];
    }
	
	


}
