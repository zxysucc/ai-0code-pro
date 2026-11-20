package com.zxy.aicodepro.service;

import com.mybatisflex.core.service.IService;
import com.zxy.aicodepro.model.entity.User;
import com.zxy.aicodepro.model.vo.LoginUserVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户 服务层。
 *
 * @author zxy
 */
public interface UserService extends IService<User> {

    long userRegister(String userAccount, String userPassword, String checkPassword);

    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    LoginUserVO getUserLoginInfo(HttpServletRequest request);

    boolean userLogout(HttpServletRequest request);
}
