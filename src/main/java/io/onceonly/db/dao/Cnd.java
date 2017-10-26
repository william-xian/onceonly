package io.onceonly.db.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.mx.app.entity.Wallet;
import io.onceonly.util.OOLog;
import io.onceonly.util.OOUtils;
import io.onceonly.util.Tuple2;
import io.onceonly.util.Tuple3;

public class Cnd<E> {
	private static enum SqlLogic {
		AND, OR, NOT,
	}

	private static enum SqlOpt {
		EQ, NE, GT, GE, LT, LE, IS_NULL,NOT_NULL,IN, LIKE, PATTERN,
	}
	private Integer page;
	private Integer pageSize;
	private List<E> order = new ArrayList<>();
	private List<Tuple2<SqlLogic,Cnd<E>>> cnds = new ArrayList<>();
	private List<Tuple3<SqlOpt,E,Object[]>> opts = new ArrayList<>();
	private List<SqlLogic> optsLogic = new ArrayList<>();
	public Cnd() {
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
	public List<E> getOrder() {
		return order;
	}
	public void orderBy(List<E> tmpl) {
		this.order.addAll(tmpl);
	}
	public Cnd<E> eq(E e) {
		opts.add(new Tuple3<SqlOpt, E,Object[]>(SqlOpt.EQ,e,null));
		return this;
	}
	public Cnd<E> ne(E e) {
		opts.add(new Tuple3<SqlOpt,E,Object[]>(SqlOpt.NE,e,null));
		return this;
	}
	public Cnd<E> ge(E e) {
		opts.add(new Tuple3<SqlOpt,E,Object[]>(SqlOpt.GE,e,null));
		return this;
	}
	public Cnd<E> lt(E e) {
		opts.add(new Tuple3<SqlOpt,E,Object[]>(SqlOpt.LT,e,null));
		return this;
	}
	public Cnd<E> le(E e) {
		opts.add(new Tuple3<SqlOpt,E,Object[]>(SqlOpt.LE,e,null));
		return this;
	}
	public Cnd<E> is_null(E e) {
		opts.add(new Tuple3<SqlOpt,E,Object[]>(SqlOpt.IS_NULL,e,null));
		return this;
	}
	public Cnd<E> not_null(E e) {
		opts.add(new Tuple3<SqlOpt,E,Object[]>(SqlOpt.NOT_NULL,e,null));
		return this;
	}
	/**
	 * 对于需要查找null值字段，传递vals为null值，
	 */
	public Cnd<E> in(E tmpl,Object[] vals) {
		opts.add(new Tuple3<SqlOpt,E,Object[]>(SqlOpt.IN,tmpl,vals));
		return this;
	}
	public Cnd<E> like(E tmpl) {
		opts.add(new Tuple3<SqlOpt,E,Object[]>(SqlOpt.LIKE,tmpl,null));
		return this;
	}
	public Cnd<E> pattern(E tmpl) {
		opts.add(new Tuple3<SqlOpt,E,Object[]>(SqlOpt.PATTERN,tmpl,null));
		return this;
	}
	
	public Cnd<E> and() {
		optsLogic.add(SqlLogic.AND);
		return this;
	}
	public Cnd<E> or() {
		optsLogic.add(SqlLogic.OR);
		return this;
	}
	public Cnd<E> not() {
		optsLogic.add(SqlLogic.NOT);
		return this;
	}
	public Cnd<E> and(Cnd<E> cnd) {
		cnds.add(new Tuple2<SqlLogic,Cnd<E>>(SqlLogic.AND,cnd));
		return this;
	}
	public Cnd<E> or(Cnd<E> cnd) {
		cnds.add(new Tuple2<SqlLogic,Cnd<E>>(SqlLogic.OR,cnd));
		return this;
	}
	public Cnd<E> not(Cnd<E> cnd) {
		cnds.add(new Tuple2<SqlLogic,Cnd<E>>(SqlLogic.NOT,cnd));
		return this;
	}
	
	public static <E> String sql(Cnd<E> cnd,List<Object> sqlArgs,TemplateAdapter adapter){
		StringBuffer self = new StringBuffer("(");
		for(Tuple3<SqlOpt,E,Object[]> opt:cnd.opts) {
			Tuple2<String[],Object[]> tpl = adapter.adapter(opt.b);
			if(tpl.a.length ==0) continue;
			switch(opt.a) {
			case EQ:
				self.append(String.format("(%s=?) AND ", String.join("=? AND ", tpl.a)));
				sqlArgs.addAll(Arrays.asList(tpl.b));
			case NE:
				self.append(String.format("(%s!=?) AND ", String.join("!=? AND ", tpl.a)));
				sqlArgs.addAll(Arrays.asList(tpl.b));
			case LT:
				self.append(String.format("(%s<?) AND ", String.join("=? AND ", tpl.a)));
				sqlArgs.addAll(Arrays.asList(tpl.b));
			case LE:
				self.append(String.format("(%s<=?) AND ", String.join("=? AND ", tpl.a)));
				sqlArgs.addAll(Arrays.asList(tpl.b));
				break;
			case GT:
				self.append(String.format("(%s>?) AND ", String.join("=? AND ", tpl.a)));
				sqlArgs.addAll(Arrays.asList(tpl.b));
				break;
			case GE:
				self.append(String.format("(%s>=?) AND ", String.join("=? AND ", tpl.a)));
				sqlArgs.addAll(Arrays.asList(tpl.b));
				break;
			case IS_NULL:
				self.append(String.format("(%s IS NULL) AND ", String.join(" IS NULL AND ", tpl.a)));
				break;
			case NOT_NULL:
				self.append(String.format("(%s NOT NULL) AND ", String.join(" NOT NULL AND ", tpl.a)));
				break;
			case IN:
				if(opt.c != null) {
					List<Object> inArgs = Arrays.asList(opt.c);
					if(!inArgs.isEmpty()){
						String stub = OOUtils.genStub("?", ",", inArgs.size());
						for(String f:tpl.a) {
							self.append(String.format("%s IN (%s) AND ",f, stub));
							sqlArgs.addAll(inArgs);
						}	
					}else {
						OOLog.warnning("条件查询  in 全部为null值");
					}
				} else {
					OOLog.warnning("条件查询  in 全部为null值");
				}
				break;
			case LIKE:
				self.append(String.format("(%s LIKE ?) AND ", String.join(" LIKE ? AND ", tpl.a)));
				sqlArgs.addAll(Arrays.asList(tpl.b));
				break;
			case PATTERN:
				self.append(String.format("(%s ~* ?) AND ", String.join(" ~* ? AND ", tpl.a)));
				sqlArgs.addAll(Arrays.asList(tpl.b));
				break;
			default:
				break;
			}
		}
		if(self.length() > 1) {
			int tailLen = " AND ".length();
			self.delete(self.length()-tailLen, self.length());
			self.append(')');
		}else {
			self.delete(0, 1);
		}
		
		for(Tuple2<SqlLogic, Cnd<E>> c:cnd.cnds){
			String other = sql(c.b,sqlArgs,adapter);
			if(!other.equals("")){
				switch(c.a) {
				case AND:
					self.append(" AND ");
					self.append(other);
					break;
				case OR:
					self.append(" OR ");
					self.append(other);
					break;
				case NOT:
					self.append(" NOT ");
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
	
	public static void main(String[] args) {
		Wallet order = new Wallet();
		order.setExpenditure(DefTmpl.ASC_INT);
		Wallet e = new Wallet();
		e.setId(DefTmpl.SHOW_LONG);
		Cnd<Wallet> cnd = new Cnd<>();
		cnd.eq(e).and().in(e,new Long[] {1L,2L}).orderBy(Arrays.asList(order));
	}
}

