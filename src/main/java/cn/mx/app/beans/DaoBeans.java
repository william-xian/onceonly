package cn.mx.app.beans;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import io.onceonly.db.annotation.Tbl;
import io.onceonly.db.annotation.TblView;
import io.onceonly.db.dao.IdGenerator;
import io.onceonly.db.dao.impl.DaoHelper;
import io.onceonly.db.tbl.OOEntity;
import io.onceonly.util.AnnotationScanner;
import io.onceonly.util.IDGenerator;

@Configuration
public class DaoBeans {

	@Autowired
	private DataSource dataSource;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private IdGenerator IdGenerator;
	
	@Bean
	public IdGenerator createIdGenerator() {
		return new IdGenerator() {

			@Override
			public Long next(Class<?> entityClass) {
				return IDGenerator.randomID();
			}
		};
	}
	
	@Bean
	public JdbcTemplate createJdbcTemplate() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate;
	}
	
	@SuppressWarnings("unchecked")
	@Bean
	public DaoHelper createDaoHelper() {
		
		List<Class<? extends OOEntity>> entities = new ArrayList<>();
		AnnotationScanner scanner = new AnnotationScanner(Tbl.class,TblView.class);
		scanner.scanPackages("cn.mx.app");
		for(Class<?> clazz:scanner.getClasses(Tbl.class)) {
			if(clazz.isAssignableFrom(OOEntity.class)) {
				entities.add((Class<? extends OOEntity>)clazz);
			}
		}
		for(Class<?> clazz:scanner.getClasses(TblView.class)) {
			if(clazz.isAssignableFrom(OOEntity.class)) {
				entities.add((Class<? extends OOEntity>)clazz);
			}
		}
		
		DaoHelper daoHelper = new DaoHelper(jdbcTemplate,IdGenerator,entities);
		return daoHelper;
	}
}
