package io.onceonly.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import io.onceonly.util.DlsUtils;

@Entity
public class I18n implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 超级组/lang_名字
	 */
	@Id
    @Column(nullable = false, length=64)
	private String id;
	@Column(length=255,nullable=false)
	private String name;
	@Column(length=32,nullable=true)
	private String val;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	public String toString() {
		return DlsUtils.toJSON(this);
	}
}
