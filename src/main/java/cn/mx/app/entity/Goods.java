package cn.mx.app.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.onceonly.db.BaseEntity;

@Entity
public class Goods extends BaseEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(length = 32,nullable = true)
	private String name;
	
	@Column(nullable = true)
	private Integer genre;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getGenre() {
		return genre;
	}
	public void setGenre(Integer genre) {
		this.genre = genre;
	}
}
