package io.onceonly.db;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cn.mx.app.entity.UserChief;
import io.onceonly.db.dao.DaoHelper;
import io.onceonly.util.IDGenerator;

public class DaoHelperTest extends DaoBaseTest{

	@Test
	public void createOrUpdate() {
		//DDHoster.putEntity(UserChief.class);
		DDHoster.upgrade();
		DaoHelper daoHelper = new DaoHelper();
		daoHelper.setJdbcTemplate(jdbcTemplate);
		daoHelper.setTableToTableMata(DDHoster.tableToTableMeta);
		daoHelper.setJdbcTemplate(jdbcTemplate);
		daoHelper.createOrUpdate(UserChief.class);
		List<UserChief> ucs = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			UserChief uc = new UserChief();
			uc.setId(IDGenerator.randomID());
			uc.setName("MaXian"+i);
			uc.setGenre(1);
			uc.setAvatar("avatar");
			uc.setPasswd("passwd");
			ucs.add(uc);
			System.out.println(uc);
		}
		int cnt = daoHelper.insert(ucs);
		System.out.println(cnt);
		daoHelper.drop(UserChief.class);
	}
}