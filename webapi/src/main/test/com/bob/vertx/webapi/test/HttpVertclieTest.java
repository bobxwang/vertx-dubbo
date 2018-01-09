package com.bob.vertx.webapi.test;

import com.bob.vertx.webapi.HttpVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by wangxiang on 17/12/15.
 */
@RunWith(VertxUnitRunner.class)
public class HttpVertclieTest {

    private Vertx vertx;
    private DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", 8080));

    @Before
    public void prepare(TestContext context) throws InterruptedException {

        vertx = Vertx.vertx();
        vertx.deployVerticle(new HttpVerticle(), options, context.asyncAssertSuccess());
    }

    @After
    public void finish(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testGetApi(TestContext context) {

        final Async async = context.async();

        WebClient webClient = WebClient.create(vertx);
        webClient.get(8080, "localhost", "/api/gateway").send(r -> {
            if (r.succeeded()) {
                Assert.assertEquals(200, r.result().statusCode());
                JsonObject jsonObject = r.result().bodyAsJsonObject();
                Assert.assertNotNull(jsonObject);
                System.out.println(jsonObject.encodePrettily());
                async.complete();
            } else {
                Assert.assertNotEquals(200, r.result().statusCode());
                System.out.println(r.cause().getMessage());
                async.complete();
            }
        });

        async.awaitSuccess(10 * 1000);
    }

    @Test
    public void testPostApi2Dubbo(TestContext context) {

        final Async async = context.async();

        WebClient webClient = WebClient.create(vertx);
        webClient.post(8080, "localhost", "/api/gateway")
                .sendJsonObject(new JsonObject()
                        .put("interfaceName", "com.weidai.sso.client.api.UserFacade")
                        .put("method", "getUserById")
                        .put("userId", 32)
                        .put("version", "2.0"), rs -> {
                    if (rs.succeeded()) {
                        JsonObject jsonObject = rs.result().bodyAsJsonObject();
                        Assert.assertNotNull(jsonObject);
                        System.out.println(jsonObject.encodePrettily());
                        async.complete();
                    } else {
                        Assert.assertNotEquals(200, rs.result().statusCode());
                        async.complete();
                    }
                });

        async.awaitSuccess(10 * 1000);
    }
}