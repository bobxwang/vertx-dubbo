package com.bob.vertx.webapi;

import com.bob.vertx.webapi.service.DubboService;
import com.bob.vertx.webapi.verticle.DubboVerticle;
import com.bob.wd.consumer.RuleHolder;
import com.bob.wd.consumer.UniqueServiceDef;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.circuitbreaker.HystrixMetricHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangxiang on 17/11/30.
 */
public class HttpVerticle extends AbstractVerticle {

    private Logger logger = LoggerFactory.getLogger(HttpVerticle.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        DubboService dubboService = DubboService.createProxy(vertx, Constants.HTTP_2_DUBBO_ADDRESS);

        Router router = Router.router(vertx);
        router.route()
                .handler(BodyHandler.create().setBodyLimit(50 * 1048576L))
                .handler(ResponseTimeHandler.create())
                .handler(LoggerHandler.create());

        router.get("/hystrix-metrics").handler(HystrixMetricHandler.create(vertx));

        String path = "/api/gateway";
        router.post(path).handler(r -> {
            String body = r.getBodyAsString();
            dubboService.invoke(body, reply -> {
                if (reply.succeeded()) {
                    JsonObject rs = new JsonObject(reply.result());
                    r.response()
                            .putHeader("content-type", "application/json;charset=UTF-8")
                            .end(new JsonObject(rs.getMap())
                                    .put("execthread", rs.getValue("execthread"))
                                    .put("httpthread", Thread.currentThread().getName())
                                    .encodePrettily());
                } else {
                    r.fail(reply.cause());
                }
            });
        });
        router.get(path).handler(r -> {
            Map<String, UniqueServiceDef> o = RuleHolder.getMap();
            Map<String, Object> otemp = new HashMap<>();
            for (Map.Entry<String, UniqueServiceDef> temp : o.entrySet()) {
                otemp.put(temp.getKey(), temp.getValue().toExample());
            }
            String v = "";
            try {
                v = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(otemp);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            r.response().putHeader("content-type", "application/json;charset=UTF-8")
                    .end(v);
        });

        addFailureHandler(router);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(config().getInteger("http.port", 8080), r -> {
                    if (r.succeeded()) {
                        logger.info("service is started and listen on 8080 port");
                    } else {
                        logger.error("service started failed with reason \n" + r.cause().getMessage(), r.cause());
                    }
                });

        Future<String> dubboVerticle = Future.future();
        vertx.deployVerticle(DubboVerticle.class.getName(),
                new DeploymentOptions().setWorker(true)
                        .setInstances(Runtime.getRuntime().availableProcessors() * 4)
                        .setWorkerPoolName("the-work-pool"), dubboVerticle.completer());
        dubboVerticle.setHandler(ar -> {
            if (ar.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(ar.cause());
            }
        });
    }

    private void addFailureHandler(final Router router) {

        router.route().failureHandler(routingContext -> {
            if (routingContext.response().ended()) {
                return;
            }

            int statusCode = routingContext.statusCode() == -1 ? 500 : routingContext.statusCode();
            logger.error("Got [{}] during processing [{}], status code: {}. ",
                    routingContext.response().getStatusMessage(), routingContext.request().absoluteURI(),
                    statusCode, routingContext.failure());

            Map<String, String> payload = new HashMap<>();
            payload.put("error", routingContext.failure().getMessage());
            payload.put("path", routingContext.request().path());
            payload.put("method", routingContext.request().rawMethod());
            Utils.fireJsonResponse(routingContext.response(), statusCode, payload);
        });
    }
}