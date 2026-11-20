package com.zxy.aicodepro.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.zxy.aicodepro.common.ErrorCode;
import com.zxy.aicodepro.exception.BusinessException;
import com.zxy.aicodepro.exception.ThrowUtils;
import com.zxy.aicodepro.model.entity.User;
import com.zxy.aicodepro.mapper.UserMapper;
import com.zxy.aicodepro.model.enums.UserRoleEnum;
import com.zxy.aicodepro.model.vo.LoginUserVO;
import com.zxy.aicodepro.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

import static com.zxy.aicodepro.constant.UserConstant.USER_LOGIN;

/**
 * 用户 服务层实现。
 *
 * @author zxy
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //如果有空的字段
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword, checkPassword), new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空"));

        ThrowUtils.throwIf(userAccount.length() < 4, new BusinessException(ErrorCode.PARAMS_ERROR, "用户昵称过短"));
        ThrowUtils.throwIf(userPassword.length() < 8, new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短"));
        ThrowUtils.throwIf(!userPassword.equals(checkPassword), new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致"));

        //查数据库，看是否是已经注册的用户
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.mapper.selectCountByQuery(queryWrapper);
        ThrowUtils.throwIf(count > 0, new BusinessException(ErrorCode.PARAMS_ERROR, "用户已存在"));
        String encryptPassword = getEncryptPassword(userPassword);
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("");
        user.setUserRole(UserRoleEnum.USER.getValue());
        this.mapper.insertSelective(user);
        return user.getId();
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword), new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空"));
        ThrowUtils.throwIf(userAccount.length() < 4, new BusinessException(ErrorCode.PARAMS_ERROR, "用户昵称过短"));
        ThrowUtils.throwIf(userPassword.length() < 8, new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短"));
        String password = getEncryptPassword(userPassword);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount)
                .eq("userPassword", password);
        User user = this.mapper.selectOneByQuery(queryWrapper);
        //创建session
        request.getSession().setAttribute(USER_LOGIN, user);
        return getUserVO(user);
    }

    @Override
    public LoginUserVO getUserLoginInfo(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN);
        ThrowUtils.throwIf(user == null, new BusinessException(ErrorCode.NOT_LOGIN_ERROR));
        User userInDb = this.mapper.selectOneById(user.getId());
        ThrowUtils.throwIf(userInDb == null, new BusinessException(ErrorCode.NOT_LOGIN_ERROR));
        if(user.getId().equals(userInDb.getId())) {
            return getUserVO(userInDb);
        }
        return null;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN);
        ThrowUtils.throwIf(user == null, new BusinessException(ErrorCode.NOT_LOGIN_ERROR));
        User userInDb = this.mapper.selectOneById(user.getId());
        ThrowUtils.throwIf(userInDb == null, new BusinessException(ErrorCode.NOT_LOGIN_ERROR));
        if(user.getId().equals(userInDb.getId())) {
            request.getSession().removeAttribute(USER_LOGIN);
            userInDb.setIsDelete(1);
            this.mapper.update(userInDb);
            return true;
        }
        return false;
    }


    public String getEncryptPassword(String userPassword) {
        final String salt = "zxy";
        return DigestUtils.md5DigestAsHex(
                (userPassword + salt)
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    public LoginUserVO getUserVO(User user) {
        if(user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }
}
