package com.sunzhk.tools.utils.http;

import com.android.volley.toolbox.HurlStack;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class OkHttpStack extends HurlStack {

	private final OkUrlFactory okUrlFactory = new OkUrlFactory(new OkHttpClient());
	
	@Override
	protected HttpURLConnection createConnection(URL url) throws IOException {
		// TODO Auto-generated method stub
		return okUrlFactory.open(url);
	}
	
}
