package com.sunzhk.tools.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.sunzhk.tools.BaseApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * catch program crash log
 * 捕获程序崩溃日志
 * @author sunzhk
 *
 */
public class CollapseLog implements UncaughtExceptionHandler {

	public static final String TAG = "CollapseLog";
	/**
	 * 单实例
	 */
	private static CollapseLog instance;
	/**
	 * 系统默认处理类
	 */
	private static UncaughtExceptionHandler mDefaultHandler;
	/**
	 * 是否需要自动退出程序
	 */
	private boolean autoExit = true;
	/**
	 * 是否需要自动重启
	 */
	private boolean autoRestart = true;
	/**
	 * 上下文
	 */
	private Context mContext;
	/**
	 * 上传回调
	 */
	private UploadCollapseLog mUploadCallBack;
	/**
	 * 用于存储设备信息与异常信息
	 */
	private HashMap<String, String> infos = new HashMap<String, String>();
	/**
	 * 格式化日期
	 */
	private DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.CHINA);
	/**
	 * 私有构造方法
	 */
	private CollapseLog() {}
	/**
	 * 获取单例
	 */
	public static CollapseLog getInstance(){
		if(instance == null){
			instance = new CollapseLog();
			mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
			Thread.setDefaultUncaughtExceptionHandler(instance);
		}
		return instance;
	}
	/**
	 * 
	 * @param context
	 */
	public CollapseLog init(Context context){
		init(context, true, null);
		return instance;
	}
	/**
	 * 
	 * @param context
	 * @param autoExit
	 */
	public CollapseLog init(Context context, boolean autoExit){
		init(context, autoExit, null);
		return instance;
	}
	/**
	 * 获取上下文和回调
	 * @param context
	 */
	public CollapseLog init(Context context, boolean autoExit, UploadCollapseLog uploadCallBack){
		mContext = context;
		this.autoExit = autoExit;
		mUploadCallBack = uploadCallBack;
		return instance;
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		if(!handleException(ex) && mDefaultHandler != null){
			mDefaultHandler.uncaughtException(thread, ex);
		}else{
//			try {
//				Thread.sleep(3000);
//			} catch (Exception e) {
//				// TODO: handle exception
//				Log.e(TAG, "sleep error : ", e);
//			}
//			android.os.Process.killProcess(android.os.Process.myPid());
//			System.exit(1);
			if(autoExit){
				exit();
			}
		}
	}

	private boolean handleException(Throwable ex){
		if(ex == null){
			return false;
		}
		Log.e(TAG, "捕获到的崩溃异常", new Exception(ex));
		//收集设备参数信息
		collectDeviceInfo(mContext);
		
		new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				Looper.prepare();
				Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
			
		}.start();
		//保存日志文件
		saveCatchInfo2File(ex);
		return true;
	}
	
	public void collectDeviceInfo(Context context){
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
			if(packageInfo != null){
				String versionName = packageInfo.versionName == null ? "null" : packageInfo.versionName;
				String versionCode = String.valueOf(packageInfo.versionCode);
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for(Field field : fields){
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				// TODO: handle exception
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}
	/**
	 * 保存错误信息到文件
	 * @param ex
	 * @return 返回文件名称
	 */
	private void saveCatchInfo2File(Throwable ex){
		StringBuffer sb = new StringBuffer();
		for(Map.Entry<String, String> entry : infos.entrySet()){
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
			sb.append("\n");
		}
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while(cause != null){
			cause.printStackTrace();
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = mDateFormat.format(new Date());
			String fileName = "CrashLog-"+ time + "-" + timestamp + ".log";
			File dir = new File(BaseApplication.getRootCacheDirPath()+"/Crash Log");
			if(!dir.exists()){
				dir.mkdir();
			}
			String logPath = dir.getAbsolutePath()+"/"+fileName;
			FileOutputStream fos = new FileOutputStream(logPath);
			fos.write(sb.toString().getBytes());
			fos.close();
			if(mUploadCallBack != null){
				mUploadCallBack.upload(logPath);
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, "an error occured while writing file...", e);
		}
	}
	
	public void exit(){
		if(autoRestart){
			setRestart();
		}
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(1);
	}
	
	private void setRestart(){
		AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		PackageManager packageManager = mContext.getPackageManager();
		Intent intent = packageManager.getLaunchIntentForPackage(mContext.getPackageName());
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0x000001, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		alarmManager.set(AlarmManager.RTC_WAKEUP, 1000, pendingIntent);
	}
	/**
	 * 设置是否需要自动重启
	 * @param autoRestart
	 */
	public CollapseLog setAutoRestart(boolean autoRestart){
		this.autoRestart = autoRestart;
		autoExit = autoExit | this.autoRestart;
		return instance;
	}
	
	public interface UploadCollapseLog{
		void upload(String logPath);
	}
	
}
