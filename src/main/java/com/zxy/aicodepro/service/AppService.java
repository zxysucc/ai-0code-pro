package com.zxy.aicodepro.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.zxy.aicodepro.model.dto.AppQueryRequest;
import com.zxy.aicodepro.model.entity.App;
import com.zxy.aicodepro.model.entity.User;
import com.zxy.aicodepro.model.vo.AppVO;
import com.zxy.aicodepro.model.vo.LoginUserVO;
import reactor.core.publisher.Flux;

/**
 * 应用 服务层。
 *
 * @author zxy
 */
public interface AppService extends IService<App> {

    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    AppVO getAppVO(App app);

    Page<AppVO> getAppVOPage(Page<App> appPage);

    Flux<String> chatToGenCode(Long appId, String userMsg, User loginUser);
}
