package cn.mx.app.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.onceonly.db.BaseEntity;
import io.onceonly.db.annotation.Extend;

@Entity
@Extend(entity=UserChief.class)
public class UserProfile extends BaseEntity{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    @Column(nullable = false, length=20)
	private String nickname;
    @Column(nullable = false)
	private Boolean gender;
    @Column(nullable = false, length=16)
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
