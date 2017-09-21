package cn.mx.app.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.onceonly.db.BaseEntity;
import io.onceonly.db.annotation.RefFrom;

@Entity
public class UserFriend extends BaseEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	@RefFrom(entity=UserChief.class,alias="user")
	private Long userId;
	@RefFrom(entity=UserChief.class,alias="friend")
	@Column(nullable = false)
	private Long friendId;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getFriendId() {
		return friendId;
	}
	public void setFriendId(Long friendId) {
		this.friendId = friendId;
	}
}
