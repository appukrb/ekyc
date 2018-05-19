package com.tcs.mmpl.customer.fragments;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.FontClass;

/**
 * Created by hp on 2016-11-30.
 */
public class RechargeBillPayFragment extends Fragment {

    FontClass fontclass = new FontClass();
    Typeface typeface;

    private boolean isViewShown = false;
    private WebView webFAQ;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            try {
//                String category_url = getActivity().getResources().getString(R.string.getSHMCategory_url);
//                Log.i("url", category_url);
//                GetIssueCategory getIssueCategory = new GetIssueCategory(getActivity());
//                getIssueCategory.execute(category_url);
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            isViewShown = true;


        } else {
            isViewShown = false;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isViewShown = false;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recharge_billpay, container, false);
        setRetainInstance(true);


        webFAQ = (WebView) rootView.findViewById(R.id.webFAQ);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(getActivity().getResources().getColor(R.color.blue), PorterDuff.Mode.MULTIPLY);

        WebSettings webSettings = webFAQ.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webFAQ.clearCache(true);
        webFAQ.setWebViewClient(new WebViewClient());
        webFAQ.loadUrl(getActivity().getResources().getString(R.string.recharge_html_url));


        return rootView;
    }


    public class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            webFAQ.setVisibility(View.VISIBLE);

        }

    }
}
