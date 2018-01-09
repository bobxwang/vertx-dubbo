package com.bob.vertx.webapi;

import com.bob.wd.DubboConsumerConfig;
import com.bob.wd.DubboProviderConfig;
import com.bob.wd.consumer.DubboConsumer;
import com.bob.wd.provider.DubboProvider;

/**
 * Created by wangxiang on 17/12/14.
 */
public final class Configs {

    public static final DubboConsumerConfig dubboConsumerConfig = new DubboConsumerConfig("bob.consumer.test", "zookeeper", "172.20.100.119:2181", 2500);

    public static final DubboProviderConfig dubboProviderConfig = new DubboProviderConfig(dubboConsumerConfig, 200, "dubbo", 20880);

    public static final DubboProvider dubboProvider = new DubboProvider(Configs.dubboProviderConfig);

    public static final DubboConsumer dubboConsumer = new DubboConsumer(Configs.dubboConsumerConfig);
}