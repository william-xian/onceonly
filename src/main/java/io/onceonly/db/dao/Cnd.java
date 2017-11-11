package io.onceonly.db.dao;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.internal.cglib.proxy.Enhancer;
import org.assertj.core.internal.cglib.proxy.MethodInterceptor;
import org.assertj.core.internal.cglib.proxy.MethodProxy;

import io.onceonly.db.dao.tpl.GroupTpl;
import io.onceonly.db.dao.tpl.HavingTpl;
import io.onceonly.db.dao.tpl.OrderTpl;
import io.onceonly.db.dao.tpl.SelectTpl;
import io.onceonly.db.dao.tpl.SqlLogic;
import io.onceonly.db.dao.tpl.Tpl;
import io.onceonly.db.meta.TableMeta;
import io.onceonly.util.OOLog;
import io.onceonly.util.OOUtils;

public class Cnd<E> extends Tpl{
	private Integer page;
	private Integer pageSize;
	private E pageArg;
	private Boolean isNext;
	private HavingTpl<E> having;
	private GroupTpl<E> group;
	private OrderTpl<E> order;
	private List<Object> args = new ArrayList<>();
	private String opt = null;
	private Object[] inVals;
	private SqlLogic logic = null;
	private List<SqlLogic> extLogics = new ArrayList<>();
	private List<Cnd<E>> extCnds = new ArrayList<>();
	private StringBuffer selfSql = new StringBuffer();
	private Class<E> tplClass;
	private E tpl;
	
	@SuppressWarnings("unchecked")
	public Cnd(Class<E> tplClass) {
		this.tplClass = tplClass;
		CndSetterProxy cglibProxy = new CndSetterProxy();
        Enhancer enhancer = new Enhancer();  
        enhancer.setSuperclass(tplClass);  
        enhancer.setCallback(cglibProxy);  
        tpl = (E)enhancer.create(); 
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getPage() {
		return page;
	}

	public E getPageArg() {
		return pageArg;
	}
	public void setPageArg(E pageArg) {
		this.pageArg = pageArg;
	}
	public Boolean getIsNext() {
		return isNext;
	}
	public void setIsNext(Boolean isNext) {
		this.isNext = isNext;
	}
	public E eq() {
		opt = "=";
		return tpl;
	}
	public E ne() {
		opt = "!=";
		return tpl;
	}
	public E lt() {
		opt = "<";
		return tpl;
	}
	public E le() {
		opt = "<=";
		return tpl;
	}
	public E gt() {
		opt = ">";
		return tpl;
	}
	public E ge() {
		opt = ">=";
		return tpl;
	}

	public E is_null() {
		opt = "IS NULL";
		return tpl;
	}
	public E not_null(E e) {
		opt = "IS NOT NULL";
		return tpl;
	}
	/**
	 * 对于需要查找null值字段，传递vals为null值，
	 */
	public E in(Object[] vals) {
		opt = "IN";
		inVals = vals;
		return tpl;
	}
	public E like() {
		opt = "LIKE";
		return tpl;
	}
	public E pattern() {
		opt = "~*";
		return tpl;
	}
	public Cnd<E> and() {
		if(opt != null) {
			logic = SqlLogic.AND;	
		}
		return this;
	}
	public Cnd<E> or() {
		if(opt != null) {
			logic = SqlLogic.OR;	
		}
		return this;
	}
	public Cnd<E> not() {
		if(opt != null) {
			logic = SqlLogic.NOT;	
		}
		return this;
	}

	public Cnd<E> and(Cnd<E> extCnd) {
		extLogics.add(SqlLogic.AND);
		extCnds.add(extCnd);
		return this;
	}
	public Cnd<E> or(Cnd<E> extCnd) {
		extLogics.add(SqlLogic.OR);
		extCnds.add(extCnd);
		return this;
	}
	public Cnd<E> not(Cnd<E> extCnd) {
		extLogics.add(SqlLogic.NOT);
		extCnds.add(extCnd);
		return this;
	}
	
	public String whereSql(List<Object> sqlArgs){
		StringBuffer self = new StringBuffer();
		if(selfSql.length() > 0) {
			self.append("("+selfSql+")");
		}
		sqlArgs.addAll(args);
		for(int i =0; i < this.extLogics.size(); i++) {
			SqlLogic sl = this.extLogics.get(i);
			Cnd<E> c = extCnds.get(i);
			String other = c.whereSql(sqlArgs);
			if(!other.equals("")){
				switch(sl) {
				case AND:
					if(self.length() > 0) self.append(" AND");
					self.append(other);
					break;
				case OR:
					if(self.length() > 0) self.append(" OR");
					self.append(other);
					break;
				case NOT:
					if(self.length() > 0) {
						self.append(" AND NOT");
					} else {
						self.append(" NOT");
					}
					self.append(other);
					break;
					default:
				}	
			}else {
				OOLog.warnning("查询条件是空的");
			}
		}
		return self.toString();
	}
	
	public String afterWhere(List<Object> sqlArgs) {
		StringBuffer afterWhere = new StringBuffer();
		String whereCnd = whereSql(sqlArgs);
		if (!whereCnd.equals("")) {
			afterWhere.append(String.format(" WHERE (%s)", whereCnd));
		}
		String having = getHaving();
		if(having != null && !having.isEmpty()) {
			afterWhere.append(String.format(" HAVING %s", having));
		}
		String group = group();
		if(group != null && !group.isEmpty()) {
			afterWhere.append(String.format(" GROUP BY %s", group));
		}
		return afterWhere.toString();
	}
	
	public StringBuffer wholeSql(TableMeta tm,SelectTpl<E> tpl,List<Object> sqlArgs) {
		StringBuffer sqlSelect = new StringBuffer("SELECT");
		if(tpl != null) {
			if(tpl.sql() != null && !tpl.sql().isEmpty()) {
				sqlSelect.append(" " + tpl.sql());		
			}else {
				sqlSelect.append(" *");		
			}
		}else {
			sqlSelect.append(" *");
		}
		sqlSelect.append(String.format(" FROM %s", tm.getTable()));
		afterWhere(sqlArgs);
		return sqlSelect;
	}
	
	//TODO 根据上一条数据 和order语句计算相临的两页数据
	public String pageSql(TableMeta tm,SelectTpl<E> tpl,List<Object> sqlArgs) {
		StringBuffer s = wholeSql(tm,tpl,sqlArgs);
		s.append(" LIMIT ? OFFSET ?");
		sqlArgs.addAll(Arrays.asList(getPageSize(),(getPage()-1)*getPageSize()));
		return s.toString();
	}
	
	public HavingTpl<E> having() {
		if(having == null) {
			having = new HavingTpl<E>(tplClass);
		}
		return having;
	}
	public String getHaving() {
		if(having!=null) {
			return having.sql();
		}else {
			return null;
		}
	}
	public OrderTpl<E> orderBy() {
		if(order == null) {
			order = new OrderTpl<E>(tplClass);
		}
		return order;
	}
	public String getOrder() {
		if(order != null) {
			return order.toString();	
		}else {
			return "";
		}
	}
	public GroupTpl<E> groupBy(){
		if(group == null) {
			group = new GroupTpl<E>(tplClass);
		}
		return group;
	}
	public String group() {
		if(group != null) {
			return group.getGroup();
		}else {
			return null;
		}
	}
	
	class CndSetterProxy implements MethodInterceptor {  
	    @Override  
	    public Object intercept(Object o, Method method, Object[] argsx, MethodProxy methodProxy) throws Throwable {
	        if(method.getName().startsWith("set") && argsx.length == 1) {
	            if(method.getName().length() > 3) {
	            	String fieldName = method.getName().substring(3,4).toLowerCase() +method.getName().substring(4);
	            	args.add(argsx[0]);
	            	String strLogic = "";
	            	if(logic != null) {
	            		strLogic = logic.toString() +" ";
	            	}
	            	if(opt.equals("IN")&& inVals!=null && inVals.length>1) {
	            		String stub = OOUtils.genStub("?", ",", inVals.length);
	            		selfSql.append(String.format("%s%s %s (%s)", strLogic,fieldName,opt, stub));
	            	}else if(opt.equals("IS NULL")){
	            		selfSql.append(String.format("%s%s IS NULL", strLogic,fieldName));
	            	}else if(opt.equals("IS NOT NULL")){
	            		selfSql.append(String.format("%s%s IS NOT NULL",strLogic, fieldName));
	            	}else {
	            		selfSql.append(String.format("%s%s %s ?", strLogic, fieldName,opt));
	            	}
	            }
	        }
	        return o;  
	    }  
	}
	
}

