package cn.mx.app.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.onceonly.db.BaseEntity;

@Entity
public class GoodsShipping extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private long goodsOrderId;
	@Column(nullable = false)
	private long buyerId;
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
