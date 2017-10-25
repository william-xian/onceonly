package io.onceonly.db;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import cn.mx.app.Launcher;
import cn.mx.app.entity.UserChief;
import io.onceonly.db.dao.DaoHelper;
import io.onceonly.db.meta.ColumnMeta;
import io.onceonly.db.meta.TableMeta;
import io.onceonly.util.IDGenerator;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Launcher.class)
public class DaoHelperTest {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Test
	public void createOrUpdate() {
		DDHoster.putEntity(UserChief.class);
		DDHoster.upgrade();
		for(TableMeta tm:DDHoster.tableToTableMeta.values()) {
			System.err.println(tm);
			for(ColumnMeta cm:tm.getColumnMetas()) {
				System.out.println(cm.getName() + " : " + cm.getField());
			}
		}

		DaoHelper daoHelper = new DaoHelper();
		daoHelper.setJdbcTemplate(jdbcTemplate);
		daoHelper.setNameToTableMata(DDHoster.tableToTableMeta);
		daoHelper.setJdbcTemplate(jdbcTemplate);
		//daoHelper.createOrUpdate(UserChief.class);
		List<UserChief> ucs = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			UserChief uc = new UserChief();
			uc.setId(IDGenerator.randomID());
			uc.setName("MaXian"+i);
			uc.setGenre(1);
			uc.setAvatar("avatar");
			uc.setPasswd("passwd");
			ucs.add(uc);
		}
		int cnt = daoHelper.insert(ucs);
		
		System.out.println(cnt);
	}
}