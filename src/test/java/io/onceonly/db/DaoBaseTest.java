package io.onceonly.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.mx.app.entity.Goods;
import cn.mx.app.entity.GoodsDesc;
import cn.mx.app.entity.GoodsOrder;
import cn.mx.app.entity.GoodsOrderView;
import cn.mx.app.entity.GoodsShipping;
import cn.mx.app.entity.UserChief;
import cn.mx.app.entity.UserFriend;
import cn.mx.app.entity.UserProfile;
import cn.mx.app.entity.Wallet;
import io.onceonly.db.dao.IdGenerator;
import io.onceonly.db.dao.impl.DaoHelper;
import io.onceonly.db.tbl.OOEntity;
import io.onceonly.util.IDGenerator;

public class DaoBaseTest {
	protected static final JdbcTemplate jdbcTemplate = new JdbcTemplate();

	protected static final DaoHelper daoHelper = new DaoHelper();
	
	public static void initDao() {
		try {
		Properties prop = new Properties();
		prop.load(new FileInputStream("src/main/resources/application.properties"));
		String driver = prop.getProperty("spring.datasource.driver");
		String url = prop.getProperty("spring.datasource.url");
		String username =prop.getProperty("spring.datasource.username");
		String password = prop.getProperty("spring.datasource.password");
		String maxActive = prop.getProperty("spring.datasource.maxActive");
		DataSource ds = new DataSource();
		ds.setDriverClassName(driver);
		ds.setUrl(url);
		ds.setUsername(username);
		ds.setPassword(password);
		ds.setMaxActive(Integer.parseInt(maxActive));
		jdbcTemplate.setDataSource(ds);
		System.out.println("loaded jdbcTemplate");
		IdGenerator idGenerator = new IdGenerator() {
			@Override
			public Long next(Class<?> entityClass) {
				return IDGenerator.randomID();
			}
			
		};
		DDHoster.upgrade();
		List<Class<? extends OOEntity<?>>> entities = new ArrayList<>();
		entities.add(UserChief.class);
		entities.add(UserProfile.class);
		entities.add(Wallet.class);
		entities.add(UserFriend.class);
		entities.add(Goods.class);
		entities.add(GoodsDesc.class);
		entities.add(GoodsOrder.class);
		entities.add(GoodsShipping.class);
		entities.add(GoodsOrderView.class);
		
		daoHelper.init(jdbcTemplate, idGenerator,entities);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
}
