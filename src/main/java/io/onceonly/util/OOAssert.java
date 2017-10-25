package io.onceonly.util;

public class OOAssert {
	
	public static void fatal(String format,Object ...args){
		fatal(false,format,args);
	}
	public static void fatal(boolean cnd,String format,Object ...args){
		assert cnd:String.format(format, args);
	}
	public static void warnning(boolean cnd,String format,Object ...args){
		assert cnd:String.format(format, args);
	}
	public static void warnning(String format,Object ...args){
		warnning(false,format,args);
	}
}
