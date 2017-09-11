package cn.mx.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import io.onceonly.db.DAOTransUtil;
import cn.mx.app.view.UserView;

@Service
public class UserService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<UserView> find(String name) {
		
		String sql = String
				.format("select u.id user_id,u.name \"name\",up.nickname nickname,up.phone phone,w.balance balance"
						+ " from user_chief u left join user_profile up on u.id = up.id "
						+ " left join wallet w on u.id = w.user_id" + " where u.name = '%s'", name);
		List<UserView> result = jdbcTemplate.query(sql, DAOTransUtil.defaultRowMapper(UserView.class));
		return result;
	}
	
	public void updateUserPassword(String userId,String passwd) {
		String sql = "update user_chief set passwd = ? where id = ?";
		jdbcTemplate.update(sql, passwd,userId);
	}

}
