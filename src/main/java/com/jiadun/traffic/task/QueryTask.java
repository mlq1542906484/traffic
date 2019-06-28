package com.jiadun.traffic.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.jiadun.traffic.config.TrafficConfig;
import com.jiadun.traffic.config.webservice.WebserviceClient;

@Component
public class QueryTask {

	private static Logger log = LoggerFactory.getLogger(QueryTask.class);
	
	@Autowired
    private TransportClient transportClient;
	
	@Autowired
	private WebserviceClient webserviceClient;
	
	@Scheduled(cron = "0 0/10 * * * *")
//	@Scheduled(cron = "0 0 8 * * ? *")
    public void scheduled() {
        log.info("Task start");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        String startDate = sdf.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String endDate = sdf.format(cal.getTime());
        QueryBuilder query = QueryBuilders.rangeQuery("entry_time")
                .from(startDate)
                .to(endDate)
                .includeLower(true)// 包含上界
                .includeUpper(false);
        SearchResponse searchResponse = transportClient
                .prepareSearch(TrafficConfig.resIndex)
                .setTypes("doc")
                .setQuery(query)
                .setFrom(1).setSize(9999)
                .get();
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        Set<String> idcards = new HashSet<>();
        for(SearchHit searchHit : searchHits){
            Map<String,String> objectData = new HashMap<>();
            objectData.put("_id",searchHit.getId());
            Map<String,Object> data = searchHit.getSourceAsMap();
            idcards.add(data.get("gmsfhm").toString());
        }
        System.out.println(idcards);
        for(String idcard : idcards) {
    		String requestContent = JSON.toJSONString(idcard);
        	String result = webserviceClient.getData(requestContent);
        }
    }
	
}
