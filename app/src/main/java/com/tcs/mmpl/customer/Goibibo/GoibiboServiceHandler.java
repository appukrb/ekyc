package com.tcs.mmpl.customer.Goibibo;

import android.content.Context;

import com.tcs.mmpl.customer.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;

/**
 * Created by hp on 2016-07-15.
 */

public class GoibiboServiceHandler {
    static String response = null;
    public final static int POST = 2;
    public final static int POST1 = 3;
    public final static int POST2 = 4;
    public final static int POST3 = 5;

    String Recharge;

    private static HostnameVerifier DO_NOT_VERIFY = null;

    Context context;


    StringEntity se;
    List<NameValuePair> nameValuePairs;


    public GoibiboServiceHandler(Context context)
    {
        this.context =context;
    }

    public GoibiboServiceHandler(Context context,StringEntity se)
    {
        this.context =context;
        this.se=se;
    }

    public GoibiboServiceHandler(Context context,List<NameValuePair> nameValuePairs)
    {
        this.context =context;
        this.nameValuePairs=nameValuePairs;
    }
    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * */
    public String makeServiceCall(String url, int method) {
        return this.makeServiceCall(url, method, null);
    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     * */
    @SuppressWarnings("static-access")
    public String makeServiceCall(String url, int method,
                                  List<NameValuePair> params) {
        try {

            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("Source", "b3JpZ2luYWwgU3RyaW5nIGJlZm9yZSBiYXNlNjQgZW5jb2RpbmcgaW4gSmF2YQ==");
                httpPost.setHeader("Authorization", "b3JpZ2luYWwgU3RyaW5nIGJlZm9yZSBiYXNlNjQgZW5jb2RpbmcgaW4gSmF2YQ==");
                httpPost.setHeader("Version",context.getResources().getString(R.string.app_version));

                // 7. Set some headers to inform server about the type of the
                httpResponse = httpClient.execute(httpPost);

            }

            else if (method == POST1) {
                //// System.out.println(".................POST1 method....................");

                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("Source", "b3JpZ2luYWwgU3RyaW5nIGJlZm9yZSBiYXNlNjQgZW5jb2RpbmcgaW4gSmF2YQ==");
                httpPost.setHeader("Authorization", "b3JpZ2luYWwgU3RyaW5nIGJlZm9yZSBiYXNlNjQgZW5jb2RpbmcgaW4gSmF2YQ==");
                httpPost.setHeader("Version",context.getResources().getString(R.string.app_version));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httpResponse = httpClient.execute(httpPost);
            }
            else if (method == POST2) {
                //// System.out.println(".................POST1 method....................");

                HttpGet httpGet = new HttpGet(url);
                httpGet.setHeader("Authorization", "Basic YXBpdGVzdGluZ0Bnb2liaWJvLmNvbTpkZW1vMTIz");
                httpResponse = httpClient.execute(httpGet);
            }
            else if (method == POST3) {
                //// System.out.println(".................POST1 method....................");

                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("Authorization", "Basic YXBpdGVzdGluZ0Bnb2liaWJvLmNvbTpkZW1vMTIz");
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httpResponse = httpClient.execute(httpPost);
            }

            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;

    }



}

