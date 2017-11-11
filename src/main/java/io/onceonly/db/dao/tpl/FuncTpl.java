package io.onceonly.db.dao.tpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.internal.cglib.proxy.Enhancer;
import org.assertj.core.internal.cglib.proxy.MethodInterceptor;
import org.assertj.core.internal.cglib.proxy.MethodProxy;

public abstract class FuncTpl<E> extends Tpl{
	protected E tpl;
	protected List<String> funcs = new ArrayList<>();
	protected List<String> argNames = new ArrayList<>();
	
	@SuppressWarnings("unchecked")
	public FuncTpl(Class<E> tplClass) {
		FuncSetterProxy cglibProxy = new FuncSetterProxy();
        Enhancer enhancer = new Enhancer();  
        enhancer.setSuperclass(tplClass);  
        enhancer.setCallback(cglibProxy);  
        tpl = (E)enhancer.create(); 
	}
	public E count() {
		funcs.add("COUNT");
		return tpl;
	}
	public E max() {
		funcs.add("MAX");
		return tpl;
	}
	public E min() {
		funcs.add("MIN");
		return tpl;
	}
	public E sum() {
		funcs.add("SUM");
		return tpl;
	}
	public E avg() {
		funcs.add("AVG");
		return tpl;
	}
	class FuncSetterProxy implements MethodInterceptor {  
	    @Override  
	    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
	        if(method.getName().startsWith("set") && args.length == 1) {
	            if(method.getName().length() > 3) {
	            	String fieldName = method.getName().substring(3,4).toLowerCase() +method.getName().substring(4);
	            	argNames.add(fieldName);
	            }
	        }
	        return o;  
	    }  
	}
}
