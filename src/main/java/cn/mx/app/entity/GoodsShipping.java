package cn.mx.app.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.onceonly.db.BaseEntity;
import io.onceonly.db.annotation.RefFrom;

@Entity
public class GoodsShipping extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@RefFrom(entity=GoodsOrder.class)
	@Column(nullable = false)
	private long goodsOrderId;
	@Column(nullable = false)
	@RefFrom(entity=UserChief.class,alias="buyer")
	private long buyerId;
	@RefFrom(entity=UserChief.class,alias="receiver")
	@Column(nullable = false)
	private long receiverId;
	@Column(length = 255)
	private String addr;
	
	public long getGoodsOrderId() {
		return goodsOrderId;
	}
	public void setGoodsOrderId(long goodsOrderId) {
		this.goodsOrderId = goodsOrderId;
	}
	
	public long getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(long buyerId) {
		this.buyerId = buyerId;
	}
	public long getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(long receiverId) {
		this.receiverId = receiverId;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
}
