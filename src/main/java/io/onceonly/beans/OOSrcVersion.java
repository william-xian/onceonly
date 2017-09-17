package io.onceonly.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import io.onceonly.util.OOUtils;

@Entity
public class OOSrcVersion implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
    @Column(nullable = false, length=64)
	private String id;
	@Column(length=32,nullable=true)
	private String tag;
	@Column(nullable=false,columnDefinition="TEXT")
	private String content;
	@Column(nullable=true)
	private Long createtime;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
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

	public String toString() {
		return OOUtils.toJSON(this);
	}
}
