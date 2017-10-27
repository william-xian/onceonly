package io.onceonly.db.dao;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import io.onceonly.db.dao.impl.TemplateAdapterImpl;
import io.onceonly.db.meta.ColumnMeta;
import io.onceonly.db.meta.DDMeta;
import io.onceonly.db.meta.TableMeta;
import io.onceonly.db.tbl.OOEntity;
import io.onceonly.util.OOAssert;
import io.onceonly.util.OOLog;
import io.onceonly.util.OOReflectUtil;
import io.onceonly.util.OOUtils;
import io.onceonly.util.Tuple2;

public class DaoHelper {
	private JdbcTemplate jdbcTemplate;
	private Map<String,TableMeta> tableToTableMeta;
	@SuppressWarnings("rawtypes")
	private Map<String,RowMapper> tableToRowMapper = new HashMap<>();
	private Map<String,DDMeta> tableToDDMata;
	private TemplateAdapterImpl adapter;
	
	public DaoHelper(){
	}
	
	public DaoHelper(JdbcTemplate jdbcTemplate, Map<String, TableMeta> tableToTableMeta) {
		super();
		init(jdbcTemplate,tableToTableMeta);
	}
	public void init(JdbcTemplate jdbcTemplate, Map<String, TableMeta> tableToTableMeta) {
		this.jdbcTemplate = jdbcTemplate;
		this.tableToTableMeta = tableToTableMeta;
		this.adapter = new TemplateAdapterImpl(this.tableToTableMeta);
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public Map<String, TableMeta> getTableToTableMata() {
		return tableToTableMeta;
	}

	public void setTableToTableMata(Map<String, TableMeta> tableToTableMeta) {
		this.tableToTableMeta = tableToTableMeta;
		this.adapter = new TemplateAdapterImpl(this.tableToTableMeta);
	}

	@SuppressWarnings("rawtypes")
	public Map<String, RowMapper> getTableToRowMapper() {
		return tableToRowMapper;
	}

	@SuppressWarnings("rawtypes")
	public void setTableToRowMapper(Map<String, RowMapper> tableToRowMapper) {
		this.tableToRowMapper = tableToRowMapper;
	}

	public Map<String, DDMeta> getTableToDDMata() {
		return tableToDDMata;
	}

	public void setTableToDDMata(Map<String, DDMeta> tableToDDMata) {
		this.tableToDDMata = tableToDDMata;
	}

	public <E extends OOEntity<?>> boolean createOrUpdate(Class<E> tbl) {
		TableMeta old = tableToTableMeta.get(tbl.getSimpleName());
		if(old == null) {
			old = TableMeta.createBy(tbl);
			List<String> sqls = old.createTableSql();
			if(!sqls.isEmpty()) {
				System.out.println(String.join(",", sqls.toArray(new String[0])));
				jdbcTemplate.batchUpdate(sqls.toArray(new String[0]));	
			}
			old.freshNameToField();
			tableToTableMeta.put(tbl.getSimpleName(), old);
			return true;
		}else {
			TableMeta tm = TableMeta.createBy(tbl);
			if(old.equals(tm)){
				return false;
			} else if(tm != null){
				List<String> sqls = old.upgradeTo(tm);
				System.out.println(OOUtils.toJSON(sqls));
				if(!sqls.isEmpty()){
					jdbcTemplate.batchUpdate(sqls.toArray(new String[0]));	
				}
				tm.freshNameToField();
				tableToTableMeta.put(tbl.getSimpleName(), tm);
				return true;
			}
		}
		return false;
	}

	public <E extends OOEntity<?>> boolean drop(Class<E> tbl) {
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		if(tm == null) {
			return false;
		}
		String sql = String.format("DROP TABLE IF EXISTS %s;", tbl.getSimpleName());
		jdbcTemplate.batchUpdate(sql);
		return true;
	}

	public int[] batchUpdate(final String... sql) throws DataAccessException {
		return jdbcTemplate.batchUpdate(sql);
	}
	
	public int[] batchUpdate(final String sql,List<Object[]> batchArgs) throws DataAccessException {
		return jdbcTemplate.batchUpdate(sql, batchArgs);
	}
	
	private static <E extends OOEntity<?>> RowMapper<E> genRowMapper(Class<E> tbl,TableMeta tm) {
		RowMapper<E> rowMapper = new RowMapper<E>(){
			@Override
			public E mapRow(ResultSet rs, int rowNum) throws SQLException {
				E row = createBy(tbl,tm,rs);
				return row;
			}
		};
		return rowMapper;
	}
	
	public static <E extends OOEntity<?>> E createBy(Class<E> tbl,TableMeta tm,ResultSet rs) throws SQLException {
		E row = null;
		if(rs.next()) {
			try {
				row = tbl.newInstance();
				List<ColumnMeta> columnMetas = tm.getColumnMetas();
				for(ColumnMeta colMeta:columnMetas) {
					Field field = colMeta.getField();
					Object val = rs.getObject(colMeta.getName(), field.getType());
					field.set(row, val);
				}
				
			} catch (InstantiationException | IllegalAccessException e) {
				OOAssert.warnning("%s InstantiationException", tbl);
			} 
		}
		return row;
	}
	
	@SuppressWarnings("unchecked")
	public <E extends OOEntity<?>,ID> E get(Class<E> tbl,ID id) {
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		if(tm == null) {
			return null;
		}
		RowMapper<E> rowMapper = null;
		if(!tableToRowMapper.containsKey(tbl.getSimpleName())) {
			rowMapper = genRowMapper(tbl,tm);
			tableToRowMapper.put(tbl.getSimpleName(), rowMapper);
		}else {
			rowMapper = tableToRowMapper.get(tbl.getSimpleName());	
		}
		String sql = String.format("SELECT * FROM %s WHERE %s = (?)", tm.getTable(),tm.getPrimaryKey());
		List<E> values = jdbcTemplate.query(sql, new Object[]{id}, rowMapper);
		if(values.size() == 1){
			return values.get(0);
		}
		return null;
	}
	
	/**
	 * 第一个一定是主键
	 */
	private <E extends OOEntity<?>> Tuple2<List<String>,List<List<Object>>> fetchNamesValues(List<ColumnMeta> columnMetas,boolean ignoreNull,List<E> entities) {
		List<String> names = new ArrayList<>(columnMetas.size());
		List<List<Object>> valsList = new ArrayList<>(entities.size());
		boolean hasNames = false;
		for(E entity:entities) {
			if(entity == null) continue;
			List<Object> vals = new ArrayList<>(columnMetas.size());
			valsList.add(vals);
			for(ColumnMeta cm:columnMetas) {
				if(!hasNames) {
					if(cm.isPrimaryKey()) {
						names.add(0,cm.getName());	
					}else {
						names.add(cm.getName());
					}
				}
				try {
					Object val = cm.getField().get(entity);
					if(val != null || !ignoreNull) {
						if(cm.isPrimaryKey()) {
							vals.add(0,val);	
						}else {
							vals.add(val);	
						}
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					OOAssert.warnning("%s.%s 访问异常:%s", entity.getClass().getSimpleName(),cm.getName(),e.getMessage());
				}
			}
			hasNames = true;
		}
		return new Tuple2<List<String>,List<List<Object>>>(names,valsList);
	}
	
	public <E extends OOEntity<?>> E insert(E entity) {
		OOAssert.warnning(entity != null,"不可以插入null");
		Class<?> tbl = entity.getClass();
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());	
		OOAssert.fatal(tm != null,"无法找到表：%s",tbl.getSimpleName());
		Tuple2<List<String>,List<List<Object>>>  nameVals = fetchNamesValues(tm.getColumnMetas(),false,Arrays.asList(entity));
		List<Object> vals = nameVals.b.get(0);
		String stub = OOUtils.genStub("?",",",nameVals.a.size());
		String sql = String.format("INSERT INTO %s(%s) VALUES(%s);", tm.getTable(),String.join(",", nameVals.a),stub);
		jdbcTemplate.update(sql, vals.toArray());
		return entity;
	}
	
	public <E extends OOEntity<?>> int insert(List<E> entities) {
		OOAssert.warnning(entities != null && !entities.isEmpty(),"不可以插入null");
		Class<?> tbl = entities.get(0).getClass();
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		OOAssert.fatal(tm != null,"无法找到表：%s",tbl.getSimpleName());
		Tuple2<List<String>,List<List<Object>>>  nameVals = fetchNamesValues(tm.getColumnMetas(),false,entities);
		String stub = OOUtils.genStub("?",",",nameVals.a.size());
		String sql = String.format("INSERT INTO %s(%s) VALUES(%s);", tm.getTable(),String.join(",", nameVals.a),stub);
		int[] cnts = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				List<Object> vals = nameVals.b.get(i);
				for(int vi = 0;  vi < vals.size(); vi++) {
					ps.setObject(vi+1, vals.get(vi));
				}
				OOLog.debug("%s values:%s",sql, OOUtils.toJSON(vals));
			}
			@Override
			public int getBatchSize() {
				return entities.size();
			}
			
		});
		int cnt = 0;
		for(int c:cnts) {
			cnt += c;
		}
		return cnt;
	}

	public <E extends OOEntity<?>> int update(E entity) {
		OOAssert.warnning(entity != null,"不可以插入null");
		Class<?> tbl = entity.getClass();
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());	
		OOAssert.fatal(tm != null,"无法找到表：%s",tbl.getSimpleName());
		Tuple2<List<String>,List<List<Object>>>  nameVals = fetchNamesValues(tm.getColumnMetas(),false,Arrays.asList(entity));
		List<Object> vals = nameVals.b.get(0);
		Object id = vals.get(0);
		OOAssert.err(id != null,"ID 不能为NULL");
		vals.add(id);
		vals.remove(0);
		String idName = nameVals.a.get(0);
		nameVals.a.remove(0);
		String sql = String.format("UPDATE %s SET %s=? WHERE %s=?;", tm.getTable(),String.join("=?,", nameVals.a),idName);
		return jdbcTemplate.update(sql, vals.toArray());
	}

	public <E extends OOEntity<?>> int updateIgnoreNull(E entity) {
		OOAssert.warnning(entity != null,"不可以插入null");
		Class<?> tbl = entity.getClass();
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());	
		OOAssert.fatal(tm != null,"无法找到表：%s",tbl.getSimpleName());
		Tuple2<List<String>,List<List<Object>>>  nameVals = fetchNamesValues(tm.getColumnMetas(),true,Arrays.asList(entity));
		List<Object> vals = nameVals.b.get(0);
		Object id = vals.get(0);
		OOAssert.err(id != null,"ID 不能为NULL");
		vals.add(id);
		vals.remove(0);
		String idName = nameVals.a.get(0);
		nameVals.a.remove(0);
		String sql = String.format("UPDATE %s SET %s=? WHERE %s=?;", tm.getTable(),String.join("=?,", nameVals.a),idName);
		return jdbcTemplate.update(sql, vals.toArray());
	}

	public <E extends OOEntity<?>> int updateIncrement(E increment) {
		OOAssert.warnning(increment != null,"不可以插入null");
		Class<?> tbl = increment.getClass();
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());	
		OOAssert.fatal(tm != null,"无法找到表：%s",tbl.getSimpleName());
		OOEntity<?> be = (OOEntity<?>) increment;
		OOAssert.err(be.getId() != null,"ID 不能为NULL");
		Tuple2<List<String>,List<List<Object>>>  nameVals = fetchNamesValues(tm.getColumnMetas(),true,Arrays.asList(increment));
		List<Object> vals = nameVals.b.get(0);
		Object id = vals.get(0);
		vals.add(id);
		vals.remove(0);
		String idName = nameVals.a.get(0);
		nameVals.a.remove(0);
		StringBuffer settings = new StringBuffer();
		for(int i = 0; i < nameVals.a.size(); i++) {
			String name = nameVals.a.get(i);
			Object val = vals.get(0);
			if(OOReflectUtil.isNumber(val)) {
				settings.append(String.format("%s=%s+(?),", name));
			}else if(OOReflectUtil.isCharacter(val)) {
				settings.append(String.format("%s=%s||?,", name));	
			}
		}
		settings.delete(settings.length()-1, settings.length());
		String sql = String.format("UPDATE %s SET %s WHERE %s=?;", tm.getTable(),settings,idName);
		return jdbcTemplate.update(sql, vals.toArray());
	}

	public <E extends OOEntity<?>> int updateIncrement(E increment, Cnd<E> cnd) {
		OOAssert.warnning(increment != null,"不可以插入null");
		Class<?> tbl = increment.getClass();
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());	
		OOAssert.fatal(tm != null,"无法找到表：%s",tbl.getSimpleName());
		Tuple2<List<String>,List<List<Object>>>  nameVals = fetchNamesValues(tm.getColumnMetas(),true,Arrays.asList(increment));
		List<Object> vals = nameVals.b.get(0);
		StringBuffer settings = new StringBuffer();
		for(int i = 0; i < nameVals.a.size(); i++) {
			String name = nameVals.a.get(i);
			Object val = vals.get(0);
			if(OOReflectUtil.isNumber(val)) {
				settings.append(String.format("%s=%s+(?),", name));
			}else if(OOReflectUtil.isCharacter(val)) {
				settings.append(String.format("%s=%s||?,", name));	
			}
		}
		settings.delete(settings.length()-1, settings.length());
		List<Object> sqlArgs = new ArrayList<>();
		String cndSql = Cnd.sql(cnd, sqlArgs, adapter);
		if(cndSql.isEmpty()) {
			OOAssert.warnning("查询条件不能为空");
		}
		vals.addAll(sqlArgs);
		String sql = String.format("UPDATE %s SET %s WHERE (%s);", tm.getTable(),settings,cndSql);
		return jdbcTemplate.update(sql, vals.toArray());
	}

	public <E,ID> int remove(Class<E> tbl,ID id) {
		if(id == null) return 0;
		OOAssert.warnning(id != null,"ID不能为null");
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		String sql = String.format("UPDATE %s SET del=false WHERE id=?;", tm.getTable());
		return jdbcTemplate.update(sql, id);
	}

	public <E,ID> int remove(Class<E> tbl, List<ID> ids) {
		if(ids == null || ids.isEmpty()) return 0;
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		String stub = OOUtils.genStub("?",",",ids.size());
		String sql = String.format("UPDATE %s SET del=false WHERE id in (%s);", tm.getTable(),stub);
		return jdbcTemplate.update(sql, ids);
	}
	public <E extends OOEntity<?>> int remove(Class<E> tbl, Cnd<E> cnd) {
		if(cnd == null) return 0;
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		List<Object> sqlArgs = new ArrayList<>();
		String whereCnd = Cnd.sql(cnd, sqlArgs, adapter);
		if(whereCnd.equals("")) {
			return 0;
		}
		String sql = String.format("UPDATE %s SET del=false WHERE (%s);", tm.getTable(),whereCnd);
		return jdbcTemplate.update(sql, sqlArgs);
	}
	public <E,ID> int delete(Class<E> tbl, ID id) {
		if(id == null) return 0;
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		String sql = String.format("DELETE FROM %s WHERE id=?;", tm.getTable());
		return jdbcTemplate.update(sql, id);
	}
	public <E,ID> int delete(Class<E> tbl, List<ID> ids) {
		if(ids == null || ids.isEmpty()) return 0;
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		String sql = String.format("UPDATE %s SET del=false WHERE id in (%s);", tm.getTable());
		return jdbcTemplate.update(sql, ids);
	}
	
	public <E extends OOEntity<?>> int delete(Class<E> tbl, Cnd<E> cnd) {
		if (cnd == null) return 0;
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		List<Object> sqlArgs = new ArrayList<>();
		String whereCnd = Cnd.sql(cnd, sqlArgs, adapter);
		if (whereCnd.equals("")) {
			return 0;
		}
		String sql = String.format("DELETE FROM %s WHERE (%s);", tm.getTable(), whereCnd);
		return jdbcTemplate.update(sql, sqlArgs);
	}


	public <E extends OOEntity<?>> long count(Class<E> tbl) {
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		String sql = String.format("SELECT COUNT(1) FROM %s;", tm.getTable());
		return jdbcTemplate.queryForObject(sql, Long.class);
	}

	public <E extends OOEntity<?>> long count(Class<E> tbl, Cnd<E> cnd) {
		if (cnd == null) return 0;
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		List<Object> sqlArgs = new ArrayList<>();
		String whereCnd = Cnd.sql(cnd, sqlArgs, adapter);
		
		String sql = String.format("SELECT COUNT(1) FROM %s WHERE %s;", tm.getTable(), whereCnd);
		if (whereCnd.equals("")) {
			sql = String.format("SELECT COUNT(1) FROM %s;", tm.getTable());
		}
		return jdbcTemplate.queryForObject(sql,sqlArgs.toArray(new Object[0]), Long.class);
	}

	public <E extends OOEntity<?>> Page<E> find(Class<E> tbl,Cnd<E> cnd) {
		return find(tbl,null,cnd);
	}
	@SuppressWarnings("unchecked")
	public <E extends OOEntity<?>> Page<E> find(Class<E> tbl,E tmpl,Cnd<E> cnd) {
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		if(tm == null) {
			return null;
		}
		Tuple2<String,Object[]> sqlAndArgs = queryFieldCnd(tm,tmpl,cnd);
		RowMapper<E> rowMapper = null;
		if(!tableToRowMapper.containsKey(tbl.getSimpleName())) {
			rowMapper = genRowMapper(tbl,tm);
			tableToRowMapper.put(tbl.getSimpleName(), rowMapper);
		}else {
			rowMapper = tableToRowMapper.get(tbl.getSimpleName());	
		}
		Page<E> page = new Page<E>();
		if(cnd.getPage() == null ||cnd.getPage() <= 0) {
			page.setPage(cnd.getPage());
			if(cnd.getPage() == null || cnd.getPage() == 0) {
				cnd.setPage(1);
				page.setPage(0);
			}else {
				cnd.setPage(Math.abs(cnd.getPage()));
			}
			page.setTotal(count(tbl,cnd));
			
		}
		if(page.getPage() > 0 && page.getTotal() > 0) {
			List<E> data = jdbcTemplate.query(sqlAndArgs.a,sqlAndArgs.b, rowMapper);
			page.setData(data);	
		}else {
			page.setPage(cnd.getPage());
		}
		return page;
	}

	private <E extends OOEntity<?>> Tuple2<String,Object[]> queryFieldCnd(TableMeta tm,E tmpl,Cnd<E> cnd) {
		StringBuffer sqlSelect = new StringBuffer("SELECT ");
		String columns = "*";
		if(tmpl != null) {
			Tuple2<String[], Object[]> nameVals = adapter.adapterForSelect(tmpl);
			if(nameVals != null && nameVals.a.length > 0) {
				sqlSelect.append(String.join(",", nameVals.a));		
			}else {
				sqlSelect.append(" * ");		
			}
		}else {
			sqlSelect.append(" * ");
		}
		List<Object> sqlArgs = new ArrayList<>();
		String whereCnd = Cnd.sql(cnd, sqlArgs, adapter);
		if (whereCnd.equals("")) {
			sqlSelect.append(String.format(" FROM %s", tm.getTable()));
		} else {
			sqlSelect.append(String.format(" FROM %s WHERE (%s)", columns, tm.getTable(), whereCnd));
		}
		String having = cnd.having();
		if(having != null && !having.isEmpty()) {
			sqlSelect.append(String.format(" HAVING %s", having));
		}
		String orderBy = cnd.orderBy();
		if(orderBy != null && !orderBy.isEmpty()) {
			sqlSelect.append(String.format(" ORDER BY %s", orderBy));
		}
		return new Tuple2<String,Object[]>(sqlSelect.toString(),sqlArgs.toArray(new Object[0]));
	}
	
	public <E extends OOEntity<?>> void download(Class<E> tbl,E tmpl,Cnd<E> cnd, Consumer<E> consumer) {
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		if(tm == null) {
			return ;
		}
		Tuple2<String,Object[]> sqlAndArgs = queryFieldCnd(tm,tmpl,cnd);
		jdbcTemplate.query(sqlAndArgs.a, sqlAndArgs.b, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				E row = createBy(tbl, tm, rs);
				consumer.accept(row);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <E extends OOEntity<?>,ID> List<E> findByIds(Class<E> tbl, List<ID> ids) {
		if(ids.isEmpty()) {
			return new ArrayList<E>();
		}
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		if(tm == null) {
		}
		RowMapper<E> rowMapper = null;
		if(!tableToRowMapper.containsKey(tbl.getSimpleName())) {
			rowMapper = genRowMapper(tbl,tm);
			tableToRowMapper.put(tbl.getSimpleName(), rowMapper);
		}else {
			rowMapper = tableToRowMapper.get(tbl.getSimpleName());	
		}
		String stub = OOUtils.genStub("?",",",ids.size());
		String sql = String.format("SELECT * FROM %s WHERE %s in (%s)", tm.getTable(),tm.getPrimaryKey(),stub);
		List<E> values = jdbcTemplate.query(sql, ids.toArray(), rowMapper);
		return values;
	}
	
}
