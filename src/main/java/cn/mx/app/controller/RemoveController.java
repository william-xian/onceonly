package cn.mx.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.mx.app.entity.UserChief;
import io.onceonly.db.dao.impl.DaoHelper;

@RestController
public class RemoveController {
	@Autowired
	private DaoHelper daoHelper;
	@RequestMapping(value = "/{tbl}/{id}", method = { RequestMethod.DELETE })
	@ResponseBody
	public Integer remove(@PathVariable(name="tbl",required=false)String tbl,@PathVariable("id")Long id) {
		Class<?> tblClazz = UserChief.class;
		return daoHelper.removeById(tblClazz, id);
	}
	
	@RequestMapping(value = "/add", method = { RequestMethod.GET })
	@ResponseBody
	public UserChief addUser(@RequestParam("name")String name,@RequestParam("passwd")String passwd) {
		UserChief uc = new UserChief();
		uc.setName(name);
		uc.setPasswd(passwd);
		uc = daoHelper.insert(uc);
		return uc;
	}
	
}


