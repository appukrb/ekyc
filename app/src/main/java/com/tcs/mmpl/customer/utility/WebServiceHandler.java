package com.tcs.mmpl.customer.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import com.tcs.mmpl.customer.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;

/**
 * Created by Admin on 9/15/2015.
 */
public class WebServiceHandler {
    private static final int MODE_PRIVATE = 0;
    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;
    public final static int POST1 = 3;
    public final static int POST2 = 4;

    String Recharge;

    private static HostnameVerifier DO_NOT_VERIFY = null;

    Context context;


//    MultipartEntityBuilder multipartEntityBuilder;
    StringEntity se;
    List<NameValuePair> nameValuePairs;

    SharedPreferences userInfoPref;
    SharedPreferences.Editor userInfoEditor;

    private String uniqueKey = "";


    public WebServiceHandler(Context context)
    {
        this.context =context;
        userInfoPref = context.getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();
        uniqueKey=getIMEI(context);

        userInfoPref = context.getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();
    }

    public WebServiceHandler(Context context,StringEntity se)
    {
        this.context =context;
        this.se=se;
        uniqueKey=getIMEI(context);

        userInfoPref = context.getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

    }

//    public WebServiceHandler(Context context,MultipartEntityBuilder multipartEntityBuilder)
//    {
//        this.context =context;
//        this.multipartEntityBuilder=multipartEntityBuilder;
//    }

    public WebServiceHandler(Context context,List<NameValuePair> nameValuePairs)
    {
        this.context =context;
        this.nameValuePairs=nameValuePairs;
        uniqueKey=getIMEI(context);

        userInfoPref = context.getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

    }

    public String getIMEI(Context context) {
        String IMEI = "";

        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            IMEI = mTelephonyManager.getDeviceId();
        } catch (Exception e) {
            IMEI = "";
        }

        return IMEI;
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

            //original code starts

//            SSLSocketFactory sslFactory = null;
//			try {
//				sslFactory = new SimpleSSLSocketFactory(null);
//			} catch (KeyManagementException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (UnrecoverableKeyException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (NoSuchAlgorithmException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (KeyStoreException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//            sslFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//
//            DefaultHttpClient client = new DefaultHttpClient();
//
//         // Enable HTTP parameters
//            HttpParams paramsSecure = new BasicHttpParams();
//            HttpProtocolParams.setVersion(paramsSecure, HttpVersion.HTTP_1_1);
//            HttpProtocolParams.setContentCharset(paramsSecure, HTTP.UTF_8);
//
//
//
//            SchemeRegistry registry = new SchemeRegistry();
//            registry.register(new Scheme("https", sslFactory, 443));
//
//
//            ClientConnectionManager ccm = new ThreadSafeClientConnManager(paramsSecure, registry);
//            httpClient = new DefaultHttpClient(ccm, client.getParams());

//             //ends here

            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("Source", "b3JpZ2luYWwgU3RyaW5nIGJlZm9yZSBiYXNlNjQgZW5jb2RpbmcgaW4gSmF2YQ==");
                httpPost.setHeader("Authorization", "b3JpZ2luYWwgU3RyaW5nIGJlZm9yZSBiYXNlNjQgZW5jb2RpbmcgaW4gSmF2YQ==");
                httpPost.setHeader("unique", uniqueKey);
                httpPost.setHeader("Version",context.getResources().getString(R.string.app_version));
                httpPost.setHeader("userType",userInfoPref.getString("usertype",""));

                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }
                //// System.out.println("URl: "+ url);


                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the
                // content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                httpResponse = httpClient.execute(httpPost);

            }
            else if (method == POST1) {
                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("Source", "b3JpZ2luYWwgU3RyaW5nIGJlZm9yZSBiYXNlNjQgZW5jb2RpbmcgaW4gSmF2YQ==");
                httpPost.setHeader("Authorization", "b3JpZ2luYWwgU3RyaW5nIGJlZm9yZSBiYXNlNjQgZW5jb2RpbmcgaW4gSmF2YQ==");
                httpPost.setHeader("unique", uniqueKey);
                httpPost.setHeader("Version",context.getResources().getString(R.string.app_version));
                httpPost.setHeader("userType",userInfoPref.getString("usertype",""));
//                // System.out.println("user"+userInfoPref.getString("usertype",""));

//                httpPost.setHeader("Email",userInfoPref.getString("emailId", ""));



                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }
                //// System.out.println("URl: "+ url);


                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                httpResponse = httpClient.execute(httpPost);

            }
            else if (method == POST2) {
                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("Source", "b3JpZ2luYWwgU3RyaW5nIGJlZm9yZSBiYXNlNjQgZW5jb2RpbmcgaW4gSmF2YQ==");
                httpPost.setHeader("Authorization", "b3JpZ2luYWwgU3RyaW5nIGJlZm9yZSBiYXNlNjQgZW5jb2RpbmcgaW4gSmF2YQ==");
                httpPost.setHeader("unique", uniqueKey);
                httpPost.setHeader("Version",context.getResources().getString(R.string.app_version));
                httpPost.setHeader("userType",userInfoPref.getString("usertype",""));
//                httpPost.setHeader("Email",userInfoPref.getString("emailId",""));



                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }
                //// System.out.println("URl: "+ url);

                httpResponse = httpClient.execute(httpPost);

            }
            else if (method == GET) {
                // appending params to url
                //// System.out.println(".................GET method....................");
                if (params != null) {
                    String paramString = URLEncodedUtils.format(params, "utf-8");
                    url += "?" + paramString;
                }
                //// System.out.println("URl: "+ url);
                HttpGet httpGet = new HttpGet(url);

                httpResponse = httpClient.execute(httpGet);

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
