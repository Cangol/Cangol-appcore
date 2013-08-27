package com.cangol.mobile.logging;


public class Log {
	private  static int LEVEL = android.util.Log.VERBOSE;

	private  static boolean FORMAT=false;
	
	public static void setLogLevelFormat(int level,boolean format) {
		LEVEL = level;
		FORMAT=format;
	}
	public static int getLevel() {
		return LEVEL;
	}
	public static boolean isFormat() {
		return FORMAT;
	}
	// VERBOSE log

	public static void v(String msg) {
			formatLog(android.util.Log.VERBOSE, null, msg, null);
	}
	public static void v(String tag, String msg) {
		formatLog(android.util.Log.VERBOSE, tag, msg, null);
	}

	public static void v(String tag, String msg, Throwable t) {
		formatLog(android.util.Log.VERBOSE, tag, msg, t);
	}

	// INFO log
	
	public static void i(String msg) {
		formatLog(android.util.Log.INFO, null, msg, null);
	}
	public static void i(String tag, String msg) {
		formatLog(android.util.Log.INFO, tag, msg, null);
	}

	public static void i(String tag, String msg, Throwable t) {
		formatLog(android.util.Log.INFO, tag, msg, t);
	}
	
	// DEBUG log
	
	public static void d(String msg) {
		formatLog(android.util.Log.DEBUG, null, msg, null);
	}
	public static void d(String tag, String msg) {
		formatLog(android.util.Log.DEBUG, tag, msg, null);
	}

	public static void d(String tag, String msg, Throwable t) {
		formatLog(android.util.Log.DEBUG, tag, msg, t);
	}
	// WARN log
	public static void w(String msg) {
		formatLog(android.util.Log.WARN, null, msg, null);
	}
	
	public static void w(String tag, String msg) {
		formatLog(android.util.Log.WARN, tag, msg, null);
	}

	public static void w(String tag, String msg, Throwable t) {
		formatLog(android.util.Log.WARN, tag, msg, t);
	}

	// ERROR log
	
	public static void e(String msg) {
		formatLog(android.util.Log.ERROR, null, msg, null);
	}
	
	public static void e(String tag, String msg) {
		formatLog(android.util.Log.ERROR, tag, msg, null);
	}

	public static void e(String tag, String msg, Throwable t) {
		formatLog(android.util.Log.ERROR, tag, msg, t);
	}
	
	public static void formatLog(int logLevel,String tag,String msg,Throwable error){
		if (LEVEL > logLevel) return;
		StackTraceElement stackTrace = new Throwable().getStackTrace()[2];
		String classname = stackTrace.getClassName();
		String filename = stackTrace.getFileName();
		String methodname = stackTrace.getMethodName();
		int linenumber = stackTrace.getLineNumber();
		String output = null;;
		if(FORMAT){
			output = String.format("%s.%s(%s:%d)-->%s", classname, methodname,filename, linenumber, msg);
		}else{
			output=msg;
		}
		if(null==tag){
			tag= (filename != null && filename.contains(".java"))?filename.replace(".java", ""):"";
		}
		switch (logLevel) {
		case android.util.Log.VERBOSE:
			if (error == null) {
				android.util.Log.v(tag, output);
			} else {
				android.util.Log.v(tag, output, error);
			}
			break;
		case android.util.Log.DEBUG:
			if ( error == null) {
				android.util.Log.d(tag, output);
			} else {
				android.util.Log.d(tag, output,  error);
			}
			break;
		case android.util.Log.INFO:
			if ( error == null) {
				android.util.Log.i(tag, output);
			} else {
				android.util.Log.i(tag, output,  error);
			}
			break;
		case android.util.Log.WARN:
			if ( error == null) {
				android.util.Log.w(tag, output);
			} else {
				android.util.Log.w(tag, output,  error);
			}
			break;
		case android.util.Log.ERROR:
			if ( error == null) {
				android.util.Log.e(tag, output);
			} else {
				android.util.Log.e(tag, output,  error);
			}
			break;
		default:
			break;
		}
	}
}
