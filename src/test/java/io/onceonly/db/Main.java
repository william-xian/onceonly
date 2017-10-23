package io.onceonly.db;

import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cn.mx.app.entity.Goods;
import cn.mx.app.entity.GoodsDesc;
import cn.mx.app.entity.GoodsOrder;
import cn.mx.app.entity.GoodsShipping;
import cn.mx.app.entity.ReqLog;
import cn.mx.app.entity.UserChief;
import cn.mx.app.entity.UserFriend;
import cn.mx.app.entity.UserProfile;
import cn.mx.app.entity.Wallet;

public class Main {
	static final Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
	public static void main(String[] args) {

		List<Class<?>> entities = Arrays.asList(Goods.class,GoodsDesc.class,GoodsOrder.class,GoodsShipping.class,
				ReqLog.class,UserChief.class,UserFriend.class,UserProfile.class,Wallet.class);
		
		for(Class<?> clazz : entities) {
			TableMeta tm = TableMeta.createBy(clazz);	
			System.out.println(tm.createTableSql());		
		}
		DDHoster.putEntities(entities);
	}
	
}
