package cn.mx.app.entity;

import io.onceonly.db.annotation.TblView;
import io.onceonly.db.annotation.VCol;

@TblView
public class GoodsOrderView extends GoodsOrder{
	@VCol(ref="name",refBy="userId")
	private String userName;
	@VCol(ref="name",refBy="goodsId")
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
