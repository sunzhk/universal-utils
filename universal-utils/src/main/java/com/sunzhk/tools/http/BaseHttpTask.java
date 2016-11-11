package com.sunzhk.tools.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HttpStack;
import com.sunzhk.tools.BaseApplication;
import com.sunzhk.tools.utils.Util;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class BaseHttpTask{

	/**
	 * 访问失败时自动显示错误信息。
	 */
	public static final boolean AUTO_SHOW_ERROR = true;
	/**
	 * 访问失败时自动隐藏错误信息。
	 */
	public static final boolean AUTO_HIDE_ERROR = false;
	/**
	 * volley队列
	 */
	private static RequestQueue mRequestQueue;
	/**
	 * 缓存路径
	 */
	private static String cachePath;
	/**
	 * 服务器URL  http://XXX.XXX.XXX.XXX:XXXX/HttpHandler.ashx
	 */
	private static String serviseUrl = "";
	/**
	 * 是否需要打印json日志
	 */
	private static boolean isDebug = false;
	/**
	 * 处理ID
	 */
	private int handeId;
	/**
	 * 重试次数
	 */
	private int maxNumRetries = 1;
	/**
	 * 超时时间
	 */
	private int timeout = 10000;
	/**
	 * 等待框
	 */
	private ProgressDialog waitLoad;
	/**
	 * 回调
	 */
	private DoAfterHttp willHandleHttp;
	/**
	 * 是否自动显示错误信息
	 */
	private static boolean mIsNeedAutoErrorMessage = AUTO_SHOW_ERROR;
	/**
	 * 初始化volley队列
	 * @param context
	 */
	public static void init(Context context){
		init(context, 4, -1);
	}
	
	/**
	 * 初始化volley队列
	 * @param context
	 * @param threadPoolSize 线程池大小
	 * @param maxDiskCacheBytes 最大缓存大小，单位byte；小于等于-1则无限制
	 */
	public static void init(Context context, int threadPoolSize, int maxDiskCacheBytes){

		File cacheDir = new File(BaseApplication.getRootCacheDirPath(), "volley");

		cachePath = cacheDir.getAbsolutePath();

//		String userAgent = "volley/0";
//		try {
//			String packageName = context.getPackageName();
//			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
//			userAgent = packageName + "/" + info.versionCode;
//		} catch (NameNotFoundException e) {
//		}
		
		HttpStack stack = new OkHttpStack();
//        if (Build.VERSION.SDK_INT >= 9) {
//            stack = new HurlStack();
//        } else {
//            // Prior to Gingerbread, HttpUrlConnection was unreliable.
//            // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
//            stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
//        }

		Network network = new BasicNetwork(stack);
		
		if (maxDiskCacheBytes <= -1){
			// No maximum size specified
			mRequestQueue = new RequestQueue(new DiskBasedCache(cacheDir), network, threadPoolSize);
		}
		else{
			// Disk cache size specified
			mRequestQueue = new RequestQueue(new DiskBasedCache(cacheDir, maxDiskCacheBytes), network, threadPoolSize);
		}
		
		mRequestQueue.start();
	}
	public static void setIsDebug(boolean isDebug){
		BaseHttpTask.isDebug = isDebug;
	}
	/**
	 * 开始访问网络
	 * 
	 * @param request 向服务器发送的数据，如果为实体类，会被转化成JSON串，如为String则不作处理
	 */
	public void execute(Object request) {
		if (waitLoad != null && !waitLoad.isShowing()) waitLoad.show();
		HttpRequest httpRequest = new HttpRequest(request);
		httpRequest.setRetryPolicy(new DefaultRetryPolicy(timeout, maxNumRetries, 1.0f));
		mRequestQueue.add(httpRequest);
	}
	/**
	 * 开始访问网络
	 * 
	 * @param request 向服务器发送的数据，如果为实体类，会被转化成JSON串，如为String则不作处理
	 * @param url 这服务器的URL，仅本次有效。
	 */
	public void onceExecute(Object request, String url) {
		if (waitLoad != null && !waitLoad.isShowing()) waitLoad.show();
		HttpRequest httpRequest;
		if(request instanceof String){
			httpRequest = new HttpRequest(url, (String)request);
		}else{
			httpRequest = new HttpRequest(url, request);
		}
		httpRequest.setRetryPolicy(new DefaultRetryPolicy(timeout, maxNumRetries, 1.0f));
		mRequestQueue.add(httpRequest);
	}
	
	public BaseHttpTask setCallBack(DoAfterHttp callBack, int handleId){
		willHandleHttp = callBack;
		this.handeId = handleId;
		return this;
	}
	
	/**
	 * 如果后台运行时需要等待对话框，则传入一个context.这个方法应该在execute之前设置
	 * 
	 * @param context
	 * @param msg
	 *            等待对话框上显示的信息
	 */
	public BaseHttpTask needWaitDialog(Context context, String msg){
		if (context == null) waitLoad = null;
		else{
			waitLoad = new ProgressDialog(context);
			waitLoad.setMessage(TextUtils.isEmpty(msg) ? "请稍等。。。" : msg);
		}
		return this;
	}
	/**
	 * 设置超时时间(以毫秒为单位)，默认10秒
	 * @param
	 */
	public BaseHttpTask setTimeout(int time) {
		timeout=time;
		return this;
	}
	/**
	 * 设置重试次数
	 * @param maxNumRetries
	 */
	public BaseHttpTask setMaxNumRetries(int maxNumRetries) {
		this.maxNumRetries = maxNumRetries + 1;
		return this;
	}
	/**
	 * 设置处理错误的模式
	 * 
	 * @param mode
	 *            只能是AUTO_SHOW_ERROR, AUTO_HIDE_ERROR, ALWAYS_CALLBACK之一.
	 *            AUTO_SHOW_ERROR这个模式下必须确保context传入.
	 */
	public static void setDealErrorModel(boolean mode){
		mIsNeedAutoErrorMessage = mode;
	}
	/**
	 * 设置是否打印json日志，默认不打印
	 * @param isDebug
	 * @return
	 */
	public static void enableDebug(Boolean isDebug){
		BaseHttpTask.isDebug = isDebug;
	}
	/**
	 * 设置服务的URL. 比如 http://192.168.0.67:8001/HttpHandler.ashx
	 * 
	 * @param url 这个URL只需设置一次。当调用execute(T requst)这个方法用的地址就是这个URL；
	 *            如果有另外的一次性的服务要访问，请使用onceExecute(T requst,String
	 *            url)而不是execute(T requst)访问网络。
	 */
	public static void setServiseUrl(String url){
		serviseUrl = url;
	}

	/**
	 * 获取缓存路径
	 * @return
     */
	public static String getCachePath(){
		return cachePath;
	}

	public static void clearCache(){
		//清理volley cache
		if(!TextUtils.isEmpty(cachePath)){
			File volleyCacheDir = new File(cachePath);
			if(volleyCacheDir.exists() && volleyCacheDir.isDirectory()){
				File[] files = volleyCacheDir.listFiles();
				for(File temp : files){
					temp.delete();
				}
			}
		}
	}

	/**
	 * 网络访问请求类，用于封装请求内容及做出响应
	 * @author sunzhk
	 *
	 */
	public class HttpRequest extends Request<String>{

		private String json;
		
		public HttpRequest(Object src) {
			// TODO Auto-generated constructor stub
//			this(serviseUrl, (src instanceof String) ? (String) src : Util.GSON.toJson(src));
			this(serviseUrl, (src instanceof String) ? (String) src : Util.GSON.toJson(src));
		}
		
		public HttpRequest(String url, Object src) {
			// TODO Auto-generated constructor stub
			super(Method.POST, url, null);
			this.json = (src instanceof String) ? (String) src : Util.GSON.toJson(src);
		}

		@Override
		public byte[] getBody() throws AuthFailureError {
			// TODO Auto-generated method stub
			if(isDebug){
				Log.v("HttpTask", "Http request body\r\n"+json+"");
			}
			if(!TextUtils.isEmpty(json)){
				return json.getBytes();
			}
			return null;
		}
		
		
		
		@Override
		protected Response<String> parseNetworkResponse(NetworkResponse response) {
			// TODO Auto-generated method stub
			String parsed;
//			Log.e("charset", response.headers.get("Content-Type"));
	        try {
	            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
	        } catch (UnsupportedEncodingException e) {
	            parsed = new String(response.data);
	        }
	        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
		}

		@Override
		protected void deliverResponse(String response) {
			// TODO Auto-generated method stub
			if(isDebug){
				Log.v("HttpTask", "Http response\r\n"+response);
			}
			callBack(true, response);
		}
		
		@Override
		public void deliverError(VolleyError error) {
			// TODO Auto-generated method stub
			if(isDebug){
				Log.v("HttpTask", "Http request error\r\n"+error.getMessage()+"\r\n"+error.getCause());
			}
			callBack(false, "访问服务器失败\r\b"+error.getMessage()+"\r\n"+error.getCause()+"\r\n"+error.getStackTrace().toString());
		}
		
		private void callBack(boolean isSuccess, String result){
			
			if (waitLoad != null && waitLoad.isShowing()){
				waitLoad.dismiss();
			}
			waitLoad = null;
			if(result==null){
				return;
			}
			if(willHandleHttp!=null){
				willHandleHttp.doAfterHttp(handeId, result);
			}
			if (!isSuccess && mIsNeedAutoErrorMessage){
				BaseApplication.showToast(result);
			}
			
		}
//		/**
//		 * 返回错误回应
//		 * @param msg 错误信息
//		 * @return 回应
//		 */
//		private response errorRes(String msg) {
//			StringBuilder sb = new StringBuilder();
//			JsonObject errResponse = new JsonObject();
//			errResponse.addProperty("result", 1);
//			errResponse.addProperty("rMsg", msg);
////			AndLog.appendLog(msg + " -- 类名：" + willHandleHttp.getClass().getName() + " handeId=" + handeId);
//			return sb.toString();
//		}
		
	}
	
	/**
	 * 网络访问回调接口
	 */
	public interface DoAfterHttp {
		/**
		 * 当访问网络后会自动执行这个方法
		 * 
		 * @param jsonResponse
		 *            网络返回的数据
		 * @param handleId
		 *            执行网络访问的id
		 */
		void doAfterHttp(int handleId, String jsonResponse);
	}

}
