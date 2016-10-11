package com.sunzhk.tools;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

@SuppressLint({"setJavaScriptEnabled", "JavascriptInterface"})
public class BaseWebFragment extends Fragment {
	
	private String mUrl;

	private String mDefaultUrl = "";
	
	private WebView mWebView;
	
	
	public BaseWebFragment() {
		// TODO Auto-generated constructor stub
	}
	
	public BaseWebFragment(String url) {
		// TODO Auto-generated constructor stub
		mUrl = url;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mUrl = mUrl == null && getArguments() != null ? getArguments().getString("") : null;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mWebView = new WebView(getActivity());
		mWebView.getSettings().setJavaScriptEnabled(true);
		
		
		
		mWebView.loadUrl(mUrl == null || mUrl.trim().isEmpty() ? mDefaultUrl : mUrl);
		return mWebView;
	}
	
	protected void setDefaultUrl(String defaultUrl) {
		mDefaultUrl = defaultUrl;
	}
	
	protected WebView getWebView(){
		return mWebView;
	}
	
}
