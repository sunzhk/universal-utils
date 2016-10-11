package com.sunzhk.tools.utils;

import android.app.Activity;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class DoubleClickExit {
	
	private static boolean exitFlag = false;
	
	public static void clickExit(Activity activity){
		Timer tExit = null;  
	    if (exitFlag == false) {
	    	exitFlag = true;
	        Toast.makeText(activity, "再次点击返回键退出应用", Toast.LENGTH_SHORT).show();  
	        tExit = new Timer();  
	        tExit.schedule(new TimerTask() {  
	            @Override  
	            public void run() {  
	            	exitFlag = false;
	            }  
	        }, 2000);
	        
	        tExit.schedule(new TimerTask() {  
	            @Override  
	            public void run() {  
	            	exitFlag = false;
	            }  
	        }, 2000);

	    } else {
	    	activity.finish();
	    	android.os.Process.killProcess(android.os.Process.myPid());
	        System.exit(0);
	    }
	}
}
