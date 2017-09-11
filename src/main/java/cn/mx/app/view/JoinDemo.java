package cn.mx.app.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.onceonly.db.annotation.Join;
import io.onceonly.db.annotation.VColumn;
import io.onceonly.db.annotation.VTable;
import io.onceonly.util.Tuple3;
import cn.mx.app.entity.UserChief;
import cn.mx.app.entity.UserProfile;
import cn.mx.app.entity.Wallet;

/**
 * @author Administrator
 *
 */
@VTable(
	mainTable = UserChief.class,alias ="uc",
	left = {
		@Join(from=UserProfile.class,fAlias="up", to=UserChief.class),
		@Join(from=Wallet.class,fAlias="uw", to=UserChief.class)
	},
	right = {
			@Join(from=UserChief.class,fAlias="uw", to=Wallet.class)
	}
)
public class JoinDemo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@VColumn("uc.id")
	private String userId;
	@VColumn("uc.name")
	private String name;
	@VColumn("up.nickname")
	private String nickname;
	@VColumn("up.phone")
	private String phone;
	@VColumn("uw.balance")
	private int balance;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	
	public static void main(String[] args) {
		process(JoinDemo.class);
	}
	
	public static void process(Class<?> clazz) {
		VTable vt = clazz.getAnnotation(VTable.class);
		/** Entity -> <别名,[我连接谁],[谁连接我]>*/
		HashMap<Class<?>,Tuple3<String,List<Class<?>>,List<Class<?>>>> map = new HashMap<>();
		Tuple3<String,List<Class<?>>,List<Class<?>>> tuple = new Tuple3<>();
		map.put(vt.mainTable(), tuple);
		for(Join lj:vt.left()) {
			tuple = map.get(lj.from());
			if(tuple == null) {
				tuple = new Tuple3<>();
				tuple.a = lj.fAlias();
				map.put(lj.from(), tuple);
			}
			if(tuple.c == null) {
				tuple.c = new ArrayList<>();
			}
			tuple.c.add(lj.to());

			tuple = map.get(lj.to());
			if(tuple == null) {
				tuple = new Tuple3<>();
				map.put(lj.to(), tuple);
			}
			if(tuple.b == null) {
				tuple.b = new ArrayList<>();
			}
			tuple.b.add(lj.to());
		}
		
	}
	
}
