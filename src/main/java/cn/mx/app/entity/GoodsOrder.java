package cn.mx.app.entity;

import io.onceonly.db.annotation.Col;
import io.onceonly.db.annotation.Tbl;
import io.onceonly.db.tbl.OOEntity;

@Tbl
public class GoodsOrder extends OOEntity<Long>{
	@Col(ref=UserChief.class)
	private Long userId;
	@Col(ref=Goods.class,nullable = false)
	private Long goodsId;
	@Col(nullable = false)
	private Integer amount;
	@Col(nullable = false)
	private Integer money;
	@Col(nullable = false)
	private Long ctime;
	public long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
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
