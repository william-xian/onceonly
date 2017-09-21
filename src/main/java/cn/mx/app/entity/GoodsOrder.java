package cn.mx.app.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.onceonly.db.BaseEntity;
import io.onceonly.db.annotation.RefFrom;

@Entity
public class GoodsOrder extends BaseEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@RefFrom(entity=UserChief.class)
	@Column(nullable = false)
	private long userId;
	@Column(nullable = false)
	@RefFrom(entity=Goods.class)
	private long goodsId;
	@Column(nullable = false)
	private Integer amount;
	@Column(nullable = false)
	private Integer money;
	@Column(nullable = false)
	private Long ctime;
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(long goodsId) {
		this.goodsId = goodsId;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public Integer getMoney() {
		return money;
	}
	public void setMoney(Integer money) {
		this.money = money;
	}
	public Long getCtime() {
		return ctime;
	}
	public void setCtime(Long ctime) {
		this.ctime = ctime;
	}
}
