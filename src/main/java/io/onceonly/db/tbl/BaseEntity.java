package io.onceonly.db.tbl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.onceonly.db.annotation.Col;
import io.onceonly.db.annotation.OId;

/**
 * @author Administrator
 *
 */
public abstract class BaseEntity<ID>{
	@OId
    @Col(nullable = false)
	protected ID id;
	@JsonIgnore
    @Col(nullable = false)
	protected boolean del = false;
	
	public BaseEntity() {
	}
	public void init() {
		
	}
	public void initId(){
		
	}
	public ID getId() {
		return id;
	}
	public void setId(ID id) {
		this.id = id;
	}
	public boolean isDel() {
		return del;
	}

	public void setDel(boolean del) {
		this.del = del;
	}
	private static final Gson GSON = new GsonBuilder().serializeNulls().create();
    @Override
	public String toString(){
    	return GSON.toJson(this);
	}
}
