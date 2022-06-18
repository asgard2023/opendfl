package org.ccs.opendfl.mysql.auth;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.mysql.utils.LoginUtils;
import org.ccs.opendfl.mysql.vo.UserVo;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class AuthAspect {

    @Around("@annotation(org.ccs.opendfl.mysql.auth.CheckLogin)")
    public Object checkLogin(ProceedingJoinPoint point) throws Throwable {
        checkToken();
        return point.proceed();
    }

    private void checkToken() {
        try {
            // 1. 从header里面获取token
            HttpServletRequest request = getHttpServletRequest();

//            String token = request.getHeader("X-Token");
            String token = RequestUtils.getToken(request);
            if(StringUtils.isBlank(token)){
                throw new SecurityException("Token null");
            }
            UserVo userVo = LoginUtils.getUserByToken(token);

            // 2. 校验token是否合法&是否过期；如果不合法或已过期直接抛异常；如果合法放行
            boolean isValid = userVo != null;
            if (!isValid) {
                throw new SecurityException("Token不合法！");
            }

            // 3. 如果校验成功，那么就将用户的信息设置到request的attribute里面
            request.setAttribute("id", userVo.getId());
            request.setAttribute("nickname", userVo.getNickname());
            request.setAttribute("role", userVo.getRole());
//            request.setAttribute("role", claims.get("role"));
        } catch (Throwable throwable) {
            log.warn("-----checkToken--error={}", throwable.getMessage());
            throw new SecurityException(throwable.getMessage());
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        return attributes.getRequest();
    }

    @Around("@annotation(org.ccs.opendfl.mysql.auth.CheckAuthorization)")
    public Object checkAuthorization(ProceedingJoinPoint point) throws Throwable {
        try {
            // 1. 验证token是否合法；
            this.checkToken();
            // 2. 验证用户角色是否匹配
            HttpServletRequest request = getHttpServletRequest();
            String role = (String) request.getAttribute("role");

            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            CheckAuthorization annotation = method.getAnnotation(CheckAuthorization.class);

            String value = annotation.value();

            if (!Objects.equals(role, value)) {
                throw new SecurityException("用户无权访问！");
            }
        } catch (Throwable throwable) {
            log.warn("-----checkAuthorization--error={}", throwable.getMessage());
            throw new SecurityException(throwable.getMessage());
        }
        return point.proceed();
    }
}
