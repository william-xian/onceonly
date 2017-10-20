package cn.mx.app.entity;

import io.onceonly.db.BaseEntity;
import io.onceonly.db.annotation.Col;
import io.onceonly.db.annotation.Tbl;

@Tbl(extend=Goods.class)
public class GoodsDesc extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Col(length = 255,nullable = true)
	private String content;
	@Col(nullable = false)
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
