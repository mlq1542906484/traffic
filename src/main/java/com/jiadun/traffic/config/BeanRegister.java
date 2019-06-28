package com.jiadun.traffic.config;


import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * bean注册
 * @author hcl
 */
@Slf4j
@Configuration
public class BeanRegister {


    /**
     * @describe: ElasticSearch
     * @author: hcl  
     * @date: 2018/4/25 17:34  
     * @param: []
     * @return org.elasticsearch.client.transport.TransportClient  
     */
    @Bean
    public TransportClient transportClient(ElasticSearchProperties elasticSearchProperties){
        /**
         * 1:通过 setting对象来指定集群配置信息
         */
        Settings setting = Settings.builder()
                .put("cluster.name", elasticSearchProperties.getClusterName())//指定集群名称
                .put("client.transport.sniff", true)//启动嗅探功能
                .put("thread_pool.search.size", elasticSearchProperties.getPoolSize())//增加线程池个数，暂时设为5
                .build();
        /**
         * 2：创建客户端
         * 通过setting来创建，若不指定则默认链接的集群名为elasticsearch
         * 链接使用tcp协议即9300
         */
        TransportClient transportClient = null;
        try{
            transportClient = new PreBuiltTransportClient(setting).addTransportAddress(new TransportAddress(InetAddress.getByName(elasticSearchProperties.getIp()), elasticSearchProperties.getPort()));
        }catch(UnknownHostException e){
            log.error("ElasticSearch 连接异常!",e);
            throw new RuntimeException("ElasticSearch 连接异常!",e);
        }
        return transportClient;
    }


}
