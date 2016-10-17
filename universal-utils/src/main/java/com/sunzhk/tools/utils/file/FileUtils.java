package com.sunzhk.tools.utils.file;

import com.sunzhk.tools.utils.MultiThreadTraversal;

import java.io.File;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 文件工具类
 */
public class FileUtils {

	/**
	 * 计算文件夹及其子文件、子文件夹的大小
	 * @param directory 需要计算大小的文件夹。若没有子文件或不是文件夹则返回0
	 * @param callBack 会在主线程里回调
	 */
	public static void getDirectorySize(File directory, final OnAnalyzeDirectorySize callBack){
		if(callBack == null){
			return;
		}
		if(directory.isFile()){
			Observable.just(directory.length())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Action1<Long>() {
						@Override
						public void call(Long aLong) {
							callBack.onResult(aLong);
						}
					});
			return;
		}
		if(!directory.isDirectory() || directory.listFiles() == null || directory.listFiles().length == 0){
			Observable.just(0L)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Action1<Long>() {
						@Override
						public void call(Long aLong) {
							callBack.onResult(aLong);
						}
					});
			return;
		}
		new MultiThreadTraversal().exce(directory, new MultiThreadTraversal.AnalyzerFilter<File>() {

			private long size;

			@Override
			public File[] getChild(File rootNode) {
				return rootNode.listFiles();
			}

			@Override
			public boolean hasChild(File rootNode) {
				return rootNode.listFiles() != null && rootNode.listFiles().length >0;
			}

			@Override
			public void process(File node) {
				if(node.length() == 0){
					return;
				}
				addSize(node.length());
			}

			@Override
			public void onFinished() {
				Observable.just(size)
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Action1<Long>() {
							@Override
							public void call(Long aLong) {
								callBack.onResult(aLong);
							}
						});
			}

			private synchronized void addSize(long childSize){
				size += childSize;
			}

		});
	}

	public interface OnAnalyzeDirectorySize{
		/**
		 *
		 * @param size in bytes
		 */
		void onResult(long size);
	}
	
}
