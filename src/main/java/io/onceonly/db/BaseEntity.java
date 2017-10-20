package io.onceonly.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.onceonly.db.annotation.Col;
import io.onceonly.db.annotation.OId;

/**
 * @author Administrator
 *
 */
@Entity
@Table
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class BaseEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.TABLE)  
    @Column(nullable = false, length=32)
	@OId
	protected Long id;
	@JsonIgnore
    @Column(nullable = false)
    @Col(nullable = false)
    private boolean _del = false;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public boolean is_del() {
		return _del;
	}
	public void set_del(boolean _del) {
		this._del = _del;
	}
	private static final Gson GSON = new GsonBuilder().serializeNulls().create();
    @Override
	public String toString(){
    	return GSON.toJson(this);
	}
}
