package io.onceonly.db;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cn.mx.app.entity.UserChief;
import io.onceonly.db.dao.Cnd;
import io.onceonly.util.IDGenerator;

public class DaoHelperTest extends DaoBaseTest{
	
	@Before
	public void createTable() {
		daoHelper.drop(UserChief.class);
		daoHelper.createOrUpdate(UserChief.class);
	}
	@After
	public void dropTable() {
		daoHelper.drop(UserChief.class);
	}
	
	@Test
	public void where() {
		List<UserChief> ucs = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			UserChief uc = new UserChief();
			uc.setId(IDGenerator.randomID());
			uc.setName("Name"+i);
			uc.setGenre(i%4);
			uc.setAvatar(String.format("avatar%d%d%d", i%4,i%3,i%2));
			uc.setPasswd("passwd");
			uc.setDel(false);
			ucs.add(uc);
			System.out.println(uc);
		}
		/**N G  A
		 * 0 0  000
		 * 1 1  111 *
		 * 2 2  220 *
		 * 3 3  301
		 * 4 0  010 *
		 * 5 1  121 *
		 * 6 2  200
		 * 7 3  311 
		 * 8 0  020 *
		 * 9 1  101 *
		 *
		 */
		int insercnt = daoHelper.insert(ucs);
		Assert.assertEquals(10, insercnt);
		Assert.assertEquals(10, daoHelper.count(UserChief.class));
		UserChief e1 = new UserChief();
		e1.setGenre(2);
		UserChief e2 = new UserChief();
		e2.setGenre(3);
		Cnd<UserChief> cnd1 = new Cnd<>();
		Assert.assertEquals(10, daoHelper.count(UserChief.class, cnd1));
		cnd1.eq(e1).or().ne(e2);
		Assert.assertEquals(8, daoHelper.count(UserChief.class, cnd1));
		
		UserChief e3 = new UserChief();
		Cnd<UserChief> cnd3 = new Cnd<>();
		e3.setAvatar("avatar%00");
		cnd3.like(e3);
		Assert.assertEquals(2, daoHelper.count(UserChief.class, cnd3));
		Cnd<UserChief> cnd4 = new Cnd<>();
		/** (genre=2 or genre != 3) and not (avatar like 'avatar%00')*/
		cnd4.and(cnd1).not(cnd3);
		Assert.assertEquals(6, daoHelper.count(UserChief.class, cnd4));
	}
}