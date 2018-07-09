package com.champion.dance.filter;


import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;


/**
 * @author jpli3
 */
@Slf4j
@WebFilter(filterName="payloadLoggingFilter",urlPatterns={"/*"})
public class PayloadLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        PayloadRequestWrapper wrapper = new PayloadRequestWrapper(request);

        log.info(new StringBuilder("\n")
                .append("::::::::::::::::::::::::::Request Method: ")
                .append(request.getMethod())
                .append("\t")
                .append("Request URL:")
                .append(request.getRequestURI())
                .append("\n")
                .append("::::::::::::::::::::::::::payload:")
                .append(Strings.nullToEmpty(wrapper.getBody()))
                .append(!CollectionUtils.isEmpty(request.getParameterMap()) ? "\n::::::::::::::::::::::::::params:" +
                        request.getParameterMap().entrySet().stream()
                                .map(entry -> entry.getKey() + ":" + Arrays.toString(entry.getValue()))
                                .collect(Collectors.joining(",")) : "")
                .append(!StringUtils.isEmpty(request.getHeader("sessionId")) ? "\n::::::::::::::::::::::::::sessionId:" + String.valueOf(request.getHeader("sessionId")) : "").toString())
        ;
        chain.doFilter(wrapper, servletResponse);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
}
