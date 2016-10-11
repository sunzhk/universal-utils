package com.sunzhk.tools;

import android.view.KeyEvent;

import java.util.Timer;
import java.util.TimerTask;

public class BaseLauncherActivity extends BaseActivity {
	
	/**
	 * 显示的时间
	 */
	private int stayTime = 4000;
	
	private boolean canExit = false;
	
	private Timer mTimer;
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(stayTime <= 0){
			return;
		}
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				onTimeOut();
			}
		}, stayTime);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mTimer.cancel();
		mTimer.purge();
	}
	
	protected void onTimeOut() {}
	/**
	 * 自动跳转延迟时间，单位毫秒。如果<=0则不自动跳转<br/>默认为4秒
	 * @param stayTime
	 */
	protected void setStayTime(int stayTime) {
		this.stayTime = stayTime;
	}
	/**
	 * 能否返回键退出
	 * @param canExit
	 */
	protected void setCanExit(boolean canExit) {
		this.canExit = canExit;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(canExit){
				mTimer.cancel();
				mTimer.purge();
				this.finish();
			}else{
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
