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

	@Column(length = 32,nullable = false)
	private String goodsOrderId;
	@Column(length = 32,nullable = false)
	private String receiverId;
	@Column(length = 255)
	private String addr;
	
	public String getGoodsOrderId() {
		return goodsOrderId;
	}
	public void setGoodsOrderId(String goodsOrderId) {
		this.goodsOrderId = goodsOrderId;
	}
	public String getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
}
