package com.zxy.aicodepro.aop;

import com.zxy.aicodepro.annotation.AuthCheck;
import com.zxy.aicodepro.common.ErrorCode;
import com.zxy.aicodepro.exception.BusinessException;
import com.zxy.aicodepro.model.entity.User;
import com.zxy.aicodepro.model.enums.UserRoleEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.zxy.aicodepro.constant.UserConstant.USER_LOGIN;

@Aspect
@Component
public class AuthInterceptor {

    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        User user = (User) request.getSession().getAttribute(USER_LOGIN);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        UserRoleEnum curUserRole = UserRoleEnum.Companion.getUserRoleEnum(user.getUserRole());
        UserRoleEnum mustRole = UserRoleEnum.Companion.getUserRoleEnum(authCheck.role());
        if(mustRole == null ) {
            return joinPoint.proceed();
        }
        if(UserRoleEnum.ADMIN.equals(mustRole) && !UserRoleEnum.ADMIN.equals(curUserRole)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return joinPoint.proceed();
    }
}
