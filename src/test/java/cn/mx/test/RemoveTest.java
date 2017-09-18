package cn.mx.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import cn.mx.app.Launcher;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Launcher.class)
public class RemoveTest {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Test
	public void removes() {
	}
}
