package com.jiadun.traffic.config;

import org.elasticsearch.client.transport.TransportClient;
import org.springframework.stereotype.Component;

/***********************************
 * 
 * @author zhoushaowei 
 * @version 1.0.3
 * 连接es集群，并创建缓冲池，重写预更新和预删除方法
 * 
 ***********************************/
@Component
public class ElasticSearchOperator {
	// 缓冲池容量
	private int MAX_BULK_COUNT;// = 100;//Integer.parseInt(GetProperTiesUtils.getProperties("MAX_BULK_COUNT"));
	// 最大提交间隔（秒）
	private int MAX_COMMIT_INTERVAL;// = 120;//Integer.parseInt(GetProperTiesUtils.getProperties("MAX_COMMIT_INTERVAL"));

	private TransportClient client = null;

	public ElasticSearchOperator(TransportClient client,ElasticSearchProperties elasticSearchProperties){
		this.client = client;
//		bulkRequestBuilder = client.prepareBulk();
		MAX_BULK_COUNT = elasticSearchProperties.getMaxBulkCount();
		MAX_COMMIT_INTERVAL = elasticSearchProperties.getMaxCommitInterval();
//		new Timer().schedule(new CommitTimer(this), 10 * 1000, MAX_COMMIT_INTERVAL * 1000);
	}

}
