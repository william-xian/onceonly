package cn.mx.app.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
@Component
@WebFilter(urlPatterns = "/**",filterName = "WebRequestFilter")
public class WebRequestFilter implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
    	if(response instanceof HttpServletResponse) {
    		//HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse rsp = (HttpServletResponse) response;  
            rsp.setHeader("Access-Control-Allow-Origin", "*");  
            rsp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");  
            rsp.setHeader("Access-Control-Max-Age", "3600");  
            rsp.setHeader("Access-Control-Allow-Headers", "x-requested-with"); 
    	}
    	filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}