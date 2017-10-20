package cn.mx.app.entity;

import io.onceonly.db.BaseEntity;
import io.onceonly.db.annotation.Col;
import io.onceonly.db.annotation.Tbl;

@Tbl
public class UserChief extends BaseEntity{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    @Col(nullable = false, length=32,unique=true)
	private String name;
    @Col(nullable = true, length=64)
    private String passwd;
    @Col(nullable = true, length=255)
    private String avatar;
    @Col(nullable = true, length=255)
    private Integer genre;
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public Integer getGenre() {
		return genre;
	}
	public void setGenre(Integer genre) {
		this.genre = genre;
	}
}
