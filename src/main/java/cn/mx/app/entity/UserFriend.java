package cn.mx.app.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.onceonly.db.BaseEntity;

@Entity
public class UserFriend extends BaseEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(length = 32,nullable = false)
	private String userId;
	@Column(length = 32,nullable = false)
	private String friendId;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFriendId() {
		return friendId;
	}
	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}
}
