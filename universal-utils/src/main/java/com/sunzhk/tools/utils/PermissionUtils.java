package com.sunzhk.tools.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by sunzhk on 2016/10/18.
 */

public class PermissionUtils {

	public enum DangerousPermissions {

		//CALENDAR
		READ_CALENDAR(Manifest.permission.READ_CALENDAR),
		WRITE_CALENDAR(Manifest.permission.WRITE_CALENDAR),
		//CAMERA
		CAMERA(Manifest.permission.CAMERA),
		//CONTACTS
		READ_CONTACTS(Manifest.permission.READ_CONTACTS),
		WRITE_CONTACTS(Manifest.permission.WRITE_CONTACTS),
		GET_ACCOUNTS(Manifest.permission.GET_ACCOUNTS),
		//LOCATION
		ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
		ACCESS_COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION),
		//MICROPHONE
		RECORD_AUDIO(Manifest.permission.RECORD_AUDIO),
		//PHONE(Manifest.permission.),
		READ_PHONE_STATE(Manifest.permission.READ_PHONE_STATE),
		CALL_PHONE(Manifest.permission.CALL_PHONE),
		READ_CALL_LOG(Manifest.permission.READ_CALL_LOG),
		WRITE_CALL_LOG(Manifest.permission.WRITE_CALL_LOG),
		ADD_VOICEMAIL(Manifest.permission.ADD_VOICEMAIL),
		USE_SIP(Manifest.permission.USE_SIP),
		PROCESS_OUTGOING_CALLS(Manifest.permission.PROCESS_OUTGOING_CALLS),
		//SENSORS
		BODY_SENSORS(Manifest.permission.BODY_SENSORS),
		//SMS
		SEND_SMS(Manifest.permission.SEND_SMS),
		RECEIVE_SMS(Manifest.permission.RECEIVE_SMS),
		READ_SMS(Manifest.permission.READ_SMS),
		RECEIVE_WAP_PUSH(Manifest.permission.RECEIVE_WAP_PUSH),
		RECEIVE_MMS(Manifest.permission.RECEIVE_MMS),
		//STORAGE
		READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE),
		WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE);

		private String content;

		DangerousPermissions(String content) {
			this.content = content;
		}

		public String getContent() {
			return content;
		}
	}

	public static boolean checkPermission(@NonNull Context context, @NonNull String permission) {
		return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
	}

	/**
	 * 会在{@link Activity#onRequestPermissionsResult(int, String[], int[])}得到请求结果
	 *
	 * @param context     当前Activity
	 * @param permission  需要的权限
	 * @param requestCode 请求码
	 */
	public static void getPermission(@NonNull Activity context, @NonNull DangerousPermissions permission, int requestCode) {
		getPermission(context, permission.getContent(), requestCode);
	}

	/**
	 * 会在{@link Activity#onRequestPermissionsResult(int, String[], int[])}得到请求结果
	 *
	 * @param context     当前Activity
	 * @param permission  需要的权限
	 * @param requestCode 请求码
	 */
	public static void getPermission(@NonNull Activity context, @NonNull String permission, int requestCode) {
		if (Build.VERSION.SDK_INT < 23) {
			return;
		}
		if (checkPermission(context, permission)) {
			ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
		}
	}

	/**
	 * 跳转到小米的应用设置页(权限页需要系统权限)
	 * @param context
	 */
	public static void getXiaoMiALertWindowPermission(@NonNull Context context) {
		Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");

		if ("V5".equals(getProperty())) {
			PackageInfo pInfo = null;
			try {
				pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			} catch (PackageManager.NameNotFoundException e) {
				Log.e("canking", "error");
				e.printStackTrace();
			}
			intent.setClassName("com.miui.securitycenter", "com.miui.securitycenter.permission.AppPermissionsEditor");
			intent.putExtra("extra_package_uid", pInfo.applicationInfo.uid);
		} else {
			//这个才是真的权限页，但是需要系统权限来打开
//			intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.RealAppPermissionsEditorActivity");
			intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
			intent.putExtra("extra_pkgname", context.getPackageName());
		}

		if (isActivityAvailable(context, intent)) {
			if (context instanceof Activity) {
				Activity a = (Activity) context;
				a.startActivityForResult(intent, 2);
			}
		} else {
			Log.e("canking", "Intent is not available!");
		}
	}

	public static String getProperty() {
		String property = "null";
		if (!"Xiaomi".equals(Build.MANUFACTURER)) {
			return property;
		}
		try {
			Class<?> spClazz = Class.forName("android.os.SystemProperties");
			Method method = spClazz.getDeclaredMethod("get", String.class, String.class);
			property = (String) method.invoke(spClazz, "ro.miui.ui.version.name", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return property;
	}

	private static boolean isActivityAvailable(Context cxt, Intent intent) {
		PackageManager pm = cxt.getPackageManager();
		if (pm == null) {
			return false;
		}
		List<ResolveInfo> list = pm.queryIntentActivities(
				intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list != null && list.size() > 0;
	}

}
