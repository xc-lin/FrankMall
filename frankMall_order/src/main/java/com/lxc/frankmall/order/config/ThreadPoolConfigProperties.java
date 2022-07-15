package com.lxc.frankmall.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Frank_lin
 * @date 2022/6/29
 */
@ConfigurationProperties(prefix = "frankmall.thread")
@Data
public class ThreadPoolConfigProperties {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;
}
