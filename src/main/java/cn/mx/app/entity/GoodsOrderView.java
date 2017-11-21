package cn.mx.app.entity;

import io.onceonly.db.annotation.Col;
import io.onceonly.db.annotation.TblView;

@TblView
public class GoodsOrderView extends GoodsOrder{
	@Col(refBy="userId-UserChief.name")
	private String userName;
	@Col(refBy="goodsId-Goods.name")
	private String goodsName;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	
}
