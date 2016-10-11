package com.sunzhk.tools;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sunzhk.R;

/**
 * 基础网页Activity
 * @author sunzhk
 *
 */
@SuppressLint({"setJavaScriptEnabled", "JavascriptInterface"})
public class BaseWebActivity extends BaseActivity {

	private String mUrl;
	protected WebView mWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_webmodel);
		mUrl = getIntent() != null ? getIntent().getStringExtra("URL") : "";
		mWebView = (WebView) findViewById(R.id.wvContainer_webmodel);
		mWebView.getSettings().setJavaScriptEnabled(true);

		// 让网页继续加载下一个(同超链接等引起的)网页
		mWebView.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon){// 网页页面开始加载的时候
				super.onPageStarted(view, url, favicon);
				View v=findViewById(R.id.pbLoading_webmodel);
				if(v!=null)v.setVisibility(View.VISIBLE);
				mWebView.setVisibility(View.GONE);
			}
			@Override
			public void onPageFinished(WebView view, String url){// 网页加载结束的时候
				super.onPageFinished(view, url);
				View v=findViewById(R.id.pbLoading_webmodel);
				if(v!=null)v.setVisibility(View.GONE);
				mWebView.setVisibility(View.VISIBLE);
			}
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url){
				view.loadUrl(url);
				return false;
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mWebView.loadUrl(mUrl);
	}

}
