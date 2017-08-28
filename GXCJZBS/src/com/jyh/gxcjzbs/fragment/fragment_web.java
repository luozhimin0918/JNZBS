package com.jyh.gxcjzbs.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.jyh.gxcjzbs.R;

public class fragment_web extends Fragment {
    private String uri;
    private WebView webView;
    private View tv;

    private ProgressBar progressBar;

    public boolean error;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uri = getArguments().getString("url");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_web, null);
        getActivity().setTheme(R.style.BrowserThemeDefault);
        tv = view.findViewById(R.id.tvId);
        tv.setVisibility(View.GONE);
        progressBar = (ProgressBar) view.findViewById(R.id.web_progressBar);
//        progressBar = view.findViewById(R.id.img);
//        progressBar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.loading_animation));
        webView = (WebView) view.findViewById(R.id.webView1);
        webView.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView v, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(v, url);
//                progressView.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView v, int errorCode,
                                        String description, String failingUrl) {
                error = true;
                tv.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
//                progressView.setVisibility(View.GONE);
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
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setLoadsImagesAutomatically(true);// 是否加载图片
        webView.loadUrl(uri);
        return view;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}
