package io.onceonly.util;

public class OOAssert {
	
	public static void fatal(String format,Object ...args){
		fatal(false,format,args);
	}
	public static void fatal(boolean cnd,String format,Object ...args){
		assert cnd:String.format(format, args);
	}
	
	public static void warnning(String format,Object ...args){
		throw new RuntimeException(String.format(format, args));
	}
}
