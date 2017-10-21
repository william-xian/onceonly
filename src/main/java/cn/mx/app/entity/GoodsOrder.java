package cn.mx.app.entity;

import io.onceonly.db.BaseEntity;
import io.onceonly.db.annotation.Col;
import io.onceonly.db.annotation.Tbl;

@Tbl
public class GoodsOrder extends BaseEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Col(ref=UserChief.class)
	private long userId;
	@Col(ref=Goods.class,nullable = false)
	private long goodsId;
	@Col(nullable = false)
	private Integer amount;
	@Col(nullable = false)
	private Integer money;
	@Col(nullable = false)
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
