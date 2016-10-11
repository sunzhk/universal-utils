package com.sunzhk.tools.utils;

/**
 * debug专用包，用于判断并抛出异常(转，但是感觉并没有什么卵用)
 * @author sunzhk
 */
public final class Debug {

	private Debug() {}

	private static StackTraceElement getStackTraceElement(int level) {
		StackTraceElement[] traceElement = (new Exception()).getStackTrace();
		level = Math.min(Math.max(level, 2), traceElement.length - 1);
		return ((new Exception()).getStackTrace())[level];
	}

	public static final int THROW_PLACE = 2;
	public static final int ONE_LEVEL_CALL_PLACE = 3;
	
	public static <T extends Object> T validateNullPointer(T object) {
		// FILE_LINE_FUNC()，获取validateNullPointer()调用点的文件名、行号、方法名信息
		if (object == null) {
			throw new NullPointerException(FILE_LINE_FUNC()
					+ "NullPointerException");
		}
		return object;
	}

	public static String FILE_LINE_FUNC(int level) {
		StackTraceElement traceElement = getStackTraceElement(level);
		return new StringBuffer("[").append("FILE:")
				.append(traceElement.getFileName()).append("|").append("LINE:")
				.append(traceElement.getLineNumber()).append("|")
				.append("FUNC:").append(traceElement.getMethodName())
				.append("]").toString();
	}

	public static String FILE_LINE_FUNC() {
		StackTraceElement traceElement = getStackTraceElement(ONE_LEVEL_CALL_PLACE);
		return new StringBuffer("[").append("FILE:")
				.append(traceElement.getFileName()).append("|").append("LINE:")
				.append(traceElement.getLineNumber()).append("|")
				.append("FUNC:").append(traceElement.getMethodName())
				.append("]").toString();
	}

	public static String CLASS(int level) {
		return getStackTraceElement(level).getClassName();
	}

	public static String CLASS() {
		return getStackTraceElement(ONE_LEVEL_CALL_PLACE).getClassName();
	}

	public static String FILE(int level) {
		return getStackTraceElement(level).getFileName();
	}

	public static String FILE() {
		return getStackTraceElement(ONE_LEVEL_CALL_PLACE).getFileName();
	}

	public static String FUNC(int level) {
		return getStackTraceElement(level).getMethodName();
	}

	public static String FUNC() {
		return getStackTraceElement(ONE_LEVEL_CALL_PLACE).getMethodName();
	}

	public static int LINE(int level) {
		return getStackTraceElement(level).getLineNumber();
	}

	public static int LINE() {
		return getStackTraceElement(ONE_LEVEL_CALL_PLACE).getLineNumber();
	}

	/*public static String TIME() {
		 return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new
		 Date());
	}*/
}
