package io.onceonly.db.dao.tpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.internal.cglib.proxy.Enhancer;
import org.assertj.core.internal.cglib.proxy.MethodInterceptor;
import org.assertj.core.internal.cglib.proxy.MethodProxy;

public class GroupTpl<E> extends Tpl {
	
	private List<String> group = new ArrayList<>();
	
	private E tpl;
	
	@SuppressWarnings("unchecked")
	public GroupTpl(Class<E> tplClass) {
		GroupSetterProxy cglibProxy = new GroupSetterProxy();
        Enhancer enhancer = new Enhancer();  
        enhancer.setSuperclass(tplClass);  
        enhancer.setCallback(cglibProxy);  
        tpl = (E)enhancer.create(); 
	}

	public E use() {
		return tpl;
	}
	
	public String getGroup() {
		return String.join(",", group);
	}

	class GroupSetterProxy implements MethodInterceptor {  
	    @Override  
	    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
	        if(method.getName().startsWith("set") && args.length == 1) {
	            if(method.getName().length() > 3) {
	            	String fieldName = method.getName().substring(3,4).toLowerCase() +method.getName().substring(4);
	            	group.add(fieldName);
	            }
	        }
	        return o;  
	    }  
	}
}
