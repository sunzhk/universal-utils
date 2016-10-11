package com.sunzhk.tools;
/**
 * 未经过测试
 * @author sunzhk
 *
 */
public class BaseThread extends Thread {

	private final String CONTORL = "";

	private Object contorlObject;
	
	public synchronized void pause(){
		pause(CONTORL);
	}
	
	public synchronized void wakeUp(){
		if(contorlObject != null){
			try {
				contorlObject.notifyAll();
			} catch (Exception e) {
				// TODO: handle exception
			}
			contorlObject = null;
		}
	}
	
	public synchronized void pause(Object contorlObject){
		if(contorlObject == null){
			return;
		}
		this.contorlObject = contorlObject;
		synchronized (contorlObject) {
			try {
				contorlObject.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
