package cn.mx.app.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.onceonly.db.BaseEntity;
import io.onceonly.db.annotation.Extend;

@Entity
@Extend(entity=Goods.class)
public class GoodsDesc extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(length = 255,nullable = true)
	private String content;
	@Column(nullable = false)
	private Integer saled;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getSaled() {
		return saled;
	}
	public void setSaled(Integer saled) {
		this.saled = saled;
	}
	
}
