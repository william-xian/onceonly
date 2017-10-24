package io.onceonly.db.tbl;

import io.onceonly.db.annotation.Col;
import io.onceonly.db.annotation.Tbl;

@Tbl
public final class OODDMeta {
	@Col(size=32,nullable=true)
	private String tag;
	@Col(nullable=true,colDef="TEXT")
	private String path;
	@Col(nullable=false,colDef="TEXT")
	private String content;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Long createtime) {
		this.createtime = createtime;
	}

}
