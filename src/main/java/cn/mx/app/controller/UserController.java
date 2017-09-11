package cn.mx.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.onceonly.exception.Failed;
import cn.mx.app.common.Messages;
import cn.mx.app.entity.UserChief;
import cn.mx.app.repository.UserRepository;
import cn.mx.app.service.UserService;
import cn.mx.app.view.UserView;

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping(value = "/add", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public UserChief add(@RequestParam(name="name",required=false)String name,
			@RequestParam("passwd")String passwd,
			@RequestParam("avatar")String avatar) {
		UserChief user = new UserChief();
		user.setName(name);
		user.setPasswd(passwd);
		user.setAvatar(avatar);
		userRepository.save(user);
		return user;
	}
	
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	@Transactional
	@ResponseBody
	public UserChief update(@RequestParam(name="name",required=false)String name,@RequestParam("passwd")String passwd,@RequestParam("avatar")String avatar,@RequestParam(name="userId",required=false)String userId) {
		UserChief user = new UserChief();
		user.setName(name);
		user.setPasswd(passwd);
		user.setAvatar(avatar);
		userService.updateUserPassword(userId, passwd);
		if(passwd.equals("1")){
			System.err.println(Messages.HELLO);
			Failed.throwError(Messages.HELLO, name);
		}
		userRepository.save(user);
		return user;
	}
	
	
	@RequestMapping(value = "/find", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public List<UserView> find(@RequestParam("name") String name,Pageable page) {
		List<UserView> result = userService.find(name);
		return result;
	}
}
