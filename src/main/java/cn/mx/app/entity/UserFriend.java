package cn.mx.app.entity;

import io.onceonly.db.annotation.Col;
import io.onceonly.db.annotation.Tbl;
import io.onceonly.db.tbl.BaseEntity;

@Tbl
public class UserFriend extends BaseEntity<Long>{
	@Col(ref=UserChief.class,nullable = false)
	private Long userId;
	@Col(ref=UserChief.class,nullable = false)
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
