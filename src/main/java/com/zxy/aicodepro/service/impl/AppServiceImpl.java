package com.zxy.aicodepro.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.zxy.aicodepro.ai.Ai0CodeGeneratorFacade;
import com.zxy.aicodepro.ai.enums.Ai0CodeGenTypeEnum;
import com.zxy.aicodepro.common.ErrorCode;
import com.zxy.aicodepro.exception.ThrowUtils;
import com.zxy.aicodepro.mapper.AppMapper;
import com.zxy.aicodepro.model.dto.AppQueryRequest;
import com.zxy.aicodepro.model.entity.App;
import com.zxy.aicodepro.model.entity.User;
import com.zxy.aicodepro.model.vo.AppVO;
import com.zxy.aicodepro.model.vo.UserVO;
import com.zxy.aicodepro.service.AppService;
import com.zxy.aicodepro.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author zxy
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    private static final Set<String> SORT_FIELD_WHITELIST = Set.of(
            "id", "appName", "codeGenType", "deployKey", "deployedTime",
            "priority", "userId", "editTime", "createTime", "updateTime"
    );

    @Resource
    private UserService userService;

    @Resource
    private Ai0CodeGeneratorFacade ai0CodeGeneratorFacade;

    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("id", appQueryRequest.getId(), appQueryRequest.getId() != null)
                .like("appName", appQueryRequest.getAppName(), StrUtil.isNotBlank(appQueryRequest.getAppName()))
                .like("cover", appQueryRequest.getCover(), StrUtil.isNotBlank(appQueryRequest.getCover()))
                .like("initPrompt", appQueryRequest.getInitPrompt(), StrUtil.isNotBlank(appQueryRequest.getInitPrompt()))
                .eq("codeGenType", appQueryRequest.getCodeGenType(), StrUtil.isNotBlank(appQueryRequest.getCodeGenType()))
                .eq("deployKey", appQueryRequest.getDeployKey(), StrUtil.isNotBlank(appQueryRequest.getDeployKey()))
                .eq("priority", appQueryRequest.getPriority(), appQueryRequest.getPriority() != null)
                .eq("userId", appQueryRequest.getUserId(), appQueryRequest.getUserId() != null);
        String sortField = appQueryRequest.getSortField();
        if (StrUtil.isNotBlank(sortField) && SORT_FIELD_WHITELIST.contains(sortField)) {
            queryWrapper.orderBy(sortField, "ascend".equals(appQueryRequest.getSortOrder()));
        }
        return queryWrapper;
    }

    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = toAppVO(app);
        User user = userService.getById(app.getUserId());
        appVO.setUser(userService.getUserVO(user));
        return appVO;
    }

    @Override
    public Page<AppVO> getAppVOPage(Page<App> appPage) {
        if (appPage == null) {
            return null;
        }
        if (CollUtil.isEmpty(appPage.getRecords())) {
            return appPage.map(this::toAppVO);
        }
        Set<Long> userIds = appPage.getRecords().stream()
                .map(App::getUserId)
                .filter(userId -> userId != null)
                .collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userIds.isEmpty()
                ? Collections.emptyMap()
                : userService.listByIds(userIds).stream().collect(Collectors.toMap(
                User::getId,
                userService::getUserVO,
                (first, second) -> first
        ));
        return appPage.map(app -> {
            AppVO appVO = toAppVO(app);
            appVO.setUser(userVOMap.get(app.getUserId()));
            return appVO;
        });
    }

    @Override
    public Flux<String> chatToGenCode(Long appId, String userMsg, User loginUser) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StrUtil.isBlank(userMsg), ErrorCode.PARAMS_ERROR, "用户消息不能为空");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        App app = getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "无权限访问");
        Ai0CodeGenTypeEnum codeGenType = Ai0CodeGenTypeEnum.getValue(app.getCodeGenType());
        ThrowUtils.throwIf(codeGenType == null, ErrorCode.PARAMS_ERROR, "不支持的代码生成类型");
        return ai0CodeGeneratorFacade.generatorAndSaveCodeStream(codeGenType, userMsg, appId);
    }

    private AppVO toAppVO(App app) {
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        return appVO;
    }

}
