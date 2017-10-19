package io.onceonly.db;

import org.junit.Test;

public class DDEngineTest {

	/**
	User {id,name}
	UserAddr{id,addr}
	UserFriend {id,userId,friendId}
	Goods {id,name}
	GoodsDesc {id,saled,content}
	GoodsOrder {id,goodsId,userId,amount,money,ctime}
	GoodsShipping {id,userId,goodsOrderId,receiverId,addr }
	1.哪些用户在情人节给他的好友购买销量小于1000，价值大于1000元戒指？
	"select u.id userId,u.name username,ua.addr addr, g.name goodsName,go.ctime ctime
	from GoodsOrder go
	left join User u on go.userId = u.id
	left join UserAddr ua on go.userId = ua.id
	left join Goods g on g.id = go.goodsId
	left join GoodsDesc gd on go.goodsId = gd.id
	left join GoodsShipping gs on go.goodsOrderId = go.id
	"
	2.在情人节，收到两位以上好友的礼物用户，好友是谁？
	"select gs.userId, u.name user, g.name goodsName,group_concat(uf.name)friends
	from GoodsShipping gs
	left join User u on gs.userId = u.id
	left join UserFriend uf on gs.receiverId = uf.id
	left join GoodsOrder go on gs.goodsOrderId = go.id
	left join Goods g on g.id = go.goodsId
	"
	*/
	@Test
	public void params() {
	}
	
}
