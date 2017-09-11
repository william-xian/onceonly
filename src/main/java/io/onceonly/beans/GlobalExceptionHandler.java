package io.onceonly.beans;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.onceonly.exception.Failed;
import io.onceonly.util.DlsUtils;


@RestController
@ControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Log logger = LogFactory.getLog(GlobalExceptionHandler.class);
    
    @Autowired
    private I18nRepository i18nRepository;
 
    @ExceptionHandler(value = Failed.class)
    @ResponseBody
    public Map<String,Object> failedHandler(HttpServletRequest req, Failed failed) throws Exception {
    	String defaultFromat  = failed.getFormat();
    	Locale  locale = req.getLocale();
    	String lang = locale == null ? null:locale.getLanguage();
    	if(lang != null && !lang.equals(Locale.getDefault().getLanguage())){
        	String id = "msg/"+lang+"_"+DlsUtils.encodeMD5(failed.getFormat());	
        	I18n i18n = i18nRepository.findOne(id);
        	if(i18n != null) {
        		defaultFromat  = i18n.getName();
        	}
    	}
    	String msg  = String.format(defaultFromat, failed.getArgs());
    	logger.error(String.format("Host %s invokes url %s ERROR: %s", req.getRemoteHost(), req.getRequestURL(), msg));
        Map<String,Object> result = new HashMap<>();
        if(failed.getData() != null) {
        	result.put("data", failed.getData());	
        }
        switch(failed.getLevel()){
        case Failed.ERROR:
        	result.put("error", msg);
        	break;
        case Failed.WARNNING:
        	result.put("warnning", msg);
        	break; 
        case Failed.MSG:
        	result.put("msg", msg);
        	break;
        }
        return result;
    }
 
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Map<String,String> defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
    	logger.error(String.format("Host %s invokes url %s ERROR: %s", req.getRemoteHost(), req.getRequestURL(), e.getMessage()));
        Map<String,String> result = new HashMap<>();
    	result.put("error", e.getMessage());
        return result;
    }
}	