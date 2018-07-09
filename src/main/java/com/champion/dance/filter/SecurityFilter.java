package com.champion.dance.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jpli3
 */
@Slf4j
@WebFilter(filterName="securityFilter",urlPatterns={"/api/member/*","/api/course/*","/api/courses/*","/api/comment/*"})
public class SecurityFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) {
        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
//        resp.setHeader("Access-Control-Allow-Origin", "*");
        String sessionId = req.getHeader("sessionId");
        if(req.getMethod().equals("OPTIONS")){
            resp.setStatus(HttpStatus.SC_NO_CONTENT);
            chain.doFilter(request, response);
        }
        else if (null == sessionId) {
            resp.setStatus(HttpStatus.SC_FORBIDDEN);
        } else {
            request.setAttribute("sessionId", sessionId);//加密的openId作为接口安全验证
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
