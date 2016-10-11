package com.sunzhk.tools.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

/**
 * 还没写完。目前NotificationCompat可以有效使用，先不写了
 * @author sunzhk
 *
 */
public class SimpleNotification {

	private static final int NOTIFICATION_FLAG = 1;
	
	private Context mContext;
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	
	private int apiLevel = Build.VERSION.SDK_INT;

	private int icon;
	private String tickerText;
	private String title;
	private String text;
	private PendingIntent pendingIntent;
	
	private Bitmap largeIcon;
	
	public SimpleNotification(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	public void build(){
		if(apiLevel < 11){
			buildAfterAPI1();
		}else if(apiLevel < 16){
			buildAfterAPI11();
		}else{
			buildAfterAPI16();
		}
	}
	
	public void show(){
		mNotificationManager.notify(NOTIFICATION_FLAG, mNotification);
	}
	/**
	 * 低版本使用
	 */
	@SuppressWarnings("deprecation")
	private void buildAfterAPI1(){
		// 下面需兼容Android 2.x版本是的处理方式
//		Notification notify1 = new Notification(icon, "TickerText:" + "您有新短消息，请注意查收！", System.currentTimeMillis());
		mNotification = new Notification();
		mNotification.icon = icon;
		mNotification.tickerText = tickerText;
		mNotification.when = System.currentTimeMillis();
//		mNotification.setLatestEventInfo(mContext, title, text, pendingIntent);
		mNotification.number = 1;
		mNotification.largeIcon = largeIcon;
		mNotification.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
	}
	/**
	 * API11及更高
	 */
	@SuppressWarnings("deprecation")
	private void buildAfterAPI11(){
		mNotification = new Notification.Builder(mContext)
				.setSmallIcon(icon) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap icon)
				.setTicker(tickerText)// 设置在status bar上显示的提示文字
				.setContentTitle(title)// 设置在下拉status bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
				.setContentText(text)// TextView中显示的详细内容
				.setContentIntent(pendingIntent) // 关联PendingIntent
				.setNumber(1) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
				.getNotification();
		mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
	}
	/**
	 * API16及更高
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void buildAfterAPI16(){
		mNotification = new Notification.Builder(mContext)
//				.setSmallIcon(R.drawable.message)
				.setTicker("TickerText:" + "您有新短消息，请注意查收！")
				.setContentTitle("Notification Title")
				.setContentText("This is the notification message")
				.setContentIntent(pendingIntent)
				.setNumber(1).build(); // 需要注意build()是在API level16及之后增加的，API11可以使用getNotificatin()来替代
		mNotification.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
	}
	
}
