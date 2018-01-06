package io.onceonly.db.tbl;

import io.onceonly.db.annotation.Col;
import io.onceonly.db.annotation.Tbl;

@Tbl
public class OOTableMeta extends OOEntity{
	@Col(size=32,nullable=true)
	private String name;
	@Col(nullable=false,colDef="TEXT")
	private String val;
	@Col(nullable=true)
	private Long createtime;
	
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
	public Long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Long createtime) {
		this.createtime = createtime;
	}
	
}
