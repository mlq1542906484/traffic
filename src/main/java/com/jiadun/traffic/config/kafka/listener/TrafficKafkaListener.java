package com.jiadun.traffic.config.kafka.listener;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jiadun.traffic.controller.vo.ModelWarnContentVo;
import com.jiadun.traffic.service.TrafficExecutorService;

/**
 * 
 * @author MLQ
 *
 * 2018年10月30日
 */
@Component
public class TrafficKafkaListener {

	private static Logger log = LoggerFactory.getLogger(TrafficKafkaListener.class);
	
	@Autowired
	private TrafficExecutorService trafficExecutorService;
    /**
     * kafka监听
     * @param record
     */
    @KafkaListener(topics = "${topic.traffic.model_task}")
    public void addWarningRecord(ConsumerRecord<?, ?> record) {
        log.info("消费者kafka的key={}  消费者kafka的value={} ", record.key(), record.value().toString());
        try {
        	JSONObject req = JSON.parseObject(record.value().toString(), JSONObject.class);
        	Long id = Long.valueOf((Integer) req.get("id"));
        	List<String> features = (List<String>) req.get("featureList");
        	
        	String isOpen = (String) req.get("isOpen");
        	ModelWarnContentVo contentVo = new ModelWarnContentVo();
        	if(req.get("content") != null) {
        		contentVo = JSON.parseObject(req.get("content").toString(), ModelWarnContentVo.class);
        	}
        	// 执行定时任务
        	if(TrafficExecutorService.map.get(id) != null) {
        		TrafficExecutorService.map.get(id).shutdownNow();
        	}
        	
        	if("open".equals(isOpen)) {
        		// 正式 start
        		String booking = contentVo.getBooking();
        		/*if(booking.contains("2")) { // 2：汽车票
        			contentVo.setBooking("2");
        			trafficExecutorService.excute(id, features , contentVo);
        		}
        		if(booking.contains("3")) { // 3：飞机票
        			contentVo.setBooking("3");
        			trafficExecutorService.excute(id, features , contentVo);
        		}
        		if(booking.contains("1")) { // 1：火车票
        			contentVo.setBooking("1");
        			trafficExecutorService.excute(id, features , contentVo);
        		}*/
        		// 正式 end
        		
        		// 测试 start
        		if(booking.contains("2")) { // 2：汽车票
        			String result ="{\"booking\":\"2\",\"id\":25,\"list\":[{\"LKXM\":\"杨中琛\",\"MZ\":\"汉族\",\"ZJLX\":\"1\",\"ZJHM\":\"51303019970702261X\",\"GPRQSJ\":\"2018-10-21 16:39:00\",\"GPFS\":\"方式1\",\"GPDD\":\"烟台\",\"GPZ\":\"烟台\",\"CC\":\"T1\",\"SCRYXM\":\"张三\",\"SCRYLXFS\":\"18888888888\",\"CFZ\":\"福山\",\"FCRQSJ\":\"2018-10-21\",\"DDZ\":\"济南\",\"SFLSXFC\":\"是\",\"PMSJ\":\"2018-10-21\"}]}";
        			trafficExecutorService.testSend(result);
        		}
        		if(booking.contains("3")) { // 3：飞机票
        			String result ="{\"booking\":\"3\",\"id\":25,\"list\":[{\"PAS_LST_NM\":\"YANGZHONGCHEN\",\"D_RKSJ\":\"2018-10-21 06:09:52\",\"OPERATEDATE\":\"20181019\",\"SUB_CLS_CD\":\"Y\",\"PNR_CR_DT\":\"20181019\",\"PAS_ID\":\"1\",\"OPR_STAT_CD\":\"DL\",\"PN_SEAT\":\"1\",\"CT_DT\":\"20181021 04:09:19\",\"RSP_OFC_CD\":\"PEK223\",\"AIR_SEG_FLT_NBR\":\"4512\",\"PAS_ID_TYPE\":\"I\",\"AIR_SEG_DPT_DT_LCL\":\"20181020\",\"OFFC_CD\":\"PEK223\",\"PAS_ID_NBR\":\"51303019970702261X\",\"AIR_CARR_CD\":\"CA中国国际航空公司\",\"FILENAME\":\"FuturePNR_20181021.txt.Z\",\"GRP_IND\":\"N\",\"RSP_AIRLN_CD\":\"CA\",\"AIR_SEG_DPT_AIRPT_CD\":\"TAO青岛流亭国际机场\",\"SYSTEMID\":\"MHDZ818578554\",\"PAS_CHN_NM\":\"杨中琛\",\"OPERATETIME\":\"16:44:00\",\"AIR_SEG_ARRV_AIRPT_CD\":\"CTU成都双流国际机场\",\"AIR_SEG_DPT_TM_LCL\":\"10:50:00\",\"AIR_SEG_ARRV_DT_LCL\":\"20181020\",\"PNR_REF\":\"NEMMEF\",\"AIR_SEG_ARRV_TM_LCL\":\"14:10:00\"}]}";
        			trafficExecutorService.testSend(result);
        		}
        		if(booking.contains("1")) { // 1：火车票
        			String result = "{\"booking\":\"1\",\"id\":25,\"list\":[{\"ID_NO\":\"51303019970702261X\",\"ID_NAME\":\"杨中琛\",\"BZK_GXSJ\":\"2018-10-21 18:37:31\",\"FROM_STATION_NAME\":\"烟台\",\"SEAT_NO\":\"016F\",\"BZK_RKSJ\":\"2018-10-21 18:37:31\",\"TRAIN_DATE\":\"20181020\",\"TICKET_STATUS_ZW\":\"售票\",\"BOARD_TRAIN_CODE\":\"G472\",\"TICKET_STATUS\":\"Z\",\"TICKET_NO\":\"9680426\",\"DS\":\"370600\",\"DT\":\"20181020\",\"TO_STATION_NAME\":\"即墨北\",\"FILETIME\":\"2018-10-21 16:39:00\",\"ID_KIND\":\"1\",\"CZ\":\"I\",\"ID_KIND_ZW\":\"1\",\"FILENAME\":\"TIELU_370600_20181020.csv\",\"COACH_NO\":\"07\",\"KEY\":\"201810201855774869680426\",\"CZ_ZW\":\"新增\"}]}";
        			trafficExecutorService.testSend(result);
        		}
        		// 测试 end
        	}
        } catch (Exception e) {
            log.error("维稳通过kafka发送任务", e);
        }
    }

}
