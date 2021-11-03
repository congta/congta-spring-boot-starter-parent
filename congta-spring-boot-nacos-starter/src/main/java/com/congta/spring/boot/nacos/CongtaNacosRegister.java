package com.congta.spring.boot.nacos;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Fucheng
 * created in 2021/11/2
 */
@Component
public class CongtaNacosRegister implements InitializingBean {

    private static Logger log = LoggerFactory.getLogger(CongtaNacosRegister.class);

    @NacosInjected
    private NamingService namingService;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private Integer serverPort;

    @Override
    public void afterPropertiesSet() throws Exception {
        String ip = NetUtils.localIP();
        namingService.registerInstance(applicationName, ip, serverPort);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                namingService.deregisterInstance(applicationName, ip, serverPort);
            } catch (NacosException e) {
                log.warn("exception when deregisterInstance to nacos", e);
            }
        }));
    }
}
