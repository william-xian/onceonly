package io.onceonly.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cn.mx.app.entity.Goods;
import cn.mx.app.entity.GoodsDesc;
import cn.mx.app.entity.GoodsOrder;
import cn.mx.app.entity.GoodsOrderView;
import cn.mx.app.entity.UserChief;
import io.onceonly.db.dao.Cnd;
import io.onceonly.db.dao.Page;

public class DaoHelperViewTest extends DaoBaseTest{
	
	@BeforeClass
	public static void init() {
		initDao();
	}
	@AfterClass
	public static void cleanup() {
	}
	
	@Test
	public void findView() {
		
		List<UserChief> ucs = new ArrayList<>();
		List<Goods> goodses = new ArrayList<>();
		List<GoodsDesc> goodsDesces = new ArrayList<>();
		List<GoodsOrder> goodsOrderes = new ArrayList<>();
		for(int i = 0; i < 5; i++) {
			UserChief uc = new UserChief();
			uc.setId(1L+i);
			uc.setName("user-"+i);
			ucs.add(uc);
			Goods g = new Goods();
			g.setId(1L+i);
			g.setName("goods-"+i);
			goodses.add(g);
			GoodsDesc gd = new GoodsDesc();
			gd.setId(1L+i);
			gd.setContent("GoodsDesc-content"+i);
			gd.setSaled(0);
			goodsDesces.add(gd);
		}
		
		for(int i = 0; i < 25; i++) {
			GoodsOrder go = new GoodsOrder();
			go.setId(1L+i);
			go.setUserId(1L+i%5);
			go.setGoodsId(1L+i%5);
			go.setAmount(1+i%4);
			go.setCtime(System.currentTimeMillis());
			go.setMoney(i+10);
			goodsOrderes.add(go);
		}
		
		daoHelper.insert(ucs);
		daoHelper.insert(goodses);
		daoHelper.insert(goodsDesces);
		daoHelper.insert(goodsOrderes);
		
		
		Cnd<GoodsOrderView> cnd = new Cnd<>(GoodsOrderView.class);
		Page<GoodsOrderView> page = daoHelper.find(GoodsOrderView.class,cnd);
		System.out.println(page);

		
		Cnd<GoodsOrder> rm = new Cnd<>(GoodsOrder.class);
		rm.ge().setId(0L);
		List<Long> ids = Arrays.asList(1L,2L,3L,4L,5L);
		daoHelper.remove(UserChief.class, ids);
		daoHelper.remove(Goods.class, ids);
		daoHelper.remove(GoodsDesc.class, ids);
		daoHelper.remove(GoodsOrder.class, rm);
		Cnd<GoodsOrder> del = new Cnd<>(GoodsOrder.class);
		del.ge().setId(0L);
		daoHelper.delete(GoodsOrder.class, del);
		daoHelper.delete(UserChief.class, ids);
		daoHelper.delete(Goods.class, ids);
		daoHelper.delete(GoodsDesc.class, ids);
		
	}
	
}