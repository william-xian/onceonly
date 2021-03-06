package io.onceonly.db;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import cn.mx.app.entity.UserChief;
import io.onceonly.db.dao.Cnd;
import io.onceonly.db.dao.Page;
import io.onceonly.db.dao.tpl.SelectTpl;
import io.onceonly.db.dao.tpl.Tpl;
import io.onceonly.db.dao.tpl.UpdateTpl;
import io.onceonly.util.IDGenerator;
import io.onceonly.util.OOUtils;

public class DaoHelperTest extends DaoBaseTest{
	
	@BeforeClass
	public static void init() {
		initDao();
	}
	@AfterClass
	public static void cleanup() {
	}
	@Test
	public void insert_get_remove_delete() {
		List<UserChief> ucs = new ArrayList<>();
		List<Long> ids = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			UserChief uc = new UserChief();
			uc.setId(IDGenerator.randomID());
			uc.setName("name"+i + "-" + System.currentTimeMillis());
			uc.setGenre(i%4);
			uc.setAvatar(String.format("avatar%d%d",i%2,i%3));
			uc.setPasswd("passwd"+i%3);
			System.out.println(OOUtils.toJSON(uc));
			ucs.add(uc);
			ids.add(uc.getId());
		}
		int insercnt = daoHelper.insert(ucs);
		Assert.assertEquals(10, insercnt);
		Assert.assertEquals(10, daoHelper.count(UserChief.class));
		UserChief uc = new UserChief();
		uc.setId(IDGenerator.randomID());
		uc.setName("name-" + System.currentTimeMillis());
		uc.setGenre(100);
		uc.setAvatar("avatar");
		uc.setPasswd("passwd");
		daoHelper.insert(uc);
		Assert.assertEquals(11, daoHelper.count(UserChief.class));
		UserChief db = daoHelper.get(UserChief.class, uc.getId());
		
		Assert.assertEquals(db.toString(), uc.toString());
		int deleted1 = daoHelper.deleteById(UserChief.class, uc.getId());
		Assert.assertEquals(0, deleted1);
		Assert.assertEquals(11, daoHelper.count(UserChief.class));
		/** 
		 * 
		 */
		int removed1 = daoHelper.removeById(UserChief.class, uc.getId());
		Assert.assertEquals(1, removed1);
		int deleteRemoved1 = daoHelper.deleteById(UserChief.class, uc.getId());
		Assert.assertEquals(1, deleteRemoved1);
		Assert.assertEquals(10, daoHelper.count(UserChief.class));

		int deleted10 = daoHelper.deleteByIds(UserChief.class, ids);
		Assert.assertEquals(0, deleted10);

		int deleteRemoved10 = daoHelper.removeByIds(UserChief.class, ids);
		Assert.assertEquals(10, deleteRemoved10);
		int deletedRemoved10 = daoHelper.deleteByIds(UserChief.class, ids);
		Assert.assertEquals(10, deletedRemoved10);
		Assert.assertEquals(0, daoHelper.count(UserChief.class));
	}
	
	@Test
	public void update_updateIgnoreNull() {
		List<UserChief> ucs = new ArrayList<>();
		List<Long> ids = new ArrayList<>();
		for(int i = 0; i < 3; i++) {
			UserChief uc = new UserChief();
			uc.setId(IDGenerator.randomID());
			uc.setName("name"+i + "-" + System.currentTimeMillis());
			uc.setGenre(i%4);
			uc.setAvatar(String.format("avatar%d%d",i%2,i%3));
			uc.setPasswd("passwd");
			ucs.add(uc);
			ids.add(uc.getId());
		}
		daoHelper.insert(ucs);
		UserChief uc1 = ucs.get(0);
		UserChief uc2 = ucs.get(1);
		UserChief uc3 = ucs.get(2);
		uc1.setName("t-name");
		uc1.setAvatar(null);
		daoHelper.update(uc1);
		UserChief db1 = daoHelper.get(UserChief.class, uc1.getId());
		Assert.assertEquals("t-name", db1.getName());
		Assert.assertNull(db1.getAvatar());
		UserChief up2 = new UserChief();
		up2.setId(uc2.getId());
		up2.setName("t-name-"+System.currentTimeMillis());
		daoHelper.updateIgnoreNull(up2);
		UserChief db2 = daoHelper.get(UserChief.class, uc2.getId());
		Assert.assertEquals(up2.getName(),db2.getName());
		Assert.assertNotNull(db2.getName());
		/** 无关数据没有被干扰 */
		UserChief db3 = daoHelper.get(UserChief.class, uc3.getId());
		uc3.setRm(false);
		Assert.assertEquals(uc3.toString(), db3.toString());
		daoHelper.removeByIds(UserChief.class, ids);
		daoHelper.deleteByIds(UserChief.class, ids);
	}
	@Test
	public void updateByTpl() {
		List<UserChief> ucs = new ArrayList<>();
		List<Long> ids = new ArrayList<>();
		for(int i = 0; i < 3; i++) {
			UserChief uc = new UserChief();
			uc.setId(IDGenerator.randomID());
			uc.setName("name"+i + "-" + System.currentTimeMillis());
			uc.setGenre(i);
			uc.setAvatar(String.format("avatar%d%d",i%2,i%3));
			uc.setPasswd("passwd");
			ucs.add(uc);
			ids.add(uc.getId());
		}
		daoHelper.insert(ucs);
		UserChief uc1 = ucs.get(0);
		UserChief uc2 = ucs.get(1);
		UpdateTpl<UserChief> tpl = new UpdateTpl<>(UserChief.class);
		tpl.set().setId(uc1.getId());
		tpl.add().setGenre(1);
		daoHelper.updateByTpl(UserChief.class,tpl);
		UserChief db1 = daoHelper.get(UserChief.class, (Long)tpl.getId());
		Assert.assertEquals(1,db1.getGenre().intValue());
		UserChief db2 = daoHelper.get(UserChief.class, uc2.getId());
		uc2.setRm(false);
		Assert.assertEquals(uc2.toString(),db2.toString());
		daoHelper.removeByIds(UserChief.class, ids);
		daoHelper.deleteByIds(UserChief.class, ids);
	}
	
	@Test
	public void find() {
		List<UserChief> ucs = new ArrayList<>();
		List<Long> ids = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			UserChief uc = new UserChief();
			uc.setId(IDGenerator.randomID());
			uc.setName("name" + i + "-" + System.currentTimeMillis());
			uc.setGenre(i % 4);
			uc.setAvatar(String.format("avatar%d%d", i % 2, i % 3));
			uc.setPasswd("passwd");
			ucs.add(uc);
			ids.add(uc.getId());
		}
		daoHelper.insert(ucs);
		Cnd<UserChief> cnd1 = new Cnd<>(UserChief.class);
		cnd1.eq().setGenre(2);
		cnd1.or().ne().setGenre(3);
		Cnd<UserChief> cnd3 = new Cnd<>(UserChief.class);
		cnd3.like().setAvatar("avatar%00");
		Assert.assertEquals(2, daoHelper.count(UserChief.class, cnd3));
		Cnd<UserChief> cnd4 = new Cnd<>(UserChief.class);
		/** (genre=2 or genre != 3) and not (avatar like 'avatar%00')*/
		cnd4.and(cnd1).not(cnd3);
		Assert.assertEquals(6, daoHelper.count(UserChief.class, cnd4));
		
		cnd4.setPage(-2);
		cnd4.setPageSize(4);
		Page<UserChief> page1 = daoHelper.find(UserChief.class, cnd4);
		Assert.assertEquals(2,page1.getData().size());
		Assert.assertEquals(6,page1.getTotal().longValue());
		daoHelper.removeByIds(UserChief.class, ids);
		daoHelper.deleteByIds(UserChief.class, ids);
		
	}
	
	@Test
	public void having_group_orderby() {
		List<UserChief> ucs = new ArrayList<>();
		List<Long> ids = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			UserChief uc = new UserChief();
			uc.setId(IDGenerator.randomID());
			uc.setName("name" + i + "-" + System.currentTimeMillis());
			uc.setGenre(i % 4);
			uc.setAvatar(String.format("avatar%d%d", i % 2, i % 3));
			uc.setPasswd("passwd");
			ucs.add(uc);
			ids.add(uc.getId());
		}
		daoHelper.insert(ucs);
		
		Cnd<UserChief> cnd = new Cnd<UserChief>(UserChief.class);
		cnd.groupBy().use().setGenre(Tpl.USING_INT);
		
		SelectTpl<UserChief> distinct = new SelectTpl<UserChief>(UserChief.class);
		distinct.using().setGenre(SelectTpl.USING_INT);
		Page<UserChief> page= daoHelper.find(UserChief.class, distinct, cnd);
		Assert.assertEquals(page.getTotal(), new Long(4));
		
		SelectTpl<UserChief> max = new SelectTpl<UserChief>(UserChief.class);
		max.max().setGenre(SelectTpl.USING_INT);
		UserChief ucMax = daoHelper.fetch(UserChief.class,max,null);
		Assert.assertEquals(ucMax.getGenre(), new Integer(3));
		
		SelectTpl<UserChief> min = new SelectTpl<UserChief>(UserChief.class);
		min.min().setGenre(SelectTpl.USING_INT);
		UserChief ucMin = daoHelper.fetch(UserChief.class,min,null);
		Assert.assertEquals(ucMin.getGenre(), new Integer(0));
		
		
		SelectTpl<UserChief> sum = new SelectTpl<UserChief>(UserChief.class);
		sum.sum().setGenre(SelectTpl.USING_INT);
		UserChief ucSum = daoHelper.fetch(UserChief.class,sum,null);
		Assert.assertEquals(ucSum.getExtra().get("sum_genre"), new Long(13));
		
		SelectTpl<UserChief> avg = new SelectTpl<UserChief>(UserChief.class);
		avg.avg().setGenre(SelectTpl.USING_INT);
		UserChief ucAvg = daoHelper.fetch(UserChief.class,avg,null);
		System.out.println(ucAvg);
		
		daoHelper.removeByIds(UserChief.class, ids);
		daoHelper.deleteByIds(UserChief.class, ids);
	}
}