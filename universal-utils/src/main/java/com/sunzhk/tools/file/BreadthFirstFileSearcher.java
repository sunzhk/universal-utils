package com.sunzhk.tools.file;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by chances on 2017/5/23.
 */

public class BreadthFirstFileSearcher {

	private static final String TAG = "FileSearcher";

	private static final int DEFAULT_THREAD_NUMBER = 5;

	private ExecutorService mThreadPool;

	private File mRootFile;
	private FileFilter mFileFilter;

	private ArrayList<String> mFileList;

	private boolean mSearchFlag = false;

	private int mThreadNum = 0;

	private long startTime;

	public BreadthFirstFileSearcher(@NonNull File root, @NonNull FileFilter filter) {
		checkNull("root file", root);
		checkNull("filter", filter);
		mRootFile = root;
		mFileFilter = filter;
		int processors = Runtime.getRuntime().availableProcessors() * 2;
		if(processors <= 0){
			processors = DEFAULT_THREAD_NUMBER;
		}
		Log.d(TAG, "FileSearcher: processor size is "+processors);
		mThreadPool = Executors.newFixedThreadPool(processors);
		mFileList = new ArrayList<>();
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
			mThreadPool.execute(new SearchRunner(mRootFile));
		}

	}

	private void checkNull(String name, Object obj){
		if(obj == null){
			throw new IllegalArgumentException(name + " cannot be null");
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

	class SearchRunner implements Runnable {

		private File rootFile;

		public SearchRunner(File root){
//			Log.d(TAG, "SearchRunner: new runner to search");
			checkNull("root file", root);
			rootFile = root;
			addNewThread();
		}

		@Override
		public void run() {
			File[] files;
			try{
				files = rootFile.listFiles();
			}catch (SecurityException e){
				e.printStackTrace();
				removeThread();
				checkSearchFinished();
				return;
			}
			if(files == null || files.length == 0){
				removeThread();
				checkSearchFinished();
				return;
			}
			try{
				for(File tempFile : files){
					if(!tempFile.exists()){
						continue;
					}
					if(tempFile.isDirectory()){
						mThreadPool.execute(new SearchRunner(tempFile));
					}else if(mFileFilter.filter(tempFile)){
						mFileList.add(tempFile.getAbsolutePath());
						mFileFilter.onAddMore(mFileList.size());
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}

			removeThread();
			checkSearchFinished();
		}
	}

}
