package cn.mx.app.entity;

import io.onceonly.db.BaseEntity;
import io.onceonly.db.annotation.Col;
import io.onceonly.db.annotation.Tbl;

@Tbl
public class GoodsShipping extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Col(ref=GoodsOrder.class, nullable = false)
	private long goodsOrderId;
	@Col(ref=UserChief.class,nullable = false)
	private long buyerId;
	@Col(ref=UserChief.class,nullable = false)
	private long receiverId;
	@Col(size = 255)
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
