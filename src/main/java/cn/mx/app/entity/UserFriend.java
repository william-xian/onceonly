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

	@Column(nullable = false)
	private long userId;
	@Column(nullable = false)
	private long friendId;
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getFriendId() {
		return friendId;
	}
	public void setFriendId(long friendId) {
		this.friendId = friendId;
	}
}
