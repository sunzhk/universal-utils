package com.sunzhk.tools.utils;

import java.io.File;

public class FileUtils {

	public long getFileSize(File file){
		if(file == null || !file.exists()){
			return -1;
		}
		if(file.isFile()){
			return file.length();
		}else{
			
		}
		return 0;
	}
	
}
