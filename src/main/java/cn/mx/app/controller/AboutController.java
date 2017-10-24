package cn.mx.app.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.mx.app.Launcher;

@RestController
@RequestMapping("/about")
public class AboutController {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(AboutController.class);

	@RequestMapping("/id")
	public String id() {
		return Launcher.CAC.getId();
	}

	@RequestMapping(value = "/name", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String name() {
		return Launcher.CAC.getApplicationName();
	}

	@RequestMapping(value = "/", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> about(@RequestBody Req data,@RequestBody Map<String,Object> extra) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("req", data);
		result.put("extra", extra);
		return result;
	}
}
class Req{
	private String age;

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}
	
}