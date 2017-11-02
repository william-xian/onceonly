package io.onceonly.db.dao.tpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.internal.cglib.proxy.Enhancer;
import org.assertj.core.internal.cglib.proxy.MethodInterceptor;
import org.assertj.core.internal.cglib.proxy.MethodProxy;

import io.onceonly.db.dao.tpl.OrderTpl.OrderSetterProxy;

public class SelectTpl<E> extends HavingTpl<E>{
	public static final byte DISTINCT_BYTE = 3;
	public static final int DISTINCT_INT = 3;
	public static final long DISTINCT_LONG = 3;
	public static final char DISTINCT_C = '3';
	public static final String DISTINCT_S = "3";

	public static final String CONCAT_AS_STR = "S";
	public static final String CONCAT_AS_ARR = "A";
	private E tpl;
	public SelectTpl(Class<E> tplClass) {
		super(tplClass);
		SelectSetterProxy cglibProxy = new SelectSetterProxy();
        Enhancer enhancer = new Enhancer();  
        enhancer.setSuperclass(tplClass);  
        enhancer.setCallback(cglibProxy);  
        tpl = (E)enhancer.create(); 
	}
	
	private List<String> columns  = new ArrayList<>();
	
	class SelectSetterProxy implements MethodInterceptor {  
	    @Override  
	    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
	        if(method.getName().startsWith("set") && args.length == 1) {
	            if(method.getName().length() > 3) {
	            	String fieldName = method.getName().substring(3,4).toLowerCase() +method.getName().substring(4);
		            Object arg = args[0];
		            if(arg != null && (arg.equals(SelectTpl.DISTINCT_BYTE) 
		            		|| arg.equals(SelectTpl.DISTINCT_INT)
		            		|| arg.equals(SelectTpl.DISTINCT_LONG)
		            		|| arg.equals(SelectTpl.DISTINCT_C)
		            		|| arg.equals(SelectTpl.DISTINCT_S)
		            		)) {
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
