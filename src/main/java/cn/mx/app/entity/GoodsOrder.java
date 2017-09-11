package cn.mx.app.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.onceonly.db.BaseEntity;

@Entity
public class GoodsOrder extends BaseEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(length = 32,nullable = false)
	private String userId;
	@Column(length = 32,nullable = false)
	private String goodsId;
	@Column(nullable = false)
	private Integer amount;
	@Column(nullable = false)
	private Integer money;
	@Column(nullable = false)
	private Long ctime;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
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
