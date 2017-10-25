package io.onceonly.db;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import cn.mx.app.Launcher;
import cn.mx.app.entity.UserChief;
import io.onceonly.db.dao.DaoHelper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Launcher.class)
public class DaoHelperTest {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Test
	public void createOrUpdate() {
		DaoHelper daoHelper = new DaoHelper();
		daoHelper.setJdbcTemplate(jdbcTemplate);
		DDHoster.putEntity(UserChief.class);
		DDHoster.upgrade();
		daoHelper.setNameToTableMata(DDHoster.tableToTableMeta);
		daoHelper.setJdbcTemplate(jdbcTemplate);
		daoHelper.createOrUpdate(UserChief.class);
		
	}
}