package com.jiadun.traffic.config.kafka.sender;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jiadun.traffic.controller.TrafficController;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

/**
 * 
 * @author MLQ
 *
 * 2018年10月30日
 */
@Component
public class TrafficKafkaSender {

    @Autowired
    private KafkaTemplate kafkaTemplate;
    
    @Value(value = "${topic.traffic.task_return}")
    private String topic;

    private static Logger log = LoggerFactory.getLogger(TrafficKafkaSender.class);

    public void send() {
        //组装发送到kafka的消息,将管控任务发送给kafka
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", "12");
            jsonObject.put("booking", "2");
//            jsonObject.put("list", value);
            
            
            JSONObject modelDetailObject = JSON.parseObject(null);
            jsonObject.put("modelDetail", modelDetailObject);
            
            log.info("kafka的topic={}, 消息={}", topic, JSON.toJSONString(jsonObject));
            kafkaTemplate.send(topic,JSON.toJSONString(jsonObject));
            log.info("发送kafka成功.");
        } catch (Exception e) {
            log.error("发送kafka出错", e);
        }
    }
}
