package cn.mx.app.entity;

import io.onceonly.db.annotation.Col;
import io.onceonly.db.annotation.Tbl;
import io.onceonly.db.tbl.BaseEntity;

@Tbl
public class Goods extends BaseEntity{
	
	@Col(size = 32,nullable = true)
	private String name;
	
	@Col(nullable = true)
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
