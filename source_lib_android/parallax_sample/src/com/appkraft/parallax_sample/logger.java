package com.appkraft.parallax_sample;



public class logger {
	private static final boolean IS_PRINT_LOG = true;
	
	public static void eSuper(String tag, String info) {
		StackTraceElement[] ste = new Throwable().getStackTrace();
		int i = 1;
		if (IS_PRINT_LOG) {
			StackTraceElement s = ste[i];
			String className = s.getClassName().contains(".") ? s
					.getClassName().substring(
							s.getClassName().lastIndexOf("."),
							s.getClassName().length()) : s.getClassName();
			
			android.util.Log.e(tag, String.format("======[%s][%s][%s]=====%s",
					className, s.getLineNumber(), s.getMethodName(),
					info));
		}
	}
}
