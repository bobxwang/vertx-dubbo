package com.bob.vertx.webapi.service.facadeimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.bob.vertx.facade.Who;
import com.bob.vertx.facade.param.User;

import java.util.Date;

/**
 * Created by wangxiang on 17/12/14.
 */
@Service
public class WhoImpl implements Who {

    @Override
    public Long currentUserId() {
        return (new Date().getTime());
    }

    @Override
    public User map(Integer id) {
        User user = new User();
        user.setId(currentUserId().intValue());
        user.setName(new Date().toString());
        return user;
    }
}