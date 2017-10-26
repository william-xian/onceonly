package io.onceonly.db.meta;

import java.util.ArrayList;
import java.util.List;

import io.onceonly.db.annotation.ConstraintType;
import io.onceonly.util.OOAssert;
import io.onceonly.util.OOUtils;

public class ConstraintMeta {
	public static final String PRIMARY_KEY = "PRIMARY KEY";
	public static final String FOREIGN_KEY = "FOREIGN KEY";
	public static final String UNIQUE = "UNIQUE";
	public static final String INDEX = "INDEX";
	String name;
	ConstraintType type;
	String using;
	String table;
	String refTable;
	List<String> columns;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ConstraintType getType() {
		return type;
	}
	public void setType(ConstraintType type) {
		this.type = type;
	}
	public String getUsing() {
		return using;
	}
	public void setUsing(String using) {
		this.using = using;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getRefTable() {
		return refTable;
	}
	public void setRefTable(String refTable) {
		this.refTable = refTable;
	}
	public List<String> getColumns() {
		return columns;
	}
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
	
	public String genName() {
		String cName = null;
		switch(type) {
		case PRIMARY_KEY:
			cName = String.format("pk_%s_%s", table,String.join("_", columns));
			break;
		case FOREGIN_KEY:
			cName = String.format("fk_%s_%s", table,String.join("_", columns));
			break;
		case INDEX:
			cName = String.format("nd_%s_%s", table,String.join("_", columns));
			break;
		case UNIQUE:
			cName = String.format("nq_%s_%s", table,String.join("_", columns));
			break;
			default:
				OOAssert.fatal("不存在：%s", OOUtils.toJSON(this));
				break;		
		}
		return cName;
		
	}

	public String genDef() {
		String def = null;
		switch(type) {
		case PRIMARY_KEY:
			def = String.format("PRIMARY KEY (%s)", String.join(",", columns));
			break;
		case FOREGIN_KEY:
			def = String.format("FOREIGN KEY (%s) REFERENCES %s(%s)", String.join(",", columns),refTable,String.join(",", columns));
			break;
		case UNIQUE:
			def = String.format("UNIQUE (%s)",String.join("_", columns));
			break;
		case INDEX:
			def = String.format("ON %s%s (%s)", table,using!=null?(" USING "+using):"",String.join("_", columns));
			break;
		default:
				OOAssert.fatal("不存在：%s", OOUtils.toJSON(this));
				break;		
		}
		return def;
		
	}
	
	public String addSql() {
		String cName = (name==null?genName():name);
		String def = genDef();
		if (type == ConstraintType.INDEX) {
			return String.format("CREATE INDEX %s %s;", cName, def);
		} else {
			return String.format("ALTER TABLE %s ADD CONSTRAINT %s %s;", table, cName, def);
		}
	}
	
	public String dropSql() {
		String cName = (name==null?genName():name);
		return String.format("ALTER TABLE %s DROP CONSTRAINT %s_%s;",table, table,cName);
	}
	
	public static List<String> addConstraintSql(List<ConstraintMeta> cms) {
		List<String> sqls = new ArrayList<>();
		for(ConstraintMeta cm:cms) {
			sqls.add(cm.addSql());
		}
		return sqls;
	}
	public static List<String> dropConstraintSql(List<ConstraintMeta> cms) {
		List<String> sqls = new ArrayList<>();
		for(ConstraintMeta cm:cms) {
			sqls.add(cm.dropSql());
		}
		return sqls;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columns == null) ? 0 : columns.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((refTable == null) ? 0 : refTable.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((using == null) ? 0 : using.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConstraintMeta other = (ConstraintMeta) obj;
		if (columns == null) {
			if (other.columns != null)
				return false;
		} else if (!columns.equals(other.columns))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (refTable == null) {
			if (other.refTable != null)
				return false;
		} else if (!refTable.equals(other.refTable))
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		if (type != other.type)
			return false;
		if (using == null) {
			if (other.using != null)
				return false;
		} else if (!using.equals(other.using))
			return false;
		return true;
	}	
}
