package com.jyh.byzb.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.jyh.byzb.GenseeActivity;
import com.jyh.byzb.GotyeLiveActivity;
import com.jyh.byzb.R;
import com.jyh.byzb.view.MyWebView;

public class Fragment_kefu extends Fragment {
    private String uri;
    private MyWebView webView;
    private View tv;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            uri = getArguments().getString("url");
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_kefu, null);
        tv = view.findViewById(R.id.tvId);
        tv.setVisibility(View.GONE);
        webView = (MyWebView) view.findViewById(R.id.webView1);
        webView.setVisibility(View.VISIBLE);
        progressBar = (ProgressBar) view.findViewById(R.id.web_progressBar);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onPageFinished(WebView v, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(v, url);

                try {
                    if (getActivity() instanceof GenseeActivity) {
                        ((GenseeActivity) getActivity()).onServiceBtnChange(url.equals(uri));
                    }
                    if (getActivity() instanceof GotyeLiveActivity) {
                        ((GotyeLiveActivity) getActivity()).onServiceBtnChange(url.equals(uri));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (url.contains("mqqwpa")) {
                    v.setVisibility(View.GONE);
                } else
                    v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView v, int errorCode, String description, String failingUrl) {
                v.setVisibility(View.GONE);
                v.loadUrl(uri);
            }

            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (url.startsWith("http") || url.startsWith("https")) {
                    return super.shouldInterceptRequest(view, url);
                } else {
                    Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(in);
                    return null;
                }
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress != 100) {
                    if (progressBar.getVisibility() == View.GONE)
                        progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);// 是否支持JavaScript
        webView.getSettings().setBuiltInZoomControls(true);//
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setLoadsImagesAutomatically(true);// 是否加载图片
        webView.removeJavascriptInterface("searchBoxJavaBridge_");
        webView.addJavascriptInterface(new isQQ(), "QQ");
        webView.loadUrl(uri);
        return view;
    }

    public void reload() {
        try {
            if (webView.canGoBack()) {
                webView.loadUrl(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class isQQ {
        @JavascriptInterface
        public boolean isAppInstalled() {
            PackageInfo packageInfo;
            try {
                packageInfo = getActivity().getPackageManager().getPackageInfo("com.tencent.mobileqq", 0);
            } catch (Exception e) {
                packageInfo = null;
                e.printStackTrace();
            }
            if (packageInfo == null) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        webView.loadUrl(uri);
    }

}
