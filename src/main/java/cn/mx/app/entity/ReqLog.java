package cn.mx.app.entity;

import io.onceonly.db.BaseEntity;
import io.onceonly.db.annotation.Col;
import io.onceonly.db.annotation.Tbl;

@Tbl
public class ReqLog extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Col(length = 255,nullable = true)
	private String curUser;
	@Col(length = 8,colDef="varchar(8) default ''",nullable = false)
	private String method;
	@Col(length = 255,nullable = true)
	private String params;
	@Col(length = 16,nullable = true)
	private String remoteAddr;
	@Col(length = 32,nullable = true)
	private String sessionId;
	@Col(length = 255,nullable = true)
	private String uri;
	@Col(nullable = false)
	private Long beginTime;
	@Col(nullable = false)
	private Long requestTime;
	@Col(colDef="TEXT",nullable = true) 
	private String result;
	public String getCurUser() {
		return curUser;
	}
	public void setCurUser(String curUser) {
		this.curUser = curUser;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getRemoteAddr() {
		return remoteAddr;
	}
	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public Long getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Long beginTime) {
		this.beginTime = beginTime;
	}
	public Long getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(Long requestTime) {
		this.requestTime = requestTime;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
}
