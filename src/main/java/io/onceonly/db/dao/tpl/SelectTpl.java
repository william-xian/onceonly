package io.onceonly.db.dao.tpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.internal.cglib.proxy.MethodInterceptor;
import org.assertj.core.internal.cglib.proxy.MethodProxy;

public class SelectTpl<E> extends HavingTpl<E>{
	public static final boolean SHOW_BOOL = true;
	public static final byte SHOW_BYTE = 1;
	public static final short SHOW_SHORT = 1;
	public static final int SHOW_INT = 1;
	public static final long SHOW_LONG = 1L;
	public static final float SHOW_FLOAT = 1F;
	public static final double SHOW_DOUBLE = 1D;
	public static final char SHOW_CHAR = '1';
	public static final String SHOW_STR = "1";
	
	public static final byte DISTINCT_BYTE = 3;
	public static final int DISTINCT_INT = 3;
	public static final long DISTINCT_LONG = 3;
	public static final char DISTINCT_C = '3';
	public static final String DISTINCT_S = "3";

	public static final String CONCAT_AS_STR = "S";
	public static final String CONCAT_AS_ARR = "A";
	
	private List<String> columns  = new ArrayList<>();
	
	class SelectSetterProxy implements MethodInterceptor {  
	    @Override  
	    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
	        if(method.getName().startsWith("set") && args.length == 1) {
	            if(method.getName().length() > 3) {
	            	String fieldName = method.getName().substring(3,4).toLowerCase() +method.getName().substring(4);
		            Object arg = args[0];
		            if(arg.equals(SelectTpl.DISTINCT_BYTE) 
		            		|| arg.equals(SelectTpl.DISTINCT_INT)
		            		|| arg.equals(SelectTpl.DISTINCT_LONG)
		            		|| arg.equals(SelectTpl.DISTINCT_C)
		            		|| arg.equals(SelectTpl.DISTINCT_S)
		            		) {
		            	columns.add("DISTINCT "+fieldName);
		            }else {
		            	columns.add(fieldName);
		            }
	            }
	        }
	        return o;  
	    }  
	}	
}
