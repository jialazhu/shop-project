package com.baidu.filter;

import com.baidu.config.JwtConfig;
import com.baidu.shop.utils.CookieUtils;
import com.baidu.shop.utils.JwtUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName LoginFilter
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-17 11:24
 * @Version V1.0
 **/
@Component
public class LoginFilter extends ZuulFilter {
    @Autowired
    private JwtConfig jwtConfig;

    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {  //执行优先级别 0最高优先
        return 5;
    }

    @Override
    public boolean shouldFilter() { //是否执行过滤器
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String uri = request.getRequestURI();
        logger.debug("=============" + uri);
        return !jwtConfig.getExcludePath().contains(uri);
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String token = CookieUtils.getCookieValue(request, jwtConfig.getCookieName());
        if(null != token){
            //解密
            try {
                JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            } catch (Exception e) {
                logger.debug("解密失败" + token);
                context.setSendZuulResponse(false);
                context.setResponseStatusCode(HttpStatus.SC_FORBIDDEN);
            }
        }else{
            logger.debug("token是null");
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.SC_FORBIDDEN);
        }
        return null;
    }
}
