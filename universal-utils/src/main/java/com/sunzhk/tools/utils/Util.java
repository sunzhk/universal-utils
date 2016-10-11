package com.sunzhk.tools.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class Util{
	
	/**
	 * 日期时间格式
	 */
	@SuppressLint("SimpleDateFormat")
	public static final SimpleDateFormat DEFAULT_DATEFORMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * Gson 实例
	 */
	public static final Gson GSON = new GsonBuilder().serializeNulls().create();
	
	/**
	 * 开关硬件加速
	 * @param view 需要开关硬件加速的View
	 * @param isHardwareAccelerated 开/关
	 */
	public static void setHardwareAccelerated(View view, boolean isHardwareAccelerated){
		
		if(view == null || !(view instanceof View)){
			return;
		}
		
		try {
			Field mAttachInfo = View.class.getDeclaredField("mAttachInfo");
			mAttachInfo.setAccessible(true);
			Object attachInfo = mAttachInfo.get(view);
			Field mHardwareAccelerated = attachInfo.getClass().getDeclaredField("mHardwareAccelerated");
			mHardwareAccelerated.setAccessible(true);
			mHardwareAccelerated.setBoolean(attachInfo, isHardwareAccelerated);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 首字母小写
	 * @param string
	 * @return
	 */
	public static String toLowerCase(String string){
		char[] chars = string.toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return String.valueOf(chars);
	}
	/**
	 * 首字母大写
	 * @param string
	 * @return
	 */
	public static String toUpperCase(String string){
		char[] chars = string.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return String.valueOf(chars);
	}

	/**
	 * 把C# 中DateTime的json转换成Date
	 * 
	 * @param dateTime
	 * @return 与dateTimeJson对应的Date
	 */
	public static Date decodeDateFromCSharp(String dateTime){
		if (TextUtils.isEmpty(dateTime)) return null;
		if (dateTime.contains("Date")){
			BigDecimal d = getNumber(dateTime.replace("/Date(", "").replace(")/", ""), BigDecimal.ONE.negate());
			if (d.signum() <= 0) return null;
			return new Date(d.longValue());
		}else{
			try{
				return DEFAULT_DATEFORMT.parse(dateTime);
			}catch(Exception e){
				return null;
			}
		}
	}
	/**
	 * 把Calendar转化成Date
	 * 
	 * @param calendar
	 * @return
	 */
	public static Date calendarToDate(Calendar calendar){
		Calendar.getInstance();
		
		return calendar == null ? null : calendar.getTime();
	}
	/**
	 * 把Date转化成Calendar
	 * @param date
	 * @return
	 */
	public static Calendar dateToCalendar(Date date){
		if (date == null) return null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
	/**
	 * 把 calendar 转换成 字符串
	 * 
	 * @param calendar
	 * @return
	 */
	public static String calendar2S(Calendar calendar){
		return calendar == null ? "" : getDateString(calendar.getTime(), DEFAULT_DATEFORMT);
	}

	
	public static String getDateStringFromCSharp(String date, SimpleDateFormat dateFormat){
		return getDateString(decodeDateFromCSharp(date), dateFormat);
	}
	public static String getDateStringFromCSharp(String date){
		return getDateString(decodeDateFromCSharp(date), DEFAULT_DATEFORMT);
	}
	
	public static String getDateString(Date date, SimpleDateFormat dateFormat){
		if(date == null || dateFormat == null){
			return "";
		}
		return dateFormat.format(date);
	}
	/**
	 * 把 date 转换成 字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String date2S(Date date){
		return calendar2S(dateToCalendar(date));
	}
	/**
	 * 把 date 转换成 字符串
	 * 
	 * @param date
	 * @param formt
	 *            格式
	 * @return
	 */
//	public static String date2S(Date date, String formt){
//		return calendar2S(dateToCalendar(date), formt);
//	}
	/**
	 * 返回当前时间 (格式 yyyy-MM-dd HH:mm:ss)
	 * 
	 * @return
	 */
	public static String now(){
		return calendar2S(Calendar.getInstance());
	}
	
	public static String now(String format){
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
		return dateFormat.format(new Date(System.currentTimeMillis()));
	}
	
	/**
	 * 判断字符串ip是否是一个正确的IP地址
	 * 
	 * @param ip
	 * @return 如果参数ip是一个正确的IP则返回true,否则返回false.
	 */
	public static boolean isIp(String ip){
		return Pattern.matches("^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$", ip);
	}
	/**
	 * 判断字符串phoneNumber是否是一个正确的手机号码
	 * @param phoneNumber
	 * @return 如果参数phoneNumber是一个正确的手机号码则返回true,否则返回false.
	 */
	public static boolean isPhoneNumber(String phoneNumber){
		return Pattern.matches("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$", phoneNumber);
	}
	/**
	 * @param context
	 * @return 返回本机的MAC地址ַ
	 */
	public static String getMac(Context context){
		WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		return wifi.getConnectionInfo().getMacAddress();
	}
	/**
	 * 返回本机的IP地址ַ
	 */
	public static String getIp(Context context){
		WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		int ipInt = info.getIpAddress();
		return (ipInt & 0xFF) + "." + ((ipInt >> 8) & 0xFF) + "." + ((ipInt >> 16) & 0xFF) + "." + (ipInt >> 24 & 0xFF);
	}
	/**
	 * 判断WIFI是否可用
	 */
	public static boolean isWifiEnable(Context context){
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.isWifiEnabled();
	}
	/**
	 * 获取当前版本号
	 * 
	 * @param context
	 * @return 当前版本号
	 */
	public static int getCurrentVersionCode(Context context){
		try{
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		}catch(Exception e){
			System.out.println("获取当前版本的版本号出错：" + e);
			return 0;
		}
	}
	/**
	 * 获取当前版本名称
	 * 
	 * @param context
	 * @return 当前版本名
	 */
	public static String getCurrentVersionName(Context context){
		try{
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		}catch(Exception e){
			System.out.println("获取当前版本的版本名称出错：" + e);
			return "";
		}
	}
	/**
	 * 钱的转换
	 * 
	 * @param money
	 *            以元为单位
	 * @return 以元为单位
	 */
	public static String getMoneyB2S(BigDecimal money){
		return money.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
	}
	/**
	 * 钱的转换
	 * 
	 * @param money
	 *            以元为单位
	 * @return 以元为单位
	 */
	public static BigDecimal getMoneyS2B(String money){
		return getNumber(money, BigDecimal.ZERO);
	}
	/**
	 * 把对象转成数字
	 * 
	 * @param object
	 *            对象
	 * @param defaultValue
	 *            默认值
	 * @return object对应的值； 如果转化出错会返回defaultValue，所以需要保存正确情况下是不可能出现defaultValue
	 */
	public static BigDecimal getNumber(Object object, BigDecimal defaultValue){
		if (object == null) return defaultValue;
		String str = object.toString().trim();
		if (TextUtils.isEmpty(str)) return defaultValue;
		try{
			return new BigDecimal(str);
		}catch(Exception e){
			return defaultValue;
		}
	}
	/**
	 * 把对象转成数字
	 * 
	 * @param object
	 *            对象
	 * @param defaultValue
	 *            默认值
	 * @return object对应的值； 如果转化出错会返回defaultValue，所以需要保存正确情况下是不可能出现defaultValue
	 */
	public static int getNumber(Object object, int defaultValue){
		return getNumber(object, new BigDecimal(defaultValue)).intValue();
	}
	/**
	 * 把对象转成数字
	 * 
	 * @param object
	 *            对象
	 * @param defaultValue
	 *            默认值
	 * @return object对应的值； 如果转化出错会返回defaultValue，所以需要保存正确情况下是不可能出现defaultValue
	 */
	public static double getNumber(Object object, double defaultValue){
		return getNumber(object, new BigDecimal(defaultValue)).doubleValue();
	}
	/**
	 * 把对象转成数字
	 * 
	 * @param object
	 *            对象
	 * @param defaultValue
	 *            默认值
	 * @return object对应的值； 如果转化出错会返回defaultValue，所以需要保存正确情况下是不可能出现defaultValue
	 */
	public static long getNumber(Object object, long defaultValue){
		return getNumber(object, new BigDecimal(defaultValue)).longValue();
	}
	/**
	 * 获得内容
	 * 
	 * @return
	 */
	public static String getContent(TextView et){
		if (et == null) return "";
		return Util.trim(et.getText().toString());
	}
	/**
	 * 获得内容
	 * 
	 * @return
	 */
	public static int getContent(TextView et, int defualt){
		if (et == null) return defualt;
		return getNumber(getContent(et), defualt);
	}
	/**
	 * 获得内容
	 * 
	 * @return
	 */
	public static double getContent(TextView et, double defualt){
		if (et == null) return defualt;
		return getNumber(getContent(et), defualt);
	}
	/**
	 * 获得内容
	 * 
	 * @return
	 */
	public static long getContent(TextView et, long defualt){
		if (et == null) return defualt;
		return getNumber(getContent(et), defualt);
	}
	/**
	 * 获得内容
	 * 
	 * @return
	 */
	public static BigDecimal getContent(TextView et, BigDecimal defualt){
		if (et == null) return defualt;
		return getNumber(getContent(et), defualt);
	}
	/**
	 * 把字符串生成二维码
	 * 
	 * @param str
	 * @return null转换失败
	 */
//	public static Bitmap create2DCode(String str){
//		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
//		BitMatrix matrix;
//		try{
//			matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 300, 300);
//			int width = matrix.getWidth();
//			int height = matrix.getHeight();
//			// 二维矩阵转为一维像素数组,也就是一直横着排了
//			int[] pixels = new int[width * height];
//			for(int y = 0; y < height; y++){
//				for(int x = 0; x < width; x++){
//					if (matrix.get(x, y)){
//						pixels[y * width + x] = 0xff000000;
//					}
//				}
//			}
//			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//			// 通过像素数组生成bitmap,具体参考api
//			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//			return bitmap;
//		}catch(WriterException e){
//			System.out.println("转换二维码异常：" + e);
//			return null;
//		}
//	}
	/**
	 * 用GZip方式压缩数据
	 * 
	 * @param data
	 *            压缩前数据
	 * @return 压缩后的数据
	 */
	public static byte[] GZipCompress(byte[] data){
		if (data == null) return new byte[0];
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			GZIPOutputStream gos = new GZIPOutputStream(baos);
			gos.write(data);
			gos.finish();
			gos.close();
			return baos.toByteArray();
		}catch(Exception e){
			System.out.println("GZip压缩异常：" + e);
			return new byte[0];
		}
	}
	/**
	 * 用GZip方式解压数据
	 * 
	 * @param data
	 *            解压前数据
	 * @return 解压后数据
	 */
	public static byte[] GZipDecompress(byte[] data){
		if (data == null) return new byte[0];
		try{
			GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(data));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int count;
			byte dataTemp[] = new byte[1024];
			while((count = gis.read(dataTemp)) != -1){
				baos.write(dataTemp, 0, count);
			}
			baos.flush();
			byte[] output = baos.toByteArray();
			baos.close();
			gis.close();
			return output;
		}catch(Exception e){
			System.out.println("GZip解压异常：" + e);
			return new byte[0];
		}
	}
	/**
	 * 获得空白串
	 * 
	 * @param size
	 *            空白的长度
	 * @return 空白串
	 */
	public static String getBlank(int size){
		String str = "";
		for(int i = 0; i < size; i++){
			str += " ";
		}
		return str;
	}

	/**
	 * 去掉字符串的前后空格
	 * 
	 * @param str
	 *            原字符串
	 * @return 去掉前后空格后的字符串
	 */
	public static String trim(String str){
		return str == null ? null : str.trim();
	}
	/**
	 * 回收 imageView 资源
	 * @param imageView
	 */
	public static void releaseImageViewResouce(ImageView imageView){
		if (imageView == null) return;
		Drawable drawable = imageView.getDrawable();
		if (drawable != null && drawable instanceof BitmapDrawable){
			BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
			Bitmap bitmap = bitmapDrawable.getBitmap();
			if (bitmap != null){
				bitmap.recycle();
				bitmap=null;
			}
			drawable.setCallback(null);
		}
		imageView.setImageBitmap(null);        
	}
	
	
	
	
	
	/**
	 * 获取像素密度
	 * 
	 * @param activity
	 * @return 像素密度
	 */
	public static int getDensity(Context context){
		float scale = context.getResources().getDisplayMetrics().density;
		return (int)(scale * 160);
	}
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dpToPx(Context context, float dpValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dpValue * scale + 0.5f);
	}
	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int pxToDp(Context context, float pxValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(pxValue / scale + 0.5f);
	}
	/**
	 * 隐藏软键盘
	 * 
	 * @param et 会弹出软键盘的输入框
	 */
	public static void hideSoftInput(EditText et) {
		if (et == null){
			return;
		}
		InputMethodManager imm = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}
	/**
	 * 隐藏软键盘
	 * 
	 * @param context
	 */
	public static void hideSoftInput(Context context){
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm.isActive()){
		    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	/**
	 * 显示软键盘
	 * 
	 * @param et
	 */
	public static void showSoftInput(EditText et) {
		InputMethodManager imm = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(et, InputMethodManager.SHOW_FORCED);
	}
	/**
	 * edittext丢失焦点时收起软键盘，会覆盖当前OnFocusChangeListener
	 * @param editText
	 */
	public static void autoHideSoftInput(EditText editText){
		if(editText == null){
			return;
		}
//		final OnFocusChangeListener focusChangeListener = editText.getOnFocusChangeListener();
		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
//				if(focusChangeListener != null && focusChangeListener != this){
//					focusChangeListener.onFocusChange(v, hasFocus);
//				}
				if(hasFocus == false){
					hideSoftInput((EditText) v);
				}
			}
		});
	}
	/**
	 * 将json转换为Object，失败则返回null
	 * @param json
	 * @param classOfT
	 * @return
	 */
	public static <E> E fromJson(String json, Class<E> classOfT){
		E result = null;
		try {
			result = GSON.fromJson(json, classOfT);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}
	/**
	 * 获取一个ViewGroup的全部子View
	 * @param container
	 * @param childs
	 * @return
	 */
	public static List<View> getAllChild(ViewGroup container, List<View> childs){
		
		ArrayList<ViewGroup> groups = new ArrayList<ViewGroup>();
		groups.add(container);
		ViewGroup tempGroup;
		View tempView;
		while(groups.size() > 0){
			tempGroup = groups.get(0);
			for(int i = 0,l = tempGroup.getChildCount();i<l;i++){
				tempView = tempGroup.getChildAt(i);
				childs.add(tempView);
				if(tempView instanceof ViewGroup){
					groups.add((ViewGroup) tempView);
				}
			}
			groups.remove(0);
		}
		return childs;
	}
	
}
