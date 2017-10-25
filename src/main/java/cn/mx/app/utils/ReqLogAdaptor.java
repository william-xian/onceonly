package cn.mx.app.utils;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.HandlerMapping;

import cn.mx.app.entity.ReqLog;
import io.onceonly.OOConfig;
import io.onceonly.util.OOLog;
import io.onceonly.util.OOUtils;

public class ReqLogAdaptor {
    
	public static ReqLog collectLog(HttpServletRequest request) {
		ReqLog optLog = new ReqLog();
		long beginTime = System.currentTimeMillis();
		// 接收到请求，记录请求内容
		String uri = request.getRequestURI();
		String remoteAddr = getIpAddr(request);
		String sessionId = request.getSession().getId();
		String user = (String) request.getSession().getAttribute("user");
		String method = request.getMethod();
		String params = "";
		if ("POST".equals(method)) {
			Object[] paramsArray = null;
			if(request.getContentLength() < OOConfig.REQ_LOG_MAX_BYTES ) {
				byte[] bytes = new byte[request.getContentLength()];
				try {
					ServletInputStream sis = request.getInputStream();
					sis.mark(request.getContentLength());
					sis.read(bytes);
					sis.reset();
					params = new String(bytes);
				} catch (IOException e) {
					OOLog.info("参数读取失败");
				}
			}else {
				params = String.format("{CONTENT_LENGTH:%s}",request.getContentLength());
			}
			
			params = OOUtils.toJSON(paramsArray);
		} else {
			Map<?, ?> paramsMap = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
			params = OOUtils.toJSON(paramsMap);
		}
		optLog.setMethod(method);
		optLog.setParams(params != null ? params.toString() : "");
		optLog.setRemoteAddr(remoteAddr);
		optLog.setSessionId(sessionId);
		optLog.setUri(uri);
		optLog.setCurUser(user);
		optLog.setBeginTime(beginTime);
		return optLog;
	}

    /**
     * 获取登录用户远程主机ip地址
     * 
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}