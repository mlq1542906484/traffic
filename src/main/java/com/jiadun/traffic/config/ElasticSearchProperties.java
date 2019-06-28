package com.jiadun.traffic.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @describe: ElasticSearch配置
 * @author: hcl  
 * @date: 2018/4/25 17:38
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix="eagle.elasticsearch")
public class ElasticSearchProperties {

    /**
     * @describe: ES ip
     */
    private String ip;

    /**
     * @describe: ES 端口
     */
    private Integer port;

    /**
     * @describe: cat 端口
     */
    private Integer catPort;

    /**
     * @describe: ES 集群名称
     */
    private String clusterName;

    /**
     * @describe: 连接数
     */
    private Integer poolSize;


    /**
     * @describe: 缓冲池容量
     */
    private Integer maxBulkCount;

    /**
     * @describe: 最大提交间隔（秒）
     */
    private Integer maxCommitInterval;

}
