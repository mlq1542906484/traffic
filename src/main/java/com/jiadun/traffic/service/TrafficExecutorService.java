package com.jiadun.traffic.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jiadun.traffic.config.TrafficConfig;
import com.jiadun.traffic.config.webservice.WebserviceClient;
import com.jiadun.traffic.controller.vo.ModelWarnContentVo;

@Service
public class TrafficExecutorService {
	
	public static volatile Map<Long, ScheduledExecutorService> map = new HashMap<>();
	
	private static Logger log = LoggerFactory.getLogger(TrafficExecutorService.class);
	
	@Autowired
	private KafkaTemplate kafkaTemplate;
	
	@Value("${topic.traffic.task_return}")
    private String topic;
	
	@Autowired
	private WebserviceClient webserviceClient;
	
	@Value("${stability.queryAll}")
	private String url;
	
	@SuppressWarnings("unchecked")
	public void queryAll() {
		RestTemplate restTemplate = new RestTemplate();
		Map<String, Object> resultMap = restTemplate.getForObject(url, Map.class);
//		Map<String, Object> resultMap = restTemplate.getForObject("http://192.168.12.7:9300/law/control/mangement/getAllTicketTask", Map.class);
		List<Map<String, Object>> data = (List<Map<String, Object>>) resultMap.get("data");
		for(Map<String, Object> req : data) {
			
			Long id = Long.valueOf((Integer) req.get("id"));
			
        	List<Map> featureList = JSON.parseArray(req.get("feature_list").toString(), Map.class);
        	List<String> features = new LinkedList<>();
        	if(!CollectionUtils.isEmpty(featureList)) {
        		for(Map<String, String> map : featureList) {
            		features.add(map.get("gmsfhm"));
            	}
        	}
        	
        	String isOpen = (String) req.get("isOpen");
        	ModelWarnContentVo contentVo = new ModelWarnContentVo();
        	if(req.get("warn_content") != null) {
        		contentVo = JSON.parseObject(req.get("warn_content").toString(), ModelWarnContentVo.class);
        	}
			if("open".equals(isOpen)) {
				String booking = contentVo.getBooking();
				
				if(booking.contains("1")) { // 1：火车票
        			contentVo.setBooking("1");
        			excute(id, features , contentVo);
        		}
        		if(booking.contains("2")) { // 2：汽车票  
        			contentVo.setBooking("2");
        			excute(id, features , contentVo);
        		}
        		if(booking.contains("3")) { // 3：飞机票
        			contentVo.setBooking("3");
        			excute(id, features , contentVo);
        		}
        	}
		}
		log.info("查询全部接口参数:{}",JSON.toJSONString(data));
	}
	
	public void excute(Long taskId, List<String> featureList, ModelWarnContentVo contentVo) {
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		Runnable runnable = new Runnable() {
			int i = 1;
			public void run() {
				Calendar cal = Calendar.getInstance();
				Date now = cal.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
				String date = sdf.format(now);
				
				cal.add(Calendar.YEAR, 0);
				cal.add(Calendar.DAY_OF_YEAR, 0);
				cal.add(Calendar.HOUR_OF_DAY, 0);
				cal.add(Calendar.MINUTE, 0);
				int period = Integer.valueOf(contentVo.getAlarmNum());
				if ("minute".equals(contentVo.getAlarmFrequency())) {
					cal.add(Calendar.MINUTE, -period);
				} else if ("hours".equals(contentVo.getAlarmFrequency())) {
					cal.add(Calendar.HOUR_OF_DAY, -period);
				} else if ("day".equals(contentVo.getAlarmFrequency())) {
					cal.add(Calendar.DAY_OF_YEAR, -period);
				} else if ("week".equals(contentVo.getAlarmFrequency())) {
					period = 7 * period;
					cal.add(Calendar.DAY_OF_YEAR, -period);
				}
				String startDate = sdf.format(cal.getTime());
				String endDate = date;
				if(!CollectionUtils.isEmpty(featureList)) {
					for(String feature : featureList) {
						String result = webserviceClient.query(feature, contentVo, startDate ,endDate);
						log.info("webservice返回结果"+ result);
						sendToKafka(taskId, i++, date ,result);
					}
				}
			}
		};
		//command--执行的任务,initialDelay--延迟开始,period--间隔时间,unit--时间单位
		TimeUnit timeUnit = null;
		Long period = Long.valueOf(contentVo.getAlarmNum());
		if("minute".equals(contentVo.getAlarmFrequency())) {
			timeUnit = TimeUnit.MINUTES;
		} else if("hours".equals(contentVo.getAlarmFrequency())) {
			timeUnit = TimeUnit.HOURS;
		} else if("day".equals(contentVo.getAlarmFrequency())) {
			timeUnit = TimeUnit.DAYS;
		} else if("week".equals(contentVo.getAlarmFrequency())) {
			timeUnit = TimeUnit.DAYS;
			period = 7 * period;
		} else {
			timeUnit = TimeUnit.SECONDS;
		}
		service.scheduleAtFixedRate(runnable, 0, period, timeUnit);
		map.put(taskId, service);
	}
	
	private void sendToKafka(Long taskId, int i ,String date, String result) {
		if (StringUtils.isBlank(topic)) {
            return;
        }
		Map<String, Object> resultMap = JSON.parseObject(result, Map.class);
		log.info("返回resultValue :"+resultMap.get("resultValue").toString());
		Map<String, Object> map = JSON.parseObject(resultMap.get("resultValue").toString(), Map.class);
		JSONObject jsonObject = new JSONObject();
		String fwbs = (String) map.get("FWBS");
		jsonObject.put("id", taskId);
        List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("FHZ");
		if(TrafficConfig.busService.equals(fwbs)) {
			jsonObject.put("booking", "3");
			if(!CollectionUtils.isEmpty(list)) {
				jsonObject.put("list", list);
			}
		}else if(TrafficConfig.plainService.equals(fwbs)) {
			jsonObject.put("booking", "2");
			if(!CollectionUtils.isEmpty(list)) {
				Map<String, String> jsonMap = readJson("1");
				Map<String, String> jsonMap2 = readJson("2");
				for(Map<String, Object> temp : list) {
					if(temp.containsKey("AIR_SEG_DPT_AIRPT_CD")) {
						String airCode = (String) temp.get("AIR_SEG_DPT_AIRPT_CD");
						String airName = jsonMap.get(airCode);
						temp.put("AIR_SEG_DPT_AIRPT_CD", airCode+airName);
					}
					if(temp.containsKey("AIR_SEG_ARRV_AIRPT_CD")) {
						String airCode = (String) temp.get("AIR_SEG_ARRV_AIRPT_CD");
						String airName = jsonMap.get(airCode);
						temp.put("AIR_SEG_ARRV_AIRPT_CD", airCode+airName);
					}
					if(temp.containsKey("AIR_CARR_CD")) {
						String airCode = (String) temp.get("AIR_CARR_CD");
						String airName = jsonMap2.get(airCode);
						temp.put("AIR_CARR_CD", airCode+airName);
					}
					if(temp.containsKey("AIR_SEG_ARRV_DT_LCL")) { // 到达时间
						String airCode = ((Integer) temp.get("AIR_SEG_ARRV_DT_LCL")).toString();
						if("0".equals(airCode)) {
							temp.put("AIR_SEG_ARRV_DT_LCL", temp.get("AIR_SEG_DPT_DT_LCL"));
						}else {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
							String endTemp = (String) temp.get("AIR_SEG_DPT_DT_LCL");
							try {
								Date end = sdf.parse(endTemp);
								Calendar cal = Calendar.getInstance();
						        cal.setTime(end);
						        cal.add(Calendar.DATE,1);
						        end = cal.getTime();
						        temp.put("AIR_SEG_ARRV_DT_LCL", sdf.format(end));
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
				}
				jsonObject.put("list", list);
			}
		}else if(TrafficConfig.trainService.equals(fwbs)) {
			jsonObject.put("booking", "1");
			if(!CollectionUtils.isEmpty(list)) {
				jsonObject.put("list", list);
			}
		}
        
		try {
            log.info("kafka的topic={}, 消息={}", topic, JSON.toJSONString(jsonObject));
            kafkaTemplate.send(topic, JSON.toJSONString(jsonObject));
            log.info("发送kafka成功.");
        } catch (Exception e) {
            log.error("发送kafka出错", e);
        }
		log.info("结束时间：" + date+ ",taskId："+ taskId +"执行第："+ i +"次");
//		writeJson(taskId, date);
	}
	
	// 测试发送
	public void testSend(String result) {
		log.info("测试kafka的topic={}, 消息={}", topic, result);
		kafkaTemplate.send(topic, result);
	}
	
//	public synchronized void writeJson(Long taskId, String date) {
//		Map<String, String> map = readJson();
//		String taskIdTemp = taskId.toString();
//		if(map.containsKey(taskIdTemp)) {
//			map.remove(taskIdTemp);
//			map.put(taskIdTemp, date);
//		}else {
//			map.put(taskIdTemp, date);
//		}
//		
//		String data = JSON.toJSONString(map);
//		//写入
//		try {
////			Resource resource = new ClassPathResource("dateJson/temp.txt");
//			File file = new File("/data/store/","temp.txt");
//			if(!file.getParentFile().exists()) {
//				file.getParentFile().mkdirs();
//			}
//			FileWriter fw = new FileWriter(file.getAbsoluteFile());
//			fw.write(data);
//			fw.flush();
//			fw.close();
//
//		} catch (IOException e) {
//            e.printStackTrace();
//        }
//	}
//	
	/**
	 * type 1 机场 2航空公司
	 * @param type
	 * @return
	 */
	public Map<String,String> readJson(String type) {
		BufferedReader reader = null;
		String laststr = "";
		try {
			String url = null;
			if("1".equals(type)) {
				url = "airport";
			}else if("2".equals(type)){
				url = "airline";
			}
//			File file = new File("/data/store/",url);
//			if(!file.getParentFile().exists()) {
//				file.getParentFile().mkdirs();
//			}
//			FileInputStream is = new FileInputStream(file);	
			InputStream is = this.getClass().getResourceAsStream("/data/"+url);

			InputStreamReader inputStreamReader = new InputStreamReader(is, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				laststr += tempString;
			}
			reader.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Map<String,String> map = new HashMap<>();
		
		if(StringUtils.isNotBlank(laststr)) {
			map = JSON.parseObject(laststr, Map.class);
		}
		log.info("类型："+ type +"，读文件json:"+ JSON.toJSONString(map));
		return map;
		
	}
	
	
}