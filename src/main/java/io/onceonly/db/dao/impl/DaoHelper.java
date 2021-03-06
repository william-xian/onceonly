package io.onceonly.db.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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

import io.onceonly.OOConfig;
import io.onceonly.db.annotation.ConstraintType;
import io.onceonly.db.dao.Cnd;
import io.onceonly.db.dao.IdGenerator;
import io.onceonly.db.dao.Page;
import io.onceonly.db.dao.tpl.SelectTpl;
import io.onceonly.db.dao.tpl.UpdateTpl;
import io.onceonly.db.meta.ColumnMeta;
import io.onceonly.db.meta.ConstraintMeta;
import io.onceonly.db.meta.TableMeta;
import io.onceonly.db.tbl.OOEntity;
import io.onceonly.db.tbl.OOTableMeta;
import io.onceonly.exception.VolidateFailed;
import io.onceonly.util.OOAssert;
import io.onceonly.util.OOLog;
import io.onceonly.util.OOUtils;

public class DaoHelper {
	private JdbcTemplate jdbcTemplate;
	private Map<String,TableMeta> tableToTableMeta;
	private IdGenerator idGenerator;
	@SuppressWarnings("rawtypes")
	private Map<String,RowMapper> tableToRowMapper = new HashMap<>();
	private List<Class<? extends OOEntity>> entities;
	public DaoHelper(){
	}
	
	public DaoHelper(JdbcTemplate jdbcTemplate, IdGenerator idGenerator,List<Class<? extends OOEntity>> entitys) {
		super();
		init(jdbcTemplate,idGenerator,entitys);
	}
	
	public boolean exist(Class<?> tbl) {
		Integer cnt = jdbcTemplate.queryForObject(String.format("select count(*) from pg_class where relname = '%s'", tbl.getSimpleName().toLowerCase()), Integer.class);
		if(cnt != null && cnt > 0) {
			return true;
		}else {
			return false;
		}
	}
	
	public void init(JdbcTemplate jdbcTemplate,IdGenerator idGenerator,List<Class<? extends OOEntity>> entities) {
		this.jdbcTemplate = jdbcTemplate;
		this.idGenerator = idGenerator;
		this.tableToTableMeta = new HashMap<>();
		if(!exist(OOTableMeta.class)) {
			List<String> sqls = this.createOrUpdate(OOTableMeta.class);
			if(sqls != null && !sqls.isEmpty()) {
				jdbcTemplate.batchUpdate(sqls.toArray(new String[0]));	
			}
		}
		TableMeta tm = TableMeta.createBy(OOTableMeta.class);
		tableToTableMeta.put(tm.getTable(), tm);
		Cnd<OOTableMeta> cnd = new Cnd<>(OOTableMeta.class);
		cnd.setPage(1);
		cnd.setPageSize(Integer.MAX_VALUE);
		Page<OOTableMeta> page = this.find(OOTableMeta.class, cnd);
		for(OOTableMeta meta:page.getData()) {
			if(meta.getName().equals(OOTableMeta.class.getSimpleName())){
				continue;
			}
			TableMeta old = OOUtils.createFromJson(meta.getVal(), TableMeta.class);
			old.getFieldConstraint();
			old.freshConstraintMetaTable();
			old.freshNameToField();
			tableToTableMeta.put(old.getTable(), old);
		}
		if(entities != null) {
			this.entities = entities;
			Map<String,List<String>> tblSqls = new HashMap<>();
			for(Class<? extends OOEntity> tbl:entities) {
				List<String> sqls = this.createOrUpdate(tbl);
				if(sqls != null && !sqls.isEmpty()) {
					tblSqls.put(tbl.getSimpleName(), sqls);
					Cnd<OOTableMeta> cndMeta = new Cnd<>(OOTableMeta.class);
					cndMeta.eq().setName(tbl.getSimpleName());
					TableMeta tblMeta = TableMeta.createBy(tbl);
					OOTableMeta ootm = this.fetch(OOTableMeta.class, null, cndMeta);
					save(ootm, tblMeta.getTable(), OOUtils.toJSON(tblMeta));
				}
			}
			List<String> order = new ArrayList<>();
			for(String tbl:tblSqls.keySet()) {
				sorted(tbl,order);
			}
			
			List<String> sqls = new ArrayList<>();
			for(String tbl:order) {
				sqls.addAll(tblSqls.get(tbl));
			}
			if(!sqls.isEmpty()) {
				jdbcTemplate.batchUpdate(sqls.toArray(new String[0]));	
			}
		}
		
	}
	private void sorted(String tbl,List<String> order) {
		if(!order.contains(tbl)) {
			TableMeta tblMeta = tableToTableMeta.get(tbl);
			if(tblMeta != null) {
				for(ConstraintMeta cm:tblMeta.getFieldConstraint()) {
					if(cm.getType().equals(ConstraintType.FOREGIN_KEY)) {
						sorted(cm.getRefTable(),order);
					}
				}
			}
			order.add(tbl);
		}
	}

	public List<Class<? extends OOEntity>> getEntities() {
		return entities;
	}

	public IdGenerator getIdGenerator() {
		return idGenerator;
	}

	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
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
	}

	@SuppressWarnings("rawtypes")
	public Map<String, RowMapper> getTableToRowMapper() {
		return tableToRowMapper;
	}

	@SuppressWarnings("rawtypes")
	public void setTableToRowMapper(Map<String, RowMapper> tableToRowMapper) {
		this.tableToRowMapper = tableToRowMapper;
	}

	private void save(OOTableMeta ootm,String name,String val) {
		if(ootm == null) {
			ootm = new OOTableMeta();
			ootm.setId(idGenerator.next(OOTableMeta.class));
			ootm.setName(name);
			ootm.setVal(val);
			ootm.setCreatetime(System.currentTimeMillis());
			insert(ootm);
		}else {
			ootm.setVal(val);
			update(ootm);
		}
	}
	
	public <E extends OOEntity> List<String> createOrUpdate(Class<E> tbl) {
		TableMeta old = tableToTableMeta.get(tbl.getSimpleName());
		if(old == null) {
			old = TableMeta.createBy(tbl);
			List<String> sqls = old.createTableSql();
			tableToTableMeta.put(old.getTable(), old);
			return sqls;
		}else {
			TableMeta tm = TableMeta.createBy(tbl);
			if(old.equals(tm)){
			} else {
				List<String> sqls = old.upgradeTo(tm);
				tableToTableMeta.put(tm.getTable(), tm);
				return sqls;
			}
		}
		return null;
	}

	public <E extends OOEntity> boolean drop(Class<E> tbl) {
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
	
	private static <E extends OOEntity> RowMapper<E> genRowMapper(Class<E> tbl,TableMeta tm) {
		RowMapper<E> rowMapper = new RowMapper<E>(){
			@Override
			public E mapRow(ResultSet rs, int rowNum) throws SQLException {
				E row = createBy(tbl,tm,rs);
				return row;
			}
		};
		return rowMapper;
	}
	public static <E extends OOEntity> E createBy(Class<E> tbl,TableMeta tm,ResultSet rs) throws SQLException {
		E row = null;
		try {
			row = tbl.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			OOAssert.warnning("%s InstantiationException", tbl);
		}
		if(row != null) {
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String colName = rsmd.getColumnName(i);
				ColumnMeta cm = tm.getColumnMetaByName(colName);
				if (cm != null) {
					try {
						Object val = rs.getObject(colName, cm.getJavaBaseType());
						cm.getField().set(row, val);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				} else {
					row.put(colName, rs.getObject(i));
				}
			}
			return row;
		}
		return row;
	}
	
	public <E extends OOEntity> E get(Class<E> tbl,Long id) {
		Cnd<E> cnd = new Cnd<E>(tbl);
		cnd.setPage(1);
		cnd.setPageSize(1);
		cnd.eq().setId(id);
		Page<E> page = find(tbl,null,cnd);
		if(page.getData().size() == 1) {
			return page.getData().get(0);
		}
		return null;
	}
	
	public <E extends OOEntity> E insert(E entity) {
		OOAssert.warnning(entity != null,"不可以插入null");
		Class<?> tbl = entity.getClass();
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());	
		OOAssert.fatal(tm != null,"无法找到表：%s",tbl.getSimpleName());
		validate(tm,entity,false);
		TblIdNameVal<E> idNameVal = new TblIdNameVal<>(tm.getColumnMetas(),Arrays.asList(entity));
		if(idNameVal.getIdAt(0) == null) {
			Object id = idGenerator.next(tbl);
			idNameVal.setIdAt(0, id);
		}
		idNameVal.dropAllNullColumns();
		List<Object> vals = idNameVal.getIdValsList().get(0);
		List<String> names = idNameVal.getIdNames();
		String stub = OOUtils.genStub("?",",",names.size());
		String sql = String.format("INSERT INTO %s(%s) VALUES(%s);", tm.getTable(),String.join(",", names),stub);
		jdbcTemplate.update(sql, vals.toArray());
		return entity;
	}
	private void validate(TableMeta tm,Object obj,boolean ignoreNull) {
		for(ColumnMeta cm:tm.getColumnMetas()) {
			if(cm.getName().equals("id") || cm.getName().equals("rm")) {
				continue;
			}
			Object val = null;
			try {
				val = cm.getField().get(obj);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				OOLog.info(e.getMessage());
			}
			if(!cm.isNullable() && val == null && !ignoreNull) {
				VolidateFailed vf = VolidateFailed.createError("%s cannot be null", cm.getName());
				vf.put(cm.getName(), "cannot be null");
				vf.throwSelf();
			} else if(val != null) {
				if(!cm.getPattern().equals("")) {
					if(val.toString().matches(cm.getPattern())) {
						VolidateFailed vf = VolidateFailed.createError("%s does not matches %s", cm.getName(),cm.getPattern());
						vf.put(cm.getName(), cm.getPattern());
						vf.throwSelf();
					}
				}
			}	
		}
		
	}
	public <E extends OOEntity> int insert(List<E> entities) {
		OOAssert.warnning(entities != null && !entities.isEmpty(),"不可以插入null");
		Class<?> tbl = entities.get(0).getClass();
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		OOAssert.fatal(tm != null,"无法找到表：%s",tbl.getSimpleName());
		
		for(E entity:entities) {
			validate(tm,entity,false);
		}
		
		TblIdNameVal<E> idNameVal = new TblIdNameVal<>(tm.getColumnMetas(),entities);
		for(int i = 0; i < idNameVal.ids.size(); i++) {
			if(idNameVal.getIdAt(i) == null) {
				Object id = idGenerator.next(tbl);
				idNameVal.setIdAt(i, id);
			}
		}

		idNameVal.dropAllNullColumns();
		List<String> names = idNameVal.getIdNames();
		List<List<Object>> valsList = idNameVal.getIdValsList();
		String stub = OOUtils.genStub("?",",",names.size());
		String sql = String.format("INSERT INTO %s(%s) VALUES(%s);", tm.getTable(),String.join(",", names),stub);
		
		OOLog.debug("%s\n",sql);
		
		int[] cnts = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				List<Object> vals = valsList.get(i);
				for(int vi = 0;  vi < vals.size(); vi++) {
					ps.setObject(vi+1, vals.get(vi));
				}
				OOLog.debug("%s values:%s \n",sql, OOUtils.toJSON(vals));
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

	private <E extends OOEntity> int update(E entity,boolean ignoreNull) {
		OOAssert.warnning(entity != null,"不可以插入null");
		Class<?> tbl = entity.getClass();
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		validate(tm,entity,ignoreNull);
		OOAssert.fatal(tm != null,"无法找到表：%s",tbl.getSimpleName());
		TblIdNameVal<E> idNameVal = new TblIdNameVal<>(tm.getColumnMetas(),Arrays.asList(entity));
		Object id = idNameVal.getIdAt(0);
		OOAssert.err(id != null,"ID 不能为NULL");
		/** ignore rm */
		idNameVal.dropColumns("rm");
		if(ignoreNull) {
			idNameVal.dropAllNullColumns();
		}
		List<String> names = idNameVal.getNames();
		List<Object> vals = idNameVal.getValsList().get(0);
		String sql = String.format("UPDATE %s SET %s=? WHERE id=? and rm = false;", tm.getTable(),String.join("=?,", names));
		vals.add(id);
		return jdbcTemplate.update(sql, vals.toArray());
	}


	public <E extends OOEntity> int update(E entity) {
		return update(entity,false);
	}
	
	public <E extends OOEntity> int updateIgnoreNull(E entity) {
		return update(entity,true);	
	}
	
	
	public <E extends OOEntity> int updateByTpl(Class<E> tbl, UpdateTpl<E> tpl) {
		OOAssert.warnning(tpl.getId() != null && tpl != null,"Are you sure to update a null value?");
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());	
		OOAssert.fatal(tm != null,"无法找到表：%s",tbl.getSimpleName());
		//validate(tm,tpl,false);
		String setTpl = tpl.getSetTpl();
		List<Object> vals = new ArrayList<>(tpl.getArgs().size()+1);
		vals.addAll(tpl.getArgs());
		vals.add(tpl.getId());
		String sql = String.format("UPDATE %s SET %s WHERE id=? and rm=false;", tm.getTable(),setTpl);
		return jdbcTemplate.update(sql, vals.toArray());
	}
	
	public <E extends OOEntity> int updateByTplCnd(Class<E> tbl,UpdateTpl<E> tpl,Cnd<E> cnd) {
		OOAssert.warnning(tpl != null,"Are you sure to update a null value?");
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());	
		OOAssert.fatal(tm != null,"无法找到表：%s",tbl.getSimpleName());
		//validate(tm,tpl,false);
		List<Object> vals = new ArrayList<>();
		vals.addAll(tpl.getArgs());
		List<Object> sqlArgs = new ArrayList<>();
		String cndSql = cnd.whereSql(sqlArgs);
		if(cndSql.isEmpty()) {
			OOAssert.warnning("查询条件不能为空");
		}
		vals.addAll(sqlArgs);
		String sql = String.format("UPDATE %s SET %s WHERE (%s) and rm=false;", tm.getTable(),tpl.getSetTpl(),cndSql);
		return jdbcTemplate.update(sql, vals.toArray());
	}

	public <E,ID> int removeById(Class<E> tbl,ID id) {
		if(id == null) return 0;
		OOAssert.warnning(id != null,"ID不能为null");
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		OOAssert.fatal(tm != null,"无法找到表：%s",tbl.getSimpleName());
		String sql = String.format("UPDATE %s SET rm=true WHERE id=?;", tm.getTable());
		return jdbcTemplate.update(sql, id);
	}

	public <E,ID> int removeByIds(Class<E> tbl, List<ID> ids) {
		if(ids == null || ids.isEmpty()) return 0;
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		String stub = OOUtils.genStub("?",",",ids.size());
		String sql = String.format("UPDATE %s SET rm=true WHERE id in (%s);", tm.getTable(),stub);
		return jdbcTemplate.update(sql, ids.toArray());
	}
	public <E extends OOEntity> int remove(Class<E> tbl, Cnd<E> cnd) {
		if(cnd == null) return 0;
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		List<Object> sqlArgs = new ArrayList<>();
		String whereCnd = cnd.whereSql(sqlArgs);
		if(whereCnd.equals("")) {
			return 0;
		}
		String sql = String.format("UPDATE %s SET rm=true WHERE (%s);", tm.getTable(),whereCnd);
		return jdbcTemplate.update(sql, sqlArgs.toArray());
	}
	public <E,ID> int deleteById(Class<E> tbl, ID id) {
		if(id == null) return 0;
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		String sql = String.format("DELETE FROM %s WHERE id=? and rm = true;", tm.getTable());
		return jdbcTemplate.update(sql, id);
	}
	public <E,ID> int deleteByIds(Class<E> tbl, List<ID> ids) {
		if(ids == null || ids.isEmpty()) return 0;
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		String stub = OOUtils.genStub("?", ",", ids.size());
		String sql = String.format("DELETE FROM %s WHERE id in (%s) and (rm = true);", tm.getTable(),stub);
		return jdbcTemplate.update(sql, ids.toArray());
	}
	
	public <E extends OOEntity> int delete(Class<E> tbl, Cnd<E> cnd) {
		if (cnd == null) return 0;
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		List<Object> sqlArgs = new ArrayList<>();
		String whereCnd = cnd.whereSql(sqlArgs);
		if (whereCnd.equals("")) {
			return 0;
		}
		String sql = String.format("DELETE FROM %s WHERE (rm = true) and (%s);", tm.getTable(), whereCnd);
		return jdbcTemplate.update(sql, sqlArgs.toArray());
	}


	public <E extends OOEntity> long count(Class<E> tbl) {
		return count(tbl,null,new Cnd<E>(tbl));
	}
	
	public <E extends OOEntity> long count(Class<E> tbl, Cnd<E> cnd) {
		return count(tbl,null,cnd);
	}
	
	public <E extends OOEntity> long count(Class<E> tbl, SelectTpl<E> tpl, Cnd<E> cnd) {
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		OOAssert.fatal(tm != null,"无法找到表：%s",tbl.getSimpleName());
		List<Object> sqlArgs = new ArrayList<>();
		String sql = cnd.countSql(tm, tpl, sqlArgs);
		OOLog.debug(sql);
		OOLog.debug(sqlArgs.toString());
		return jdbcTemplate.queryForObject(sql,sqlArgs.toArray(new Object[0]), Long.class);
	}

	public <E extends OOEntity> Page<E> find(Class<E> tbl,Cnd<E> cnd) {
		return find(tbl,null,cnd);
	}
	@SuppressWarnings("unchecked")
	public <E extends OOEntity> Page<E> find(Class<E> tbl,SelectTpl<E> tpl,Cnd<E> cnd) {
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		OOAssert.fatal(tm != null,"无法找到表：%s",tbl.getSimpleName());
		RowMapper<E> rowMapper = null;
		String mapperKey = tbl.getSimpleName();
		if(!tableToRowMapper.containsKey(mapperKey)) {
			rowMapper = genRowMapper(tbl,tm);
			tableToRowMapper.put(mapperKey, rowMapper);
		}else {
			rowMapper = tableToRowMapper.get(mapperKey);	
		}
		Page<E> page = new Page<E>();
		if(cnd.getPage() == null || cnd.getPage() <= 0) {
			page.setPage(cnd.getPage());
			if(cnd.getPage() == null || cnd.getPage() == 0) {
				cnd.setPage(1);
				page.setPage(1);
			}else {
				cnd.setPage(Math.abs(cnd.getPage()));
			}
			page.setTotal(count(tbl,tpl,cnd));
		}
		if(cnd.getPageSize() == null) {
			cnd.setPageSize(OOConfig.PAGE_SIZE_DEFAULT);
			page.setPageSize(OOConfig.PAGE_SIZE_DEFAULT);
		}else if(cnd.getPageSize() > OOConfig.PAGE_SIZE_MAX) {
			cnd.setPageSize(OOConfig.PAGE_SIZE_MAX);
			page.setPageSize(OOConfig.PAGE_SIZE_MAX);
		}
		if(page.getTotal() == null || page.getTotal() > 0) {
			List<Object> sqlArgs = new ArrayList<>();
			String sql = cnd.pageSql(tm,tpl,sqlArgs);
			OOLog.debug(sql);
			OOLog.debug(sqlArgs.toString());
			List<E> data = jdbcTemplate.query(sql,sqlArgs.toArray(), rowMapper);
			page.setData(data);
		}
		return page;
	}

	public <E extends OOEntity> E fetch(Class<E> tbl,SelectTpl<E> tpl,Cnd<E> cnd) {
		if(cnd == null) {
			cnd = new Cnd<E>(tbl);
		}
		cnd.setPage(1);
		cnd.setPageSize(1);
		Page<E> page = find(tbl,tpl,cnd);
		if(page.getData().size() > 0) {
			return page.getData().get(0);
		}
		return null;
	}
	
	public <E extends OOEntity> void download(Class<E> tbl,SelectTpl<E> tpl,Cnd<E> cnd, Consumer<E> consumer) {
		TableMeta tm = tableToTableMeta.get(tbl.getSimpleName());
		if(tm == null) {
			return ;
		}
		List<Object> args = new ArrayList<>();
		StringBuffer sql = cnd.wholeSql(tm, tpl, args);
		jdbcTemplate.query(sql.toString(), args.toArray(new Object[0]), new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				E row = createBy(tbl, tm,rs);
				consumer.accept(row);
			}
		});
	}
	
	public <E extends OOEntity,ID> List<E> findByIds(Class<E> tbl, List<ID> ids) {
		if(ids == null || ids.isEmpty()) {
			return new ArrayList<E>();
		}
		Cnd<E> cnd = new Cnd<E>(tbl);
		cnd.setPage(1);
		cnd.setPageSize(ids.size());
		cnd.in(ids.toArray(new Object[0])).setId(null);
		Page<E> page = find(tbl,null,cnd);
		return page.getData();
	}
}

