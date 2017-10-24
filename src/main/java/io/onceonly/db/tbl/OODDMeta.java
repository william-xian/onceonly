package io.onceonly.db.tbl;

import io.onceonly.db.annotation.Col;
import io.onceonly.db.annotation.Tbl;

@Tbl
public final class OODDMeta extends BaseEntity {
	@Col(size=32,nullable=true)
	private String tag;
	@Col(nullable=true,colDef="TEXT")
	private String path;
	@Col(nullable=false,colDef="TEXT")
	private String val;
	@Col(nullable=true)
	private Long createtime;
	
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
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
