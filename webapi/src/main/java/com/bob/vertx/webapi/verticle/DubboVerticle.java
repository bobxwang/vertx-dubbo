package com.bob.vertx.webapi.verticle;

import com.bob.vertx.webapi.Constants;
import com.bob.vertx.webapi.service.DubboService;
import io.vertx.core.AbstractVerticle;
import io.vertx.serviceproxy.ProxyHelper;

/**
 * Created by wangxiang on 17/11/30.
 */
public class DubboVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {

        ProxyHelper.registerService(DubboService.class, vertx, DubboService.create(vertx), Constants.HTTP_2_DUBBO_ADDRESS);
    }
}