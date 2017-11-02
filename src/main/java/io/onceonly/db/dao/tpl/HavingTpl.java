package io.onceonly.db.dao.tpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.internal.cglib.proxy.Enhancer;
import org.assertj.core.internal.cglib.proxy.MethodInterceptor;
import org.assertj.core.internal.cglib.proxy.MethodProxy;

public class HavingTpl<E> extends Tpl{
	public static final byte COUNT_BYTE = 2;
	public static final short COUNT_SHORT = 2;
	public static final int COUNT_INT = 2;
	public static final long COUNT_LONG = 2L;
	public static final float COUNT_FLOAT = 2F;
	public static final double COUNT_DOUBLE = 2D;
	public static final char COUNT_CHAR = '2';
	public static final String COUNT_STR = "2";
	
	public static final int SUM_INT = 2;
	public static final long SUM_LONG = 2;
	
	public static final long AVG_DECIMAL = 3;
	public static final long AVG_LONG = 3;
	public static final long AVG_INT = 3;
	private String func="";
	private String havingExp;
	private List<Object> args = new ArrayList<>();
	private SqlOpt opt;
	private List<SqlOpt> opts = new ArrayList<>();
	private E tpl;
	
	public HavingTpl(Class<E> tplClass) {
		HavingSetterProxy cglibProxy = new HavingSetterProxy();
        Enhancer enhancer = new Enhancer();  
        enhancer.setSuperclass(tplClass);  
        enhancer.setCallback(cglibProxy);  
        tpl = (E)enhancer.create(); 
	}
	
	public E eq() {
		return tpl;
	}
	public E ne() {
		return tpl;
	}
	public E lt() {
		return tpl;
	}
	public E le() {
		return tpl;
	}
	public E gt() {
		return tpl;
	}
	public E ge() {
		return tpl;
	}
	

	public HavingTpl<E> count() {
		func= "COUNT";
		return this;
	}
	public HavingTpl<E> max() {
		func= "MAX";
		return this;
	}
	public HavingTpl<E> min() {
		func= "MIN";
		return this;
	}
	public HavingTpl<E> sum() {
		func= "SUM";
		return this;
	}
	public HavingTpl<E> avg() {
		func= "AVG";
		return this;
	}
	public HavingTpl<E> and() {
		return this;
	}
	
	public HavingTpl<E> or() {
		return this;
	}
	public HavingTpl<E> not() {
		return this;
	}
	
	public HavingTpl<E> args(Object... args) {
		return this;
	}

	class HavingSetterProxy implements MethodInterceptor {  
	    @Override  
	    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
	        if(method.getName().startsWith("set") && args.length == 1) {
	            if(method.getName().length() > 3) {
	            	String fieldName = method.getName().substring(3,4).toLowerCase() +method.getName().substring(4);
		            Object arg = args[0];
		            String.format("%s(%s) %s ?", func,fieldName,opt);
		            if(arg != null && (arg.equals(SelectTpl.DISTINCT_BYTE) 
		            		|| arg.equals(SelectTpl.DISTINCT_INT)
		            		)) {
		            	//TODO
		            }else {
		            }
	            }
	        }
	        return o;  
	    }  
	}	
}
