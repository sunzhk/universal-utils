package com.sunzhk.tools.utils;

import android.graphics.Color;

public class ColorUtils {

	/**
	 * Gradient = A + (B-A) * N / Step
	 * @param startColor
	 * @param endColor
	 * @param outColors
	 */
	public static void getGraduallyChangingColor(int startColor, int endColor, int[] outColors){
		if(outColors == null || outColors.length <= 0){
			throw new RuntimeException("outColors cannot be null and length must > 0");
		}
		int startRed = Color.red(startColor);
		int startGreen = Color.green(startColor);
		int startBlue = Color.blue(startColor);
		
		int endRed = Color.red(endColor);
		int endGreen = Color.green(endColor);
		int endBlue = Color.blue(endColor);

		for(int i = 0; i<outColors.length; i++){
			outColors[i] = Color.rgb(startRed + (startRed - endRed) * (outColors.length + 1) / (i + 1), 
									 startGreen + (startGreen - endGreen) * (outColors.length + 1) / (i + 1), 
									 startBlue + (startBlue - endBlue) * (outColors.length + 1) / (i + 1));
		}
	}
	
}
