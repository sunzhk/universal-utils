package com.sunzhk.tools.utils.file;

import com.sunzhk.tools.utils.MultiThreadTraversal;

import java.io.File;

/**
 * 文件工具类
 */
public class FileUtils {

	/**
	 * 计算文件夹及其子文件、子文件夹的大小
	 * @param directory
	 * @param callBack 会在子线程回调
	 */
	public static void getDirectorySize(File directory, final OnAnalyzeDirectorySize callBack){
		if(callBack == null){
			return;
		}
		if(!directory.isDirectory() || directory.listFiles() == null || directory.listFiles().length == 0){
				callBack.onResult(0L);
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
				callBack.onResult(size);
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
