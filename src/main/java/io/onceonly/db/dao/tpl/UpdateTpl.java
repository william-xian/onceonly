package io.onceonly.db.dao.tpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.internal.cglib.proxy.Enhancer;
import org.assertj.core.internal.cglib.proxy.MethodInterceptor;
import org.assertj.core.internal.cglib.proxy.MethodProxy;

public class UpdateTpl<T> extends Tpl{
	private StringBuffer sql = new StringBuffer();
	private T tpl;
	private String strOpt;
	private Object id;
	private List<Object> args =  new ArrayList<>();
	@SuppressWarnings("unchecked")
	public UpdateTpl(Class<T> tplClass) {
		UpdateSetterProxy cglibProxy = new UpdateSetterProxy();
        Enhancer enhancer = new Enhancer();  
        enhancer.setSuperclass(tplClass);  
        enhancer.setCallback(cglibProxy);  
        tpl = (T)enhancer.create(); 
	}
	
	public void setId(Object id) {
		this.id = id;
	}

	public Object getId() {
		return id;
	}
	public T add() {
		strOpt = "+";
		return tpl;
	}
	public T sub() {
		strOpt = "-";
		return tpl;
	}
	public T mul() {
		strOpt = "*";
		return tpl;
	}
	public T div() {
		strOpt = "/";
		return tpl;
	}
	public T and() {
		strOpt = "&";
		return tpl;
	}
	public T or() {
		strOpt = "|";
		return tpl;
	}
	public T not() {
		strOpt = "~";
		return tpl;
	}
	public T xor() {
		strOpt = "#";
		return tpl;
	}
	public T left_shift() {
		strOpt = "<<";
		return tpl;
	}
	public T right_shift() {
		strOpt = ">>";
		return tpl;
	}
	
	public String getSetTpl() {
		return sql.substring(0,sql.length()-1);
	}

	public List<Object> getArgs() {
		return args;
	}

	class UpdateSetterProxy implements MethodInterceptor {  
	    @Override  
	    public Object intercept(Object o, Method method, Object[] argsx, MethodProxy methodProxy) throws Throwable {
	        if(method.getName().startsWith("set") && argsx.length == 1) {
	            if(method.getName().length() > 3) {
	            	String fieldName = method.getName().substring(3,4).toLowerCase() +method.getName().substring(4);
		            Object arg = argsx[0];
		            if(fieldName.equals("id") || fieldName.equals("rm")) {
		            }else if(strOpt != null && strOpt.equals("~")) {
	            		sql.append(String.format("%s=~(%s),", fieldName,fieldName));
	            	}else if(arg != null) {
		            	args.add(arg);
		            	if(!fieldName.equals("~")) {
		            		sql.append(String.format("%s=%s%s(?),", fieldName,fieldName,strOpt));
		            	}
		            }
	            }
	        }
	        return o;  
	    }  
	}	
}
