package com.sunzhk.tools.file;

import com.sunzhk.tools.utils.MultiThreadTraversal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 文件工具类
 */
public class FileUtils {

	/**
	 * 大文件的MD5处理会比较耗时，建议在子线程中进行
	 * @param inputFile
	 * @return
	 */
	public static String largeFileToMD5(String inputFile) {
		// 缓冲区大小（这个可以抽出一个参数）
		int bufferSize = 256 * 1024;
		FileInputStream fileInputStream = null;
		DigestInputStream digestInputStream = null;
		try {
			// 拿到一个MD5转换器（同样，这里可以换成SHA1）
			MessageDigest messageDigest =MessageDigest.getInstance("MD5");
			// 使用DigestInputStream
			fileInputStream = new FileInputStream(inputFile);
			digestInputStream = new DigestInputStream(fileInputStream,messageDigest);
			// read的过程中进行MD5处理，直到读完文件
			byte[] buffer =new byte[bufferSize];
			while (digestInputStream.read(buffer) > 0);
			// 获取最终的MessageDigest
			messageDigest= digestInputStream.getMessageDigest();
			// 拿到结果，也是字节数组，包含16个元素
			byte[] resultByteArray = messageDigest.digest();
			// 同样，把字节数组转换成字符串
			return convertHashToString(resultByteArray);
		} catch (Exception e) {
			return null;
		} finally {
			try {
				digestInputStream.close();
			} catch (Exception e) {
			}
			try {
				fileInputStream.close();
			} catch (Exception e) {
			}
		}
	}

	public static void copyDirectionary(String source, String target, OnCopyResult result){
		if(source == null || target == null || source.trim().equals("") || target.trim().equals("")){
			return;
		}
		copyDirectionary(new File(source), new File(target), result);
	}

	public static void copyDirectionary(File source, File target, OnCopyResult result){
		if(source == null || target == null){
			return;
		}
		if(!source.exists() || !source.canRead()){
			return;
		}

		try {
			FileChannel sourceChannel = new FileInputStream(source).getChannel();
			FileChannel targetChannel = new FileInputStream(target).getChannel();
			targetChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void copyFile(File source, File target, OnCopyResult result){
		if(source == null || target == null){
			return;
		}
		if(!source.exists() || !source.canRead()){
			return;
		}
		if(source.isDirectory()){
			return;
		}
		if(!target.exists()){
			try {
				target.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				result.onResult(false);
			}
		}
		try {
			FileChannel sourceChannel = new FileInputStream(source).getChannel();
			FileChannel targetChannel = new FileInputStream(target).getChannel();
			targetChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public interface OnCopyResult {
		void onResult(boolean result);
	}

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

	/**
	 * Convert the hash bytes to hex digits string
	 * @param hashBytes
	 * @return The converted hex digits string
	 */
	private static String convertHashToString(byte[] hashBytes) {
		String returnVal = "";
		for (int i = 0; i < hashBytes.length; i++) {
			returnVal += Integer.toString(( hashBytes[i] & 0xff) + 0x100, 16).substring(1);
		}
		return returnVal.toLowerCase();
	}

	public interface OnAnalyzeDirectorySize{
		/**
		 *
		 * @param size in bytes
		 */
		void onResult(long size);
	}
	
}
