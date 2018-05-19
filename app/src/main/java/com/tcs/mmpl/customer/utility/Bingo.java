package com.tcs.mmpl.customer.utility;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by BeAsT on 5/2/2017.
 */

public class Bingo {
    public static  String Bingo_one(String message)  {

        try {
            String secret="rpsXYZABpcDKLMf56879nhkpO";
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);


            byte raw[] = sha256_HMAC.doFinal(message.getBytes());

            StringBuffer ls_sb=new StringBuffer();
            for(int i=0;i<raw.length;i++)
                ls_sb.append(char2hexsecurity(raw[i]));
            return ls_sb.toString();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String char2hexsecurity(byte x) {
        char arr[]={
                '3','1','0','k',
                '9','4','x','7',
                '8','s','A','B',
                'C','f','z','g'
        };

        char c[] = {arr[(x & 0xF0)>>4],arr[x & 0x0F]};
        return (new String(c));
    }
}
