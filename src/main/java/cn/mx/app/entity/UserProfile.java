package cn.mx.app.entity;

import io.onceonly.db.annotation.Col;
import io.onceonly.db.annotation.Tbl;

@Tbl(extend=UserChief.class)
public class UserProfile extends BaseEntity{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    @Col(nullable = false, size=20)
	private String nickname;
    @Col(nullable = false)
	private Boolean gender;
    @Col(nullable = false, size=16)
	private String phone;
    
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Boolean getGender() {
		return gender;
	}
	public void setGender(Boolean gender) {
		this.gender = gender;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
}
