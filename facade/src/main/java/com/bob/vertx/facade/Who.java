package com.bob.vertx.facade;

import com.bob.vertx.facade.param.User;

/**
 * Created by wangxiang on 17/12/14.
 */
public interface Who {

    Long currentUserId();

    User findUserById(Integer id);

    User updateByUser(User user);

    User deleteByUser(User user, Integer id);
}