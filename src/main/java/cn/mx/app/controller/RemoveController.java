package cn.mx.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RemoveController {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/{tbl}/{id}", method = { RequestMethod.DELETE })
	@ResponseBody
	public Integer remove(@PathVariable(name="tbl",required=false)String tbl,@PathVariable("id")String id) {
		String sql = String.format("update %s set active = false where id = ?", tbl);
		return jdbcTemplate.update(sql,id);
	}

	@RequestMapping(value = "/{tbl}", method = { RequestMethod.DELETE})
	@ResponseBody
	public Integer removes(@PathVariable(name="tbl",required=false)String tbl,@RequestBody RemoveReq req) {
		String[] args = req.getIds();
		if(args != null && args.length > 0){
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < args.length; i++){
				sb.append("?,");
			}
			sb.deleteCharAt(sb.length()-1);
			String sql = String.format("update %s set active = false where id in (%s)", tbl,sb.toString());
			return jdbcTemplate.update(sql, new ArgumentPreparedStatementSetter(args));		
		}else {
			return 0;
		}
	}
}

class RemoveReq {
	private String[] ids;

	public String[] getIds() {
		return ids;
	}

	public void setIds(String[] ids) {
		this.ids = ids;
	}
}

