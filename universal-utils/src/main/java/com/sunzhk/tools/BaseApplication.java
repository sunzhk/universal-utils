package com.sunzhk.tools;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Environment;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.sunzhk.tools.utils.SoftArrayList;
import com.sunzhk.tools.http.BaseHttpTask;

import java.io.File;
import java.util.HashMap;

public class BaseApplication extends Application {

	protected static Object mApplication;
	/**
	 * 已启动的Activity列表。以软引用数组保存。
	 */
	private static final SoftArrayList<Activity> mActivities = new SoftArrayList<>();
	/**
	 * 全局数据，建议使用枚举来管理
	 */
	private static final HashMap<String, Object> mGlobalData = new HashMap<>();
	/**
	 * 外部存储器状态
	 */
	private static boolean isExternalStorageMounted = false;
	/**
	 * 应用专用数据文件夹
	 */
	private static String rootFilesDirPath;
	/**
	 * 应用专用缓存文件夹
	 */
	private static String rootCacheDirPath;
	/**
	 * 屏幕高度
	 */
	private static int windowHeight;
	/**
	 * 屏幕宽度
	 */
	private static int windowWidth;
	/**
	 * 应用打开次数
	 */
	private static int useTimes;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mApplication = this;
		BaseHttpTask.init(this);
		updateUseTimes();
		initFileState();
		setSDCardStateReceiver();
		initWindowSize();
	}
	
	/**
	 * 获取屏幕大小
	 */
	public void initWindowSize(){
		Point size = new Point();
		((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
		windowWidth = size.x;
		windowHeight = size.y;
	}
	
	/**
	 * 初始化文件环境
	 */
	public void initFileState(){
		if(isExternalStorageMounted = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			rootFilesDirPath = getExternalFilesDir(null).getAbsolutePath();
			rootCacheDirPath = getExternalCacheDir().getAbsolutePath();
		}else{
			rootFilesDirPath = getFilesDir().getAbsolutePath();
			rootCacheDirPath = getCacheDir().getAbsolutePath();
		}
	}
	/**
	 * 设置广播接收者以获取SD卡拔插广播
	 */
	private void setSDCardStateReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_MEDIA_EJECT);
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addDataScheme("file");
		filter.setPriority(1000);
		registerReceiver(new SDCardStateReceiver(), filter);
	}
	/**
	 * 应用打开次数加一；如为第一次使用则调用onFirstOpen
	 */
	private void updateUseTimes(){
		SharedPreferences usePreferences = getSharedPreferences("UseInfo", MODE_PRIVATE);
		useTimes = usePreferences.getInt("UseTimes", 1);
		if(useTimes == 1){
			onFirstOpen();
		}
		useTimes++;
		SharedPreferences.Editor editor = usePreferences.edit();
		editor.putInt("UseTimes", useTimes);
		editor.commit();
	}
	/**
	 * 初始化图片加载器
	 */
	protected void initImageLoader(int threadPoolSize, int threadPriority, int memoryCacheSize, int diskCacheSize, int connectTimeOut, int readTimeOut){
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
//					.memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
					.threadPoolSize(threadPoolSize)// 线程池内加载的数量
					.threadPriority(threadPriority)//线程优先级
					.denyCacheImageMultipleSizesInMemory()
					.memoryCache(new UsingFreqLimitedMemoryCache(memoryCacheSize)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
					.memoryCacheSize(memoryCacheSize)
					.diskCacheSize(diskCacheSize)
					.diskCache(new UnlimitedDiskCache(new File(rootCacheDirPath + "/imagesCache")))
					.diskCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
					.imageDecoder(new BaseImageDecoder(true))
					.tasksProcessingOrder(QueueProcessingType.LIFO)
//					.diskCacheFileCount(Setting.ImageLoader.DISK_CACHE_FILE_COUNT) //缓存的文件数量
					.defaultDisplayImageOptions(new DisplayImageOptions.Builder().cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中  
																				.cacheOnDisk(true)                          // 设置下载的图片是否缓存在SD卡中 
																				.build())
					.imageDownloader(new BaseImageDownloader(this, connectTimeOut, readTimeOut)) // connectTimeout (5 s), readTimeout (30 s)超时时间
					.writeDebugLogs()
					.build();

		ImageLoader.getInstance().init(config);
	}
	/**
	 * 初始化默认的ImageLoader
	 */
	protected void initDefaultImageLoader(){
		initImageLoader(5, Thread.NORM_PRIORITY-2, 2 * 1024 * 1024, 50 * 1024 * 1024, 5 * 1000, 30 * 1000);
	}
	/**
	 * 重写此方法以对第一次使用本应用做出处理
	 */
	protected void onFirstOpen(){}
	
	/**
	 * 获取SD卡挂载状态
	 * @return
	 */
	public static boolean isExternalStorageMounted(){
		return isExternalStorageMounted;
//		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	public static String getRootFilesDirPath(){
		return rootFilesDirPath;
	};

	public static String getRootCacheDirPath(){
		return rootCacheDirPath;
	};
	/**
	 * 获取应用的使用次数，第一次打开返回1，依此。
	 * @return
	 */
	public static int getUseTimes(){
		return useTimes;
	}
	
	/**
	 * 添加一个Activity到列表中，如果已有则会被移到末端
	 * @param activity
	 */
	public static void addActivity(Activity activity){
		if(mActivities.contains(activity)){
			mActivities.remove(activity);
		}
		mActivities.add(activity);
	}
	/**
	 * 从列表中移除一个Activity
	 * @param activity
	 */
	public static void removeActivity(Activity activity){
		mActivities.remove(activity);
	}
	/**
	 * 结束所有的Activity
	 */
	public static void finishAllActivies(){
		for(Activity activity : mActivities){
			activity.finish();
		}
		mActivities.clear();
	}
	/**
	 * 获取最后一个加载的Activity
	 * @return
	 */
	public static Activity getCurrentActivity(){
		if(mActivities.isEmpty()){
			return null;
		}
		return mActivities.get(mActivities.size()-1);
	}
	/**
	 * 添加一个全局数据。如果已有，则更新
	 * @param key
	 * @param value
	 */
	public static void putGlobalData(String key, Object value){
		mGlobalData.put(key, value);
	}
	/**
	 * 获取一个全局数据，如果没有则返回null
	 * @param key
	 * @return
	 */
	public static Object getGlobalData(String key){
		return mGlobalData.get(key);
	}
	
	public static void removeGlobalData(String key){
		mGlobalData.remove(key);
	}
	
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void showToast(String msg){
		if(msg == null){
			msg = "";
		}
		if (mActivities.isEmpty() || getCurrentActivity() == null){
			if(mApplication != null){
				Toast.makeText((BaseApplication)mApplication, msg, Toast.LENGTH_LONG).show();
			}
			return;
		}
		Toast toast = new Toast(mActivities.get(mActivities.size()-1));
		TextView tv = new TextView(mActivities.get(mActivities.size()-1));
		tv.setPadding(60, 30, 60, 30);
		tv.setText(msg + "");
		tv.setTextSize(15);
		GradientDrawable drawable = new GradientDrawable();
		drawable.setColor(0xFFFFFFFF);
		drawable.setCornerRadius(20);
		drawable.setStroke(2, 0xFF000000);
		if(Build.VERSION.SDK_INT < 16){
			tv.setBackgroundDrawable(drawable);
		}else{
			tv.setBackground(drawable);
		}
		tv.setTextColor(Color.BLACK);
		toast.setView(tv);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.show();
	}
	
	public static int getWindowHeight() {
		return windowHeight;
	}

	public static int getWindowWidth() {
		return windowWidth;
	}
	
	class SDCardStateReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals(Intent.ACTION_MEDIA_EJECT) || action.equals(Intent.ACTION_MEDIA_MOUNTED)){
				initFileState();
			}
		}
	}

}
