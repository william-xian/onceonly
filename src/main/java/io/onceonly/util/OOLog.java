package io.onceonly.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OOLog {
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static void debug(String format,Object ...args) {
		StackTraceElement curStack =  Thread.currentThread().getStackTrace()[2];
		String location = curStack.getClassName()+ ":" + curStack.getLineNumber();
		String msg = String.format(format, args);
		System.out.printf("%s DEBUG %s  %s",SDF.format(new Date()), location,msg);
	}
	public static void log(String format,Object ...args) {
		StackTraceElement curStack =  Thread.currentThread().getStackTrace()[2];
		String location = curStack.getFileName() + ":" + curStack.getLineNumber();
		String msg = String.format(format, args);
		System.out.printf("%s LOG %s  %s",SDF.format(new Date()), location,msg);
	}

	public static void warnning(String format,Object ...args) {
		StackTraceElement curStack =  Thread.currentThread().getStackTrace()[2];
		String location = curStack.getFileName() + ":" + curStack.getLineNumber();
		String msg = String.format(format, args);
		System.err.printf("%s WARN %s  %s",SDF.format(new Date()), location,msg);
	}

	public static void error(String format,Object ...args) {
		StackTraceElement curStack =  Thread.currentThread().getStackTrace()[2];
		String location = curStack.getFileName() + ":" + curStack.getLineNumber();
		String msg = String.format(format, args);
		System.err.printf("%s error %s  %s",SDF.format(new Date()), location,msg);
	}

	public static void fatal(String format,Object ...args) {
		StackTraceElement curStack =  Thread.currentThread().getStackTrace()[2];
		String location = curStack.getFileName() + ":" + curStack.getLineNumber();
		String msg = String.format(format, args);
		System.err.printf("%s fatal %s  %s",SDF.format(new Date()), location,msg);
	}
}
