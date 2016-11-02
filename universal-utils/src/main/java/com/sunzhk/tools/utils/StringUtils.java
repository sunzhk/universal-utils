package com.sunzhk.tools.utils;

import android.text.TextUtils;

/**
 * Created by sunzhk on 2016/10/30.
 */

public class StringUtils {

	public static void main(String[] args) {
		String str1 = "白虎";
		String str2 = "老虎";
		System.out.println("TestSimilar:" + similar(str1, str2));
	}

	public static String add(String in) {
		StringBuilder sb = new StringBuilder(in);
		for (int i = 0; i < in.length(); i++) {
			sb.append("*");
		}
		return sb.toString();
	}


	/**
	 * 计算两个字符串的相似度
	 * @param str1
	 * @param str2
	 * @return 返回0~1
	 */
	public static double similar(String str1,String str2) {
		if(TextUtils.isEmpty(str1) || TextUtils.isEmpty(str2)){
			return 0;
		}
		//计算两个字符串的长度。
		int len1 = str1.length();
		int len2 = str2.length();
		//建立上面说的数组，比字符长度大一个空间
		int[][] dif = new int[len1 + 1][len2 + 1];
		//赋初值，步骤B。
		for (int a = 0; a <= len1; a++) {
			dif[a][0] = a;
		}
		for (int a = 0; a <= len2; a++) {
			dif[0][a] = a;
		}
		//计算两个字符是否一样，计算左上的值
		int temp;
		for (int i = 1; i <= len1; i++) {
			for (int j = 1; j <= len2; j++) {
				if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
					temp = 0;
				} else {
					temp = 1;
				}
				//取三个值中最小的
				dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1,
						dif[i - 1][j] + 1);
			}
		}
		//计算相似度
		double similarity =1d - (double) dif[len1][len2] / Math.max(str1.length(), str2.length());
		return similarity;
	}

	//得到最小值
	private static int min(int... is) {
		int min = Integer.MAX_VALUE;
		for (int i : is) {
			if (min > i) {
				min = i;
			}
		}
		return min;
	}
}
