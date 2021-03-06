package io.onceonly.db.tbl;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.onceonly.db.annotation.Col;
import io.onceonly.db.annotation.OId;

/**
 * @author Administrator
 *
 */
public abstract class OOEntity {
	@OId
    @Col(nullable = false)
	protected Long id;
    @Col(colDef="boolean default false",nullable = false)
	protected transient Boolean rm;
	/** 用户存储额外数据，如 聚合函数 */
	protected Map<String,Object> extra;
	
	public OOEntity() {
	}
	public void init() {
	}
	public void initId(){
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Boolean getRm() {
		return rm;
	}
	public void setRm(Boolean rm) {
		this.rm = rm;
	}
	public Map<String,Object> put(String key,Object val) {
		if(extra == null) {
			extra = new HashMap<String,Object>();
		}
		extra.put(key, val);
		return extra;
	}
	public Map<String, Object> getExtra() {
		return extra;
	}
	public void setExtra(Map<String, Object> extra) {
		this.extra = extra;
	}

	private static final Gson GSON = new GsonBuilder().serializeNulls().create();
    @Override
	public String toString(){
    	return GSON.toJson(this);
	}
}
