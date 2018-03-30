package com.bob.vertx.webapi;

import com.bob.vertx.facade.Who;
import com.bob.vertx.webapi.service.facadeimpl.WhoImpl;
import com.bob.wd.consumer.RuleHolder;
import com.bob.wd.consumer.UniqueServiceDef;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangxiang on 17/10/31.
 */
public class Runner {

    private static Logger logger = LoggerFactory.getLogger(Runner.class);

    public static void main(String[] args) {

        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");

//        Runner runner = new Runner();
//        runner.runDubboRegister();
//        runner.runDubboInvoke();

        VertxOptions vo = new VertxOptions();
        vo.setEventLoopPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        Vertx vertx = Vertx.vertx(vo);

        DeploymentOptions options = new DeploymentOptions();
        options.setInstances(Runtime.getRuntime().availableProcessors() * 2);
        vertx.deployVerticle(HttpVerticle.class.getName(), options);
    }

    /**
     * just a demo to register a dubbo service
     */
    @SuppressWarnings("unused")
    private void runDubboRegister() {
        Who who = new WhoImpl();
        Configs.dubboProvider.register(who, false);
    }

    /**
     * just a demo to consumer a dubbo service
     */
    @SuppressWarnings("unused")
    private void runDubboInvoke() {

        UniqueServiceDef temp = new UniqueServiceDef();
        temp.setInterfaceName("com.bob.vertx.facade.Who");
        temp.setMethod("currentUserId");
        String key = RuleHolder.generateKey(temp);
        UniqueServiceDef uniqueServiceDef = RuleHolder.find(key);
        if (uniqueServiceDef == null) {
            uniqueServiceDef = temp;
            RuleHolder.add(temp);
        }

        Map<String, Object> params = new HashMap<>();
        try {
            Object obj = Configs.dubboConsumer.invoke(uniqueServiceDef, params);
            logger.info("the result is :" + String.valueOf(obj));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}