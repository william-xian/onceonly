package cn.mx.app.aspect;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import io.onceonly.util.OOUtils;
import cn.mx.app.audit.repository.ReqLogRepository;
import cn.mx.app.entity.ReqLog;

@Aspect
@Component
public class WebRequestLogAspect {

    private static Logger logger = LoggerFactory.getLogger(WebRequestLogAspect.class);

    private ThreadLocal<ReqLog> tlocal = new ThreadLocal<ReqLog>();

    @Autowired
    private ReqLogRepository reqLogService;

    @Pointcut("execution(public * cn.mx.app.controller.*.*(..))")
    public void webRequestLog() {}

    //@Order(5)
    @Before("webRequestLog()")
    public void doBefore(JoinPoint joinPoint) {
        try {
            long beginTime = System.currentTimeMillis();
            // 接收到请求，记录请求内容
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String uri = request.getRequestURI();
            String remoteAddr = getIpAddr(request);
            String sessionId = request.getSession().getId();
            String user = (String) request.getSession().getAttribute("user");
            String method = request.getMethod();
            String params = "";
            if ("POST".equals(method)) {
                Object[] paramsArray = joinPoint.getArgs();
                params = OOUtils.toJSON(paramsArray);
            } else {
                Map<?, ?> paramsMap = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
                params = OOUtils.toJSON(paramsMap);
            }
            ReqLog optLog = new ReqLog();
            optLog.setMethod(method);
            optLog.setParams(params != null ? params.toString() : "");
            optLog.setRemoteAddr(remoteAddr);
            optLog.setSessionId(sessionId);
            optLog.setUri(uri);
            optLog.setCurUser(user);
            optLog.setBeginTime(beginTime);
            tlocal.set(optLog);
            logger.info(OOUtils.toJSON(optLog));

        } catch (Exception e) {
            logger.error("***操作请求日志记录失败doBefore()***", e);
        }
    }

    //@Order(5)
    @AfterReturning(returning = "result", pointcut = "webRequestLog()")
    public void doAfterReturning(Object result) {
        try {
            // 处理完请求，返回内容
            ReqLog optLog = tlocal.get();
            optLog.setResult(result.toString());
            long beginTime = optLog.getBeginTime();
            long requestTime = System.currentTimeMillis() - beginTime ;
            optLog.setRequestTime(requestTime);
            reqLogService.save(optLog);
        } catch (Exception e) {
            logger.error("***操作请求日志记录失败doAfterReturning()***", e);
        }
    }

    /**
     * 获取登录用户远程主机ip地址
     * 
     * @param request
     * @return
     */
    private String getIpAddr(HttpServletRequest request) {
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