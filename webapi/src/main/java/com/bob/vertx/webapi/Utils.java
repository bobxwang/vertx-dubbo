package com.bob.vertx.webapi;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Map;

/**
 * Created by wangxiang on 17/12/7.
 */
public final class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static void fireSingleMessageResponse(HttpServerResponse response, int statusCode) {
        response.setStatusCode(statusCode).end();
    }

    public static void fireSingleMessageResponse(HttpServerResponse response, int statusCode, String message) {
        response.setStatusCode(statusCode).end(message);
    }

    public static void fireJsonResponse(HttpServerResponse response, int statusCode, Map payload) {
        response.setStatusCode(statusCode)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(new JsonObject(payload).encodePrettily());
    }
}