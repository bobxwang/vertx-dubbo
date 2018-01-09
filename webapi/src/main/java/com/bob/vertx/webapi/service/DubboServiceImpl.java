package com.bob.vertx.webapi.service;

import com.bob.vertx.webapi.Constants;
import com.bob.vertx.webapi.Configs;
import com.bob.wd.InvokeException;
import com.bob.wd.consumer.Objectex;
import com.bob.wd.consumer.RuleHolder;
import com.bob.wd.consumer.UniqueServiceDef;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.serviceproxy.ServiceException;
import scala.Console;

import java.util.Map;

/**
 * Created by wangxiang on 17/12/4.
 */
public class DubboServiceImpl implements DubboService {

    private Logger logger = LoggerFactory.getLogger(DubboServiceImpl.class);

    private Vertx vertx;
    private CircuitBreaker breaker;

    public DubboServiceImpl(Vertx vertx) {
        this.vertx = vertx;
        this.breaker = CircuitBreaker.create("my-circuit-breaker", vertx,
                new CircuitBreakerOptions()
                        .setMaxFailures(5)          // 最大故障次数
                        .setTimeout(2000)           // 超时时间
                        .setFallbackOnFailure(true) // 设置是否失败回调
                        .setResetTimeout(10000)     // 重置状态超时
        );
    }

    @Override
    public void invoke(String body, Handler<AsyncResult<String>> resultHandler) {

        JsonObject jsonObject = new JsonObject(body);

        UniqueServiceDef temp = new UniqueServiceDef();
        temp.setInterfaceName(jsonObject.getString("interfaceName"));
        temp.setMethod(jsonObject.getString("method"));
        temp.setVersion(jsonObject.getString("version", "v"));
        temp.setGroup(jsonObject.getString("group", "g"));
        temp = RuleHolder.find(RuleHolder.generateKey(temp));
        if (temp == null) {
            // error, u can to find from db and registe to rulehodler depend u logic, here just a example
            resultHandler.handle(ServiceException.fail(Constants.RULE_NOT_EXIST, "路由未找到请重新配置"));
        } else {

            final UniqueServiceDef uniqueServiceDef = temp;
            final Map<String, Object> map = Objectex.convertJson2Map(uniqueServiceDef, body);

            breaker.<String>execute(x -> {
                try {
                    Map<String, Object> object = (Map) Configs.dubboConsumer.invoke(uniqueServiceDef, map);
                    String jo = new JsonObject(object)
                            .put("execthread", Thread.currentThread().getName()).encode();
                    x.complete(jo);
                } catch (InvokeException ie) {
                    logger.error(Console.RED() + ie.getMessage() + Console.RESET(), ie);
                    x.fail(ie);
                }
            }).setHandler(ar -> {
                if (ar.succeeded()) {
                    resultHandler.handle(Future.succeededFuture(ar.result()));
                } else {
                    Throwable throwable = ar.cause();
                    resultHandler.handle(ServiceException.fail(Constants.INVOKE_DUBBO_ERROCODE, throwable.getMessage()));
                }
            });
        }
    }
}