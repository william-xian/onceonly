package cn.mx.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
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
		System.out.println(jdbcTemplate);
		String[] args = new String[] {"4028f6815e27a166015e27a60b95000b","4028f6815e27a166015e27a60b6e000a"};
		String sql = String.format("update %s set active = false where id in (?,?)", "req_log");
		/*
		for(Field field :Types.class.getFields()){
			try{
				int t = field.getInt(null);
				//System.err.println(field.getName()+ " : " + t);
				int c = jdbcTemplate.update(sql, args, new int[]{t,t});
				System.err.println(field.getName()+ " - " + c);
			}catch(Exception e){
			}
		}*/
	}
}
