package com.sunzhk.tools.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemAlarmUtils {
	
	/**
	 * 设置重复的系统闹钟
	 * @param context 上下文;
	 * @param intent 需要启动的intent
	 * @param startTime 启动时间-使用System.currentTimeMillis()以立即启动
	 * @param repeatTime 两次闹钟的时间间隔
	 */
	public static void setAlarm(Context context, Intent intent, long startTime, long repeatTime){
		PendingIntent sender = PendingIntent.getService(context, 0, intent, 0);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, startTime, repeatTime, sender);
	}
	
	/**
	 * 取消系统闹钟
	 * @param context 上下文
	 * @param intent 设置闹钟时的intent
	 */
	public static void cancelAlarm(Context context, Intent intent){
		PendingIntent sender = PendingIntent.getService(context, 0, intent, 0);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(sender);
	}
	
	/**
	 * 获取下一次整点的时间
	 * @return 下一个整点的时间
	 */
	@SuppressLint("SimpleDateFormat")
	public static long getNextIntegralPoint(){
		SimpleDateFormat time = new SimpleDateFormat("mm");
		Date now = new Date(System.currentTimeMillis());
		int minute = Integer.parseInt(time.format(now));
		time = new SimpleDateFormat("ss");
		int second = Integer.parseInt(time.format(now));
		long add = ((60-minute)*60-second)*1000;
		return now.getTime()+add;
	}
	
}
