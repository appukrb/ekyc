package com.tcs.mmpl.customer.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.tcs.mmpl.customer.Activity.ChangeMPINActivity;


/**
 * Created by Home1 on 4/23/2016.
 */
public class SmsReciever_Two extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs ;
        String messageReceived = "";
        if(bundle != null)
        {
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i<msgs.length; i++)
            {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                messageReceived += msgs[i].getMessageBody().toString();
                messageReceived += "\n";
            }
            Log.d("MSG", messageReceived);
            if (messageReceived.contains("mRUPEE"))
            {
                messageReceived = messageReceived.substring(0, 4);
                ChangeMPINActivity.getSmsDetails(messageReceived);
            }
        }
    }
}