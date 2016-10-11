package com.sunzhk.tools.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;

/**
 * 摇一摇监听器
 * 自己声明权限。<br>在生命周期的开始和结束调用regist()和unRegist()方法。
 * @author sunzhk
 *
 */
public abstract class BaseShakeListener implements SensorEventListener {

	private SensorManager mSensorManager;
	
	private Vibrator mVibrator;
	
	public BaseShakeListener(Context context) {
		// TODO Auto-generated constructor stub
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		int sensorType = event.sensor.getType();
		//values[0]:X轴，values[1]：Y轴，values[2]：Z轴
		float[] values = event.values;
//		Log.e("event.sensor.Type", sensorType+";"+values[0]+";"+values[1]+";"+values[2]);
		if (sensorType == Sensor.TYPE_ACCELEROMETER){
			//正常情况下，任意轴数值最大就在9.8~10之间，只有在突然摇动手机的时候，瞬时加速度才会突然增大或减少。监听任一轴的加速度大于14(反正我觉得摇一摇没必要太用力把。。)
			if ((Math.abs(values[0]) > 14 || Math.abs(values[1]) > 14 || Math.abs(values[2]) > 14)){
				onShake(sensorType+";"+values[0]+";"+values[1]+";"+values[2]);
				mVibrator.vibrate(800);
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	
	public boolean regist(){
		return mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void unRegist(){
		mSensorManager.unregisterListener(this);
	}
	/**
	 * 摇一摇回调
	 */
	protected abstract void onShake(String msg);

}
