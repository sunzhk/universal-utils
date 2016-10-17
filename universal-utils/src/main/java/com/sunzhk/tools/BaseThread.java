package com.sunzhk.tools;
/**
 * 添加线程休眠与唤起方法
 * @author sunzhk
 *
 */
public class BaseThread extends Thread {

	private final String CONTORLER = "";


	public synchronized void wakeUp(){
		try {
			CONTORLER.notifyAll();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public synchronized void pause(){
		synchronized (CONTORLER) {
			try {
				CONTORLER.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
