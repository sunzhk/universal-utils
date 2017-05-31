package com.sunzhk.tools.file;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by chances on 2017/5/27.
 */

public class DepthFirstFileSearcher {

	private static final String TAG = "DFFileSearcher";

	private static final int DEFAULT_THREAD_NUMBER = 5;

	private ExecutorService mThreadPool;
	private int mPoolSize;

	private File mRootFile;
	private FileFilter mFileFilter;

	private Stack<File> mFileStackToFilter;

	private ArrayList<String> mFileList;

	private boolean mSearchFlag = false;

	private int mThreadNum = 0;

	private long startTime;

	public DepthFirstFileSearcher(@NonNull File root, @NonNull FileFilter filter) {
		checkNull("root file", root);
		checkNull("filter", filter);
		mRootFile = root;
		mFileFilter = filter;
		mPoolSize = Runtime.getRuntime().availableProcessors() * 2;
		if(mPoolSize <= 0){
			mPoolSize = DEFAULT_THREAD_NUMBER;
		}
		Log.d(TAG, "BreadthFirstFileSearcher: processor size is "+mPoolSize);
		mThreadPool = Executors.newFixedThreadPool(mPoolSize);
		mFileList = new ArrayList<>();
		mFileStackToFilter = new Stack<>();
	}

	private void checkNull(String name, Object obj){
		if(obj == null){
			throw new IllegalArgumentException(name + " cannot be null");
		}
	}

	public void startSearch(){
		if(mSearchFlag){
			throw new RuntimeException("The searcher is already running");
		}
		mSearchFlag = true;
		startTime = System.currentTimeMillis();
		if(mRootFile.isFile()){
			if(mFileFilter.filter(mRootFile)){
				mFileList.add(mRootFile.getAbsolutePath());
			}
			mFileFilter.filterResult(mFileList);
			Log.d(TAG, "startSearch: root is file");
		}else{
			Log.d(TAG, "startSearch: root is dictionary,start search");
			mFileStackToFilter.push(mRootFile);
			mThreadPool.execute(new SearchRunner());
		}

	}

	private void checkSearchFinished(){
		//if search over，call filter.filterResult
		synchronized ((Integer) mThreadNum){
//			Log.d(TAG, "checkSearchFinished: current size is " + mThreadNum);
			if(mThreadNum > 0){
				return;
			}
		}
		Log.d(TAG, "checkSearchFinished: use time:"+(System.currentTimeMillis() - startTime));
		mFileFilter.filterResult(mFileList);
		mSearchFlag = false;
	}

	private synchronized void addNewThread(){
		mThreadNum++;
	}

	private synchronized void removeThread(){
		mThreadNum--;
	}

	private class SearchRunner implements Runnable {

		public SearchRunner(){
			addNewThread();
		}

		@Override
		public void run() {
			File rootFile;
			out : while(true){
				try {
					rootFile = mFileStackToFilter.pop();
				}catch (EmptyStackException e){
					break;//栈为空，没有需要check的文件了
				}
				File[] files;
				try{
					files = rootFile.listFiles();
				}catch (SecurityException e){
					e.printStackTrace();
					continue out;
				}
				if(files == null || files.length == 0){
					continue out;
				}
				try{
					in : for(File tempFile : files){
						if(!tempFile.exists()){
							continue in;
						}
						if(tempFile.isDirectory()){
							mFileStackToFilter.push(tempFile);
							int size = mFileStackToFilter.size();
							if(size > maxStackSize){
								maxStackSize = size;
								Log.d(TAG, "run: push directory,all :"+mFileStackToFilter.size());
							}
							if(mThreadNum < mPoolSize){
								mThreadPool.execute(new SearchRunner());
							}
						}else if(mFileFilter.filter(tempFile)){
							mFileList.add(tempFile.getAbsolutePath());
							mFileFilter.onAddMore(mFileList.size());
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}

			}
			removeThread();
			checkSearchFinished();
		}
	}

	private transient int maxStackSize;
}
