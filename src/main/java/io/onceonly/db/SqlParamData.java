package io.onceonly.db;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.onceonly.util.OOUtils;

public 
class SqlParamData {
	DDMeta main;
	Set<DDMeta> depends;
	List<String> dependNamePaths;
	Set<DDMeta> supplements;
	List<String> supplementNamePaths;
	Map<String,DDMeta> namePathToMeta;
	String sql;
	public DDMeta getMain() {
		return main;
	}
	public void setMain(DDMeta main) {
		this.main = main;
	}
	public Set<DDMeta> getDepends() {
		return depends;
	}
	public void setDepends(Set<DDMeta> depends) {
		this.depends = depends;
	}
	public List<String> getDependNamePaths() {
		return dependNamePaths;
	}
	public void setDependNamePaths(List<String> dependNamePaths) {
		this.dependNamePaths = dependNamePaths;
	}
	public Set<DDMeta> getSupplements() {
		return supplements;
	}
	public void setSupplements(Set<DDMeta> supplements) {
		this.supplements = supplements;
	}
	public List<String> getSupplementNamePaths() {
		return supplementNamePaths;
	}
	public void setSupplementNamePaths(List<String> supplementNamePaths) {
		this.supplementNamePaths = supplementNamePaths;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public Map<String, DDMeta> getNamePathToMeta() {
		return namePathToMeta;
	}
	public void setNamePathToMeta(Map<String, DDMeta> namePathToMeta) {
		this.namePathToMeta = namePathToMeta;
	}
	@Override
	public String toString() {
		return OOUtils.toJSON(this);
	}
	
}