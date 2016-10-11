package com.sunzhk.tools.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 用于运行命令行，目前只适合运行单次命令。有待完善
 * Created by sunzhk on 2016/10/1.
 */
public class CmdUtils {

	private CmdThread mCmdThread;
	private CmdListener mCmdListener;
	private Process currentProcess;

	public CmdUtils(CmdListener cmdListener){
		mCmdListener = cmdListener;
	}
	/**
	 * 运行单条命令
	 * @param command
	 * @return
	 */
	public boolean exec(String command){
		try {
			currentProcess = Runtime.getRuntime().exec(command);
			if(mCmdThread == null){
				mCmdThread = new CmdThread();
				mCmdThread.start();
			}else{
				mCmdThread.mNotify();
				mCmdThread.nextRead();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 运行多条命令(未经测试)
	 * @param command
	 * @return
	 */
	public boolean exec(String... command){
		try {
			currentProcess = Runtime.getRuntime().exec(command);
			if(mCmdThread == null){
				mCmdThread = new CmdThread();
				mCmdThread.start();
			}else{
				mCmdThread.nextRead();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 并不能停止正在执行的命令，只能停止读取日志
	 */
	public void stop(){
		mCmdThread.stopRead();
	}
	/**
	 * 关闭读取线程，会在再次调用exce是创建新的读取线程
	 */
	public void close(){
		mCmdThread.close();
		if(mCmdThread.isWaiting){
			mCmdThread.mNotify();
		}
		mCmdThread = null;
	}
	/**
	 * 并不是执行线程，而是读取执行后的结果的线程。命令行会在单独的进程中被执行
	 * @author sunzhk
	 *
	 */
	class CmdThread extends Thread{

		private boolean runFlag = true;
		/**
		 * 0:线程休眠 1:正常运行 2:运行下一条
		 */
		private int readFlag = 1;
		private boolean isWaiting = false;
		private Object waitFlag;
		public CmdThread(){
			waitFlag = new Object();
		}

		@Override
		public void run() {
			while(runFlag){
				if(mCmdListener != null){
					mCmdListener.onStart();
				}
				preparRead();
				readCmd();
				if(mCmdListener != null){
					mCmdListener.onStop();
				}
				if(readFlag == 0){
					mWait();
				}
			}
		}
		/**
		 * 从流中读取日志
		 */
		public void readCmd(){
			BufferedReader logReader = new BufferedReader(new InputStreamReader(currentProcess.getInputStream()));
			String buffer;
			try {
				while(readFlag == 1 && (buffer = logReader.readLine()) != null){

					if(mCmdListener != null){
						mCmdListener.onResult(buffer);
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
				mCmdListener.onResult("Read Result Error");
			} finally {

				if(readFlag == 1){
					readFlag = 0;
				}

			}
		}
		public void preparRead(){
			readFlag = 1;
		}
		public void stopRead(){
			readFlag = 0;
		}
		public void nextRead(){
			readFlag = 2;
		}
		/**
		 * 关闭这个线程(非即时)
		 */
		public void close(){
			readFlag = 2;
			runFlag = false;
		}
		/**
		 * 休眠线程
		 */
		public void mWait(){
			try {
				synchronized (waitFlag){
					isWaiting = true;
					waitFlag.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		/**
		 * 唤醒线程
		 */
		public void mNotify(){
			try {
				synchronized (waitFlag){
					waitFlag.notify();
					isWaiting = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			preparRead();
		}
		
		public boolean isWaiting(){
			return isWaiting;
		}
	}
	/**
	 * 执行回调线程，返回执行状态与结果
	 * @author sunzhk
	 *
	 */
	public interface CmdListener {

		void onStart();
		void onResult(String log);
		void onStop();

	}

}
