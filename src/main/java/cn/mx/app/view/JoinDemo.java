package cn.mx.app.view;

import cn.mx.app.entity.Goods;
import cn.mx.app.entity.GoodsDesc;
import cn.mx.app.entity.GoodsOrder;
import cn.mx.app.entity.GoodsShipping;
import cn.mx.app.entity.UserChief;
import cn.mx.app.entity.UserProfile;
import cn.mx.app.entity.Wallet;
import io.onceonly.db.annotation.Join;
import io.onceonly.db.annotation.VColumn;
import io.onceonly.db.annotation.VTable;

/**
 * @author Administrator
 *
 */
@VTable(
	mainTable = UserChief.class,alias ="buyer",
	joins = {
		@Join(left=Wallet.class,alias="uw"),
		@Join(left=UserProfile.class,alias="up"),
		@Join(left=GoodsOrder.class,alias="go",cnd="go.userId=buyer.id"),
		@Join(left=GoodsShipping.class,alias="gs",tAlias="go",cnd="gs.goodsOrderId=go.id"),
		@Join(left=UserChief.class,alias="receiver",tAlias="gs",cnd="gs.receiverId=receiver.id"),
		@Join(left=Goods.class,alias="g",tAlias="go",cnd = "go.goodsId=g.id"),
		@Join(left=GoodsDesc.class,alias="gd",tAlias="g")
	}
)
public class JoinDemo {
	@VColumn("buyer.name")
	private String buyerName;
	@VColumn("g.name")
	private String goodsName;
	@VColumn("gd.saled")
	private Integer saled;
	@VColumn("receiver.name")
	private String receiverName;
	@VColumn("uw.balance")
	private int buyerBalance;
	
	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public Integer getSaled() {
		return saled;
	}

	public void setSaled(Integer saled) {
		this.saled = saled;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public int getBuyerBalance() {
		return buyerBalance;
	}

	public void setBuyerBalance(int buyerBalance) {
		this.buyerBalance = buyerBalance;
	}

}
