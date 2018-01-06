package io.onceonly.db;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import cn.mx.app.entity.Goods;
import cn.mx.app.entity.GoodsDesc;
import cn.mx.app.entity.GoodsOrder;
import cn.mx.app.entity.GoodsOrderView;
import cn.mx.app.entity.UserChief;
import io.onceonly.db.dao.Cnd;
import io.onceonly.db.dao.Page;
import io.onceonly.db.dao.tpl.SelectTpl;
import io.onceonly.db.dao.tpl.Tpl;

public class DaoHelperViewTest extends DaoBaseTest{
	

	private static List<UserChief> ucs = new ArrayList<>();
	private static List<Goods> goodses = new ArrayList<>();
	private static List<GoodsDesc> goodsDesces = new ArrayList<>();
	private static List<GoodsOrder> goodsOrderes = new ArrayList<>();
	@BeforeClass
	public static void init() {
		initDao();
		insert();
	}
	public static void insert() {
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
	}
	@AfterClass
	public static void cleanup() {
		Cnd<GoodsOrder> rm = new Cnd<>(GoodsOrder.class);
		rm.ge().setId(0L);
		List<Long> ids = Arrays.asList(1L,2L,3L,4L,5L);
		daoHelper.removeByIds(UserChief.class, ids);
		daoHelper.removeByIds(Goods.class, ids);
		daoHelper.removeByIds(GoodsDesc.class, ids);
		daoHelper.remove(GoodsOrder.class, rm);
		Cnd<GoodsOrder> del = new Cnd<>(GoodsOrder.class);
		del.ge().setId(0L);
		daoHelper.delete(GoodsOrder.class, del);
		daoHelper.deleteByIds(UserChief.class, ids);
		daoHelper.deleteByIds(Goods.class, ids);
		daoHelper.deleteByIds(GoodsDesc.class, ids);
	}
	
	//@Test
	public void findView() {
		Cnd<GoodsOrderView> cnd = new Cnd<>(GoodsOrderView.class);
		cnd.setPageSize(25);
		SelectTpl<GoodsOrderView> tplOrder = new SelectTpl<>(GoodsOrderView.class);
		tplOrder.usingRowNum();
		Page<GoodsOrderView> page = daoHelper.find(GoodsOrderView.class,tplOrder,cnd);
		System.out.println(page);
		Assert.assertEquals(new Long(25), page.getTotal());
		Assert.assertEquals(25L, page.getData().size());
		GoodsOrderView gov = daoHelper.get(GoodsOrderView.class, 1L);
		Assert.assertNotNull(gov);
		
		SelectTpl<GoodsOrderView> tpl = new SelectTpl<>(GoodsOrderView.class);
		tpl.max().setId(Tpl.USING_LONG);
		GoodsOrderView maxTime = daoHelper.fetch(GoodsOrderView.class, tpl, cnd);
		Assert.assertEquals(25L, maxTime.getId()+0L);
		
		SelectTpl<GoodsOrderView> tplMin = new SelectTpl<>(GoodsOrderView.class);
		tplMin.min().setId(Tpl.USING_LONG);
		tplMin.sum().setId(Tpl.USING_LONG);
		tplMin.avg().setId(Tpl.USING_LONG);
		GoodsOrderView min = daoHelper.fetch(GoodsOrderView.class, tplMin, cnd);
		Assert.assertEquals(1L, min.getId()+0L);
		System.out.println(min);
		Assert.assertEquals(new BigDecimal((1L+25)*25/2), (BigDecimal)min.getExtra().get("sum_id"));
		Assert.assertEquals(new BigDecimal((1L+25)/2).intValue(), ((BigDecimal)min.getExtra().get("avg_id")).intValue());
	}
	@Test
	public void having() {
		Cnd<GoodsOrderView> cnd = new Cnd<>(GoodsOrderView.class);
		SelectTpl<GoodsOrderView> tpl = new SelectTpl<>(GoodsOrderView.class);
		tpl.max().setId(Tpl.USING_LONG);
		tpl.using().setUserName(Tpl.USING_S);
		cnd.groupBy().use().setUserName(Tpl.USING_S);
		cnd.having().max().setId(Tpl.USING_LONG);
		cnd.having().gt(2L);
		Page<GoodsOrderView> page = daoHelper.find(GoodsOrderView.class,tpl,cnd);
		System.out.println(page);
	}
	
	
}