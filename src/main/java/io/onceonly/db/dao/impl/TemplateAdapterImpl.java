package io.onceonly.db.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.onceonly.db.dao.TemplateAdapter;
import io.onceonly.db.meta.ColumnMeta;
import io.onceonly.db.meta.TableMeta;
import io.onceonly.util.OOLog;
import io.onceonly.util.Tuple2;

public class TemplateAdapterImpl implements TemplateAdapter{
	
	private Map<String,TableMeta> tableToTableMeta;
	
	public TemplateAdapterImpl(Map<String,TableMeta> tableToTableMeta)  {
		this.tableToTableMeta = tableToTableMeta;
	}
	
	@Override
	public <E> Tuple2<String[], Object[]> adapterForUpdate(E tmpl) {
		if(tmpl == null) return null;
		TableMeta tm = tableToTableMeta.get(tmpl.getClass().getSimpleName());
		if(tm == null) {
			OOLog.warnning("无法找到 TableMeta:%s", tmpl.getClass());
			return null;
		}
		List<String> names = new ArrayList<>();
		List<Object> vals = new ArrayList<>();
		for(ColumnMeta cm:tm.getColumnMetas()) {
			try {
				Object val = cm.getField().get(tmpl);
				//TODO 没有处理val值
				if(val != null) {
					names.add(cm.getName());
					vals.add(val);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				OOLog.warnning("%s", e.getMessage());
			}
		}
		return new Tuple2<String[], Object[]>(names.toArray(new String[0]),vals.toArray(new Object[0]));
	}

	@Override
	public <E> Tuple2<String[], Object[]> adapterForSelect(E tmpl) {
		if(tmpl == null) return null;
		TableMeta tm = tableToTableMeta.get(tmpl.getClass().getSimpleName());
		if(tm == null) {
			OOLog.warnning("无法找到 TableMeta:%s", tmpl.getClass());
			return null;
		}
		List<String> names = new ArrayList<>();
		List<Object> vals = new ArrayList<>();
		for(ColumnMeta cm:tm.getColumnMetas()) {
			try {
				Object val = cm.getField().get(tmpl);
				//TODO 没有处理val值
				if(val != null) {
					names.add(cm.getName());
					vals.add(val);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				OOLog.warnning("%s", e.getMessage());
			}
		}
		return new Tuple2<String[], Object[]>(names.toArray(new String[0]),vals.toArray(new Object[0]));
	}

	@Override
	public <E> Tuple2<String[], Object[]> adapterForWhere(E tmpl) {
		if(tmpl == null) return null;
		TableMeta tm = tableToTableMeta.get(tmpl.getClass().getSimpleName());
		if(tm == null) {
			OOLog.warnning("无法找到 TableMeta:%s", tmpl.getClass());
			return null;
		}
		List<String> names = new ArrayList<>();
		List<Object> vals = new ArrayList<>();
		for(ColumnMeta cm:tm.getColumnMetas()) {
			try {
				Object val = cm.getField().get(tmpl);
				//TODO 没有处理val值
				if(val != null) {
					names.add(cm.getName());
					vals.add(val);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				OOLog.warnning("%s", e.getMessage());
			}
		}
		return new Tuple2<String[], Object[]>(names.toArray(new String[0]),vals.toArray(new Object[0]));
	}

}
