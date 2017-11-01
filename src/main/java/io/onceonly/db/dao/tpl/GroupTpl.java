package io.onceonly.db.dao.tpl;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.internal.cglib.proxy.Enhancer;
import org.assertj.core.internal.cglib.proxy.MethodInterceptor;
import org.assertj.core.internal.cglib.proxy.MethodProxy;

public class GroupTpl<E> {
	public static final Object USEING = null;
	public static final byte USING_B = 'D';
	public static final char USING_C = 'D';
	public static final int USING_INT = 1;
	public static final long USING_LONG = 1L;
	public static final String USING_S = "D";
	public static final double USING_DOUBLE = 1d;
	public static final float USING_FLOAT = 1f;
	public static final BigDecimal USING_DECIMAL = new BigDecimal(1);
	
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

	public E tpl() {
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
