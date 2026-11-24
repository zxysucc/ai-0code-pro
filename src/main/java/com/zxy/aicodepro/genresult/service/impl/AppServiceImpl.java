package com.zxy.aicodepro.genresult.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.zxy.aicodepro.genresult.entity.App;
import com.zxy.aicodepro.genresult.mapper.AppMapper;
import com.zxy.aicodepro.genresult.service.AppService;
import org.springframework.stereotype.Service;

/**
 * 应用 服务层实现。
 *
 * @author zxy
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{

}
