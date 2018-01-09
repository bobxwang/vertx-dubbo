package com.bob.vertx.webapi.service;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ServiceProxyBuilder;

/**
 * Created by wangxiang on 17/12/4.
 */
@ProxyGen
public interface DubboService {

    static DubboService create(Vertx vertx) {
        return new DubboServiceImpl(vertx);
    }

    static DubboService createProxy(Vertx vertx, String address) {
        return (new ServiceProxyBuilder(vertx)).setAddress(address).build(DubboService.class);
    }

    void invoke(String body, Handler<AsyncResult<String>> resultHandler);
}