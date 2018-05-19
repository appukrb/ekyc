package com.tcs.mmpl.customer.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by hp on 25-11-2015.
 */
public class Contact {

    private Activity activity;
    private  String contactNumber="",Isd_code="+91";
    private EditText edtMoblie;
    private int count =10;
    private CharSequence[] alteredNumbers;
    public Contact(Context context,EditText edtMoblie)
    {
        this.activity = (Activity)context;
        this.edtMoblie = edtMoblie;
    }
    public Contact(Context context,EditText edtMoblie,String Isd_code,int count)
    {
        this.activity = (Activity)context;
        this.edtMoblie = edtMoblie;
        this.Isd_code=Isd_code;
        this.count=count;
    }
    public void getContactNumber(String id)
    {

        try {
            // query for phone numbers for the selected contact id
            Cursor c = activity.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                    new String[]{id}, null);

            int phoneIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int phoneType = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
            //// System.out.println("Count of numbers length" + c.getCount());

            if (c.getCount() > 1) { // contact has multiple phone numbers
                final CharSequence[] numbers = new CharSequence[c.getCount()];
                alteredNumbers = new CharSequence[c.getCount()];

                int i = 0;
                if (c.moveToFirst()) {
                    while (!c.isAfterLast()) { // for each phone number, add it to the numbers array
                        String type = (String) ContactsContract.CommonDataKinds.Phone.getTypeLabel(activity.getResources(), c.getInt(phoneType), ""); // insert a type string in front of the number
                        String number = type + ": " + c.getString(phoneIdx);
                        numbers[i++] = number;
                        c.moveToNext();
                    }

                    int count = numbers.length;

                    Set<CharSequence> val = new HashSet<CharSequence>();

                    for (int j = 0; j < count; j++) {
                        val.add(numbers[j]);
                    }

                    int k = 0;
                    Iterator it = val.iterator();


                    CharSequence temp;
                    while (it.hasNext()) {

                       temp = (CharSequence) it.next();
                        if(temp != null) {
                            alteredNumbers[k] = temp;
                            //// System.out.println("alteredNumbers" + alteredNumbers[k]);
                            k++;
                        }
                    }

                    List<CharSequence> list = new ArrayList<CharSequence>();
                    for(CharSequence s : alteredNumbers) {
                        if(s != null && s.length() > 0) {
                            list.add(s);
                        }
                    }

                    alteredNumbers = list.toArray(new CharSequence[list.size()]);

                    //// System.out.println("Altered Numbers length:" + alteredNumbers.length);
                    if (alteredNumbers.length == 1) {
                        String number = (String) alteredNumbers[0];
                        int index = number.indexOf(":");
                        number = number.substring(index + 2);
                        //edt_mobile.setText(number);
                        contactNumber = trimNumber(number);
                        edtMoblie.setText(contactNumber);
                    } else {
                        // build and show a simple dialog that allows the user to select a number
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("Select Number");
                        builder.setItems(alteredNumbers, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int item) {

                                    String number = (String) alteredNumbers[item];
                                    int index = number.indexOf(":");
                                    number = number.substring(index + 2);
                                    //edt_mobile.setText(number);

                                    contactNumber = trimNumber(number);
                                    edtMoblie.setText(contactNumber);
                                   // dialog.cancel();

                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.setOwnerActivity(activity);
                        alert.show();

                    }
                } else {
                    edtMoblie.setText(contactNumber);
                }
            } else if (c.getCount() == 1) {
                // contact has a single phone number, so there's no need to display a second dialog
                //// System.out.println("Coming inside................");
                c.moveToFirst();
                contactNumber = trimNumber(c.getString(phoneIdx).toString());
                //edt_mobile.setText(c.getString(phoneIdx).toString());
                //// System.out.println("contactNumber......... " + contactNumber);
                edtMoblie.setText(contactNumber);
            }
            c.close();

        }
        catch (Exception e)
        {

        }
    }


    public String trimNumber(String newContactNumber)
    {
                try
                {
                    newContactNumber = newContactNumber.replaceAll("\\s","");
                    newContactNumber = newContactNumber.replaceFirst("^0", "");
                    //// System.out.println("Removing zero......... "+newContactNumber);
                }
                catch(Exception e)
                {
                    //// System.out.println("Error Here in contact");
                }
                if(newContactNumber.contains("-"))
                {
                    newContactNumber = newContactNumber.replaceAll("-","");
                }


                if(newContactNumber.contains(Isd_code))
                {
                    //// System.out.println("Searching +91......... "+newContactNumber);
                    newContactNumber = newContactNumber.replaceAll("\\"+Isd_code, "");
                    //// System.out.println("Removing +91......... "+newContactNumber);
                }else
                {
                    if(newContactNumber.contains("+"))
                    {
                        newContactNumber = "";
                        Toast.makeText(activity,"Please choose proper number",Toast.LENGTH_LONG).show();
                    }
                }


            return  newContactNumber;
    }

}
