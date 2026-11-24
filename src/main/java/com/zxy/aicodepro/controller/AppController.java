package com.zxy.aicodepro.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import com.zxy.aicodepro.ai.enums.Ai0CodeGenTypeEnum;
import com.zxy.aicodepro.annotation.AuthCheck;
import com.zxy.aicodepro.common.BaseResponse;
import com.zxy.aicodepro.common.DeleteRequest;
import com.zxy.aicodepro.common.ErrorCode;
import com.zxy.aicodepro.common.ResultUtils;
import com.zxy.aicodepro.exception.BusinessException;
import com.zxy.aicodepro.exception.ThrowUtils;
import com.zxy.aicodepro.model.dto.AppAddRequest;
import com.zxy.aicodepro.model.dto.AppAdminUpdateRequest;
import com.zxy.aicodepro.model.dto.AppQueryRequest;
import com.zxy.aicodepro.model.dto.AppUpdateRequest;
import com.zxy.aicodepro.model.entity.App;
import com.zxy.aicodepro.model.entity.User;
import com.zxy.aicodepro.model.vo.AppVO;
import com.zxy.aicodepro.service.AppService;
import com.zxy.aicodepro.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

import static com.zxy.aicodepro.constant.UserConstant.ADMIN;

@RestController
@RequestMapping("/app")
public class AppController {

    private static final int MAX_PAGE_SIZE = 20;

    private static final int GOOD_APP_PRIORITY = 99;

    @Resource
    private AppService appService;

    @Resource
    private UserService userService;

    @GetMapping(value = "/chat/gen/code", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatToGenCode(@RequestParam Long appId, @RequestParam String userMsg, HttpServletRequest request) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StrUtil.isBlank(userMsg), ErrorCode.PARAMS_ERROR, "用户消息不能为空");
        User loginUser = userService.getLoginUser(request);
        Flux<String> stringFlux = appService.chatToGenCode(appId, userMsg, loginUser);
        return stringFlux.map(chunk -> {
            Map<String,String> map = Map.of("d",chunk);
            String str = JSONUtil.toJsonStr(map);
            return ServerSentEvent.<String>builder().data(str).build();
        }).concatWith(Mono.just(
                ServerSentEvent.<String>builder().event("end").data("").build()
        ));
    }


    @PostMapping("/add")
    public BaseResponse<Long> addApp(@RequestBody AppAddRequest appAddRequest,
                                     HttpServletRequest request) {
        ThrowUtils.throwIf(appAddRequest == null, ErrorCode.PARAMS_ERROR);
        String initPrompt = StrUtil.trim(appAddRequest.getInitPrompt());
        ThrowUtils.throwIf(StrUtil.isBlank(initPrompt), ErrorCode.PARAMS_ERROR, "初始化 prompt 不能为空");
        User loginUser = userService.getLoginUser(request);
        App app = new App();
        app.setInitPrompt(initPrompt);
        app.setUserId(loginUser.getId());
        app.setAppName(initPrompt.substring(0, Math.min(initPrompt.length(), 12)));
        app.setCodeGenType(Ai0CodeGenTypeEnum.MULTI_FILE.getType());
        boolean result = appService.save(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(app.getId());
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateApp(@RequestBody AppUpdateRequest appUpdateRequest,
                                           HttpServletRequest request) {
        ThrowUtils.throwIf(appUpdateRequest == null || appUpdateRequest.getId() == null
                || appUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StrUtil.isBlank(appUpdateRequest.getAppName()),
                ErrorCode.PARAMS_ERROR, "应用名称不能为空");
        User loginUser = userService.getLoginUser(request);
        App oldApp = appService.getById(appUpdateRequest.getId());
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        if (!oldApp.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        App app = new App();
        app.setId(appUpdateRequest.getId());
        app.setAppName(StrUtil.trim(appUpdateRequest.getAppName()));
        app.setEditTime(LocalDateTime.now());
        boolean result = appService.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteApp(@RequestBody DeleteRequest deleteRequest,
                                           HttpServletRequest request) {
        Long id = getValidId(deleteRequest);
        User loginUser = userService.getLoginUser(request);
        App oldApp = appService.getById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        if (!oldApp.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = appService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @GetMapping("/get/vo")
    public BaseResponse<AppVO> getAppVOById(@RequestParam long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(appService.getAppVO(app));
    }

    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<AppVO>> listMyAppVOByPage(@RequestBody AppQueryRequest appQueryRequest,
                                                       HttpServletRequest request) {
        validatePageRequest(appQueryRequest, true);
        User loginUser = userService.getLoginUser(request);
        appQueryRequest.setUserId(loginUser.getId());
        Page<App> appPage = appService.page(
                Page.of(appQueryRequest.getPageNum(), appQueryRequest.getPageSize()),
                appService.getQueryWrapper(appQueryRequest)
        );
        return ResultUtils.success(appService.getAppVOPage(appPage));
    }

    @PostMapping("/good/list/page/vo")
    public BaseResponse<Page<AppVO>> listGoodAppVOByPage(@RequestBody AppQueryRequest appQueryRequest) {
        validatePageRequest(appQueryRequest, true);
        appQueryRequest.setPriority(GOOD_APP_PRIORITY);
        Page<App> appPage = appService.page(
                Page.of(appQueryRequest.getPageNum(), appQueryRequest.getPageSize()),
                appService.getQueryWrapper(appQueryRequest)
        );
        return ResultUtils.success(appService.getAppVOPage(appPage));
    }

    @PostMapping("/admin/delete")
    @AuthCheck(role = ADMIN)
    public BaseResponse<Boolean> deleteAppByAdmin(@RequestBody DeleteRequest deleteRequest) {
        Long id = getValidId(deleteRequest);
        App oldApp = appService.getById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = appService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/admin/update")
    @AuthCheck(role = ADMIN)
    public BaseResponse<Boolean> updateAppByAdmin(
            @RequestBody AppAdminUpdateRequest appAdminUpdateRequest) {
        ThrowUtils.throwIf(appAdminUpdateRequest == null || appAdminUpdateRequest.getId() == null
                || appAdminUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        App oldApp = appService.getById(appAdminUpdateRequest.getId());
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        App app = new App();
        BeanUtil.copyProperties(appAdminUpdateRequest, app);
        boolean result = appService.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @GetMapping("/admin/get/vo")
    @AuthCheck(role = ADMIN)
    public BaseResponse<AppVO> getAppVOByIdByAdmin(@RequestParam long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(appService.getAppVO(app));
    }

    @PostMapping("/admin/list/page/vo")
    @AuthCheck(role = ADMIN)
    public BaseResponse<Page<AppVO>> listAppVOByPageByAdmin(
            @RequestBody AppQueryRequest appQueryRequest) {
        validatePageRequest(appQueryRequest, false);
        Page<App> appPage = appService.page(
                Page.of(appQueryRequest.getPageNum(), appQueryRequest.getPageSize()),
                appService.getQueryWrapper(appQueryRequest)
        );
        return ResultUtils.success(appService.getAppVOPage(appPage));
    }

    private Long getValidId(DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null
                || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return deleteRequest.getId();
    }

    private void validatePageRequest(AppQueryRequest appQueryRequest, boolean limitPageSize) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        int pageNum = appQueryRequest.getPageNum();
        int pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageNum <= 0 || pageSize <= 0, ErrorCode.PARAMS_ERROR);
        if (limitPageSize) {
            ThrowUtils.throwIf(pageSize > MAX_PAGE_SIZE, ErrorCode.PARAMS_ERROR);
        }
    }
}
