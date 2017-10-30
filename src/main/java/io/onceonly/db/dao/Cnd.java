package io.onceonly.db.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		for(int i = 0; i < cnd.opts.size(); i++ ) {
			Tuple3<SqlOpt,E,Object[]> opt = cnd.opts.get(i);
			String nextLogic = "";
			if(i < cnd.optsLogic.size()) {
				SqlLogic nl = cnd.optsLogic.get(i);
				switch(nl) {
				case AND:
					nextLogic = " AND";
					break;
				case OR:
					nextLogic = " OR";
					break;
				case NOT:
					nextLogic = " AND NOT";
					break;
					default:
						nextLogic = "";
				}
			}
			Tuple2<String[],Object[]> tpl = adapter.adapterForWhere(opt.b);
			if(tpl == null || tpl.a.length ==0) continue;
			switch(opt.a) {
			case EQ:
				self.append(String.format("(%s=?) %s ", String.join("=? AND", tpl.a),nextLogic));
				sqlArgs.addAll(Arrays.asList(tpl.b));
				break;
			case NE:
				self.append(String.format("(%s!=?) %s ", String.join("!=? AND", tpl.a),nextLogic));
				sqlArgs.addAll(Arrays.asList(tpl.b));
				break;
			case LT:
				self.append(String.format("(%s<?) %s", String.join("=? AND", tpl.a),nextLogic));
				sqlArgs.addAll(Arrays.asList(tpl.b));
				break;
			case LE:
				self.append(String.format("(%s<=?) %s", String.join("=? AND", tpl.a),nextLogic));
				sqlArgs.addAll(Arrays.asList(tpl.b));
				break;
			case GT:
				self.append(String.format("(%s>?) %s", String.join("=? AND", tpl.a),nextLogic));
				sqlArgs.addAll(Arrays.asList(tpl.b));
				break;
			case GE:
				self.append(String.format("(%s>=?) %s", String.join("=? AND", tpl.a),nextLogic));
				sqlArgs.addAll(Arrays.asList(tpl.b));
				break;
			case IS_NULL:
				self.append(String.format("(%s IS NULL) %s", String.join(" IS NULL AND", tpl.a),nextLogic));
				break;
			case NOT_NULL:
				self.append(String.format("(%s NOT NULL) %s", String.join(" NOT NULL AND", tpl.a),nextLogic));
				break;
			case IN:
				if(opt.c != null) {
					List<Object> inArgs = Arrays.asList(opt.c);
					if(!inArgs.isEmpty()){
						String stub = OOUtils.genStub("?", ",", inArgs.size());
						for(String f:tpl.a) {
							self.append(String.format(" %s IN (%s) %s",f, stub,nextLogic));
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
				self.append(String.format("(%s LIKE ?) %s", String.join(" LIKE ? AND", tpl.a),nextLogic));
				sqlArgs.addAll(Arrays.asList(tpl.b));
				break;
			case PATTERN:
				self.append(String.format("(%s ~* ?) %s", String.join(" ~* ? AND", tpl.a),nextLogic));
				sqlArgs.addAll(Arrays.asList(tpl.b));
				break;
			default:
			}
		}
		if(self.length() > 1) {
			self.delete(self.length()-1, self.length());
			self.append(')');
		}else {
			self.delete(0, 1);
		}
		for(Tuple2<SqlLogic, Cnd<E>> c:cnd.cnds){
			String other = sql(c.b,sqlArgs,adapter);
			if(!other.equals("")){
				switch(c.a) {
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
	
	public String having() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String orderBy() {
		// TODO Auto-generated method stub
		return null;
	}
	public String group() {
		// TODO Auto-generated method stub
		return null;
	}
}

