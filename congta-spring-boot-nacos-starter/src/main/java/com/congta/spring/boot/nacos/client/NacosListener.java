package com.congta.spring.boot.nacos.client;

import com.alibaba.nacos.api.config.listener.Listener;
import java.util.concurrent.Executor;

/**
 * Created by zhangfucheng on 2021/11/1.
 */
public interface NacosListener extends Listener {

    @Override
    default Executor getExecutor() {
        return null;
    }
}
