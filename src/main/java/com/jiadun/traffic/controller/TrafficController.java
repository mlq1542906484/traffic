package com.jiadun.traffic.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jiadun.traffic.config.TrafficConfig;
import com.jiadun.traffic.config.webservice.WebserviceClient;
import com.jiadun.traffic.controller.vo.ModelWarnContentVo;
import com.jiadun.traffic.service.TrafficExecutorService;


@RestController
@RequestMapping("/traffic")
public class TrafficController {
	
	private static Logger log = LoggerFactory.getLogger(TrafficController.class);
	
	@Autowired
	private WebserviceClient webserviceClient;
	
	@Autowired
	private TrafficExecutorService trafficExecutorService;
	
	@RequestMapping(value = "/getData", method = {RequestMethod.GET} )
	public List<String> getData(@RequestParam("idCard")String idCard) {
		List<String> results = new LinkedList<>();
		// 生产环境start
		/*Date now = new Date();
		trafficExecutorService.readJson("2");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String endDate = sdf.format(now);
		ModelWarnContentVo contentVo = new ModelWarnContentVo();
		contentVo.setBooking("1");
		String result1 = webserviceClient.query(idCard, contentVo, null, endDate);
		results.add(dealData(result1));
		contentVo.setBooking("2");
		String result2 = webserviceClient.query(idCard, contentVo, null, endDate);
		results.add(dealData(result2));
		contentVo.setBooking("3");
		String result3 = webserviceClient.query(idCard, contentVo, null, endDate);
		results.add(dealData(result3));*/
		// 生产环境end
		
		results.add("{\"booking\":\"1\",\"list\":[{\"ID_NO\":\"51303019970702261X\",\"ID_NAME\":\"杨中琛\",\"BZK_GXSJ\":\"2018-10-21 18:37:31\",\"FROM_STATION_NAME\":\"烟台\",\"SEAT_NO\":\"016F\",\"BZK_RKSJ\":\"2018-10-21 18:37:31\",\"TRAIN_DATE\":\"20181020\",\"TICKET_STATUS_ZW\":\"售票\",\"BOARD_TRAIN_CODE\":\"G472\",\"TICKET_STATUS\":\"Z\",\"TICKET_NO\":\"9680426\",\"DS\":\"370600\",\"DT\":\"20181020\",\"TO_STATION_NAME\":\"即墨北\",\"FILETIME\":\"2018-10-21 16:39:00\",\"ID_KIND\":\"1\",\"CZ\":\"I\",\"ID_KIND_ZW\":\"1\",\"FILENAME\":\"TIELU_370600_20181020.csv\",\"COACH_NO\":\"07\",\"KEY\":\"201810201855774869680426\",\"CZ_ZW\":\"新增\"}]}");
		results.add("{\"booking\":\"2\",\"list\":[]}");
		results.add("{\"booking\":\"3\",\"list\":[{\"PAS_LST_NM\":\"YANGZHONGCHEN\",\"D_RKSJ\":\"2018-10-21 06:09:52\",\"OPERATEDATE\":\"20181019\",\"SUB_CLS_CD\":\"Y\",\"PNR_CR_DT\":\"20181019\",\"PAS_ID\":\"1\",\"OPR_STAT_CD\":\"DL\",\"PN_SEAT\":\"1\",\"CT_DT\":\"20181021 04:09:19\",\"RSP_OFC_CD\":\"PEK223\",\"AIR_SEG_FLT_NBR\":\"4512\",\"PAS_ID_TYPE\":\"I\",\"AIR_SEG_DPT_DT_LCL\":\"20181020\",\"OFFC_CD\":\"PEK223\",\"PAS_ID_NBR\":\"51303019970702261X\",\"AIR_CARR_CD\":\"CA中国国际航空公司\",\"FILENAME\":\"FuturePNR_20181021.txt.Z\",\"GRP_IND\":\"N\",\"RSP_AIRLN_CD\":\"CA\",\"AIR_SEG_DPT_AIRPT_CD\":\"TAO青岛流亭国际机场\",\"SYSTEMID\":\"MHDZ818578554\",\"PAS_CHN_NM\":\"杨中琛\",\"OPERATETIME\":\"16:44:00\",\"AIR_SEG_ARRV_AIRPT_CD\":\"CTU成都双流国际机场\",\"AIR_SEG_DPT_TM_LCL\":\"10:50:00\",\"AIR_SEG_ARRV_DT_LCL\":\"20181020\",\"PNR_REF\":\"NEMMEF\",\"AIR_SEG_ARRV_TM_LCL\":\"14:10:00\"}]}");
		return results;
	}

	private String dealData(String result) {
		Map<String, Object> resultMap = JSON.parseObject(result, Map.class);
		log.info("返回resultValue :"+resultMap.get("resultValue").toString());
		Map<String, Object> map = JSON.parseObject(resultMap.get("resultValue").toString(), Map.class);
		JSONObject jsonObject = new JSONObject();
		String fwbs = (String) map.get("FWBS");
		List<Map<String, Object>> listTemp = new LinkedList<>();
		List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("FHZ");
		if(TrafficConfig.plainService.equals(fwbs)) {
			jsonObject.put("booking", "3");
		}else if(TrafficConfig.busService.equals(fwbs)) {
			jsonObject.put("booking", "2");
		}else if(TrafficConfig.trainService.equals(fwbs)) {
			jsonObject.put("booking", "1");
		}
        
		if (!CollectionUtils.isEmpty(list)) {
			Map<String, Object> temp = list.get(0);
			if(TrafficConfig.plainService.equals(fwbs)) {
				Map<String, String> jsonMap = trafficExecutorService.readJson("1");
				Map<String, String> jsonMap2 = trafficExecutorService.readJson("2");
				if(temp.containsKey("AIR_SEG_DPT_AIRPT_CD")) {
					String airCode = (String) temp.get("AIR_SEG_DPT_AIRPT_CD");
					String airName = jsonMap.get(airCode);
					log.info("出发机场：代码"+airCode+ "出发机场："+ airName);
					temp.put("AIR_SEG_DPT_AIRPT_CD", airCode+airName);
				}
				if(temp.containsKey("AIR_SEG_ARRV_AIRPT_CD")) {
					String airCode = (String) temp.get("AIR_SEG_ARRV_AIRPT_CD");
					String airName = jsonMap.get(airCode);
					log.info("到达机场：代码"+airCode+ "到达机场："+ airName);
					temp.put("AIR_SEG_ARRV_AIRPT_CD", airCode+airName);
				}
				if(temp.containsKey("AIR_CARR_CD")) {
					String airCode = (String) temp.get("AIR_CARR_CD");
					String airName = jsonMap2.get(airCode);
					log.info("航空公司：代码"+airCode+ "航空公司名称："+ airName);
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
			listTemp.add(temp);
		}
		jsonObject.put("list", listTemp);
		
		return JSON.toJSONString(jsonObject);
	}
	
	
	@RequestMapping(value = "/testPlain", method = {RequestMethod.GET} )
	public String testPlain(@RequestParam(value = "idCard", required = false ) String idCard) {
		Map<String, Object> params = new HashMap<>();
		params.put("FWQQZMC", "王宏");
		params.put("DYTS", "100");
		params.put("BBH", "2.0");
		params.put("DQYM", "1");
		params.put("FWQQZBS", "370611300000");
		params.put("FWBS", TrafficConfig.plainService);
		params.put("TOKEN", TrafficConfig.plainToken);
		params.put("PXZD", "D_RKSJ DESC");
		List<String> fhzds = new LinkedList<>();
		StringBuffer sb = new StringBuffer();
		sb.append("D_RKSJ > to_date('20180101 00:00:00', 'yyyymmdd hh24:mi:ss')");
		if(StringUtils.isNotBlank(idCard)) {
			sb.append("AND PAS_ID_NBR = '"+ idCard+"'");
		}
		params.put("CXTJ",sb.toString());
		
		fhzds.add("PNR_REF");
		fhzds.add("PNR_CR_DT");
		fhzds.add("AIR_CARR_CD");
		fhzds.add("AIR_SEG_FLT_NBR_SFX");
		fhzds.add("AIR_SEG_FLT_NBR");
		fhzds.add("AIR_SEG_DPT_DT_LCL");
		fhzds.add("AIR_SEG_DPT_TM_LCL");
		fhzds.add("AIR_SEG_ARRV_TM_LCL");
		fhzds.add("AIR_SEG_ARRV_DT_LCL");
		fhzds.add("AIR_SEG_DPT_AIRPT_CD");
		fhzds.add("AIR_SEG_ARRV_AIRPT_CD");
		fhzds.add("SUB_CLS_CD");
		fhzds.add("OFFC_CD");
		fhzds.add("OPR_STAT_CD");
		fhzds.add("RSP_AIRLN_CD");
		fhzds.add("RSP_OFC_CD");
		fhzds.add("PAS_ID");
		fhzds.add("PAS_LST_NM");
		fhzds.add("PAS_FST_NM");
		fhzds.add("PAS_CHN_NM");
		fhzds.add("PAS_ID_TYPE");
		fhzds.add("PAS_ID_NBR");
		fhzds.add("FFP_ID_NBR");
		fhzds.add("GRP_IND");
		fhzds.add("GRP_NM");
		fhzds.add("VIP_IND");
		fhzds.add("PN_SEAT");
		fhzds.add("OPERATEDATE");
		fhzds.add("OPERATETIME");
		fhzds.add("FILENAME");
		fhzds.add("CT_DT");
		fhzds.add("SYSTEMID");
		fhzds.add("D_RKSJ");
		params.put("FHZD", fhzds);
		String requestContent = JSON.toJSONString(params);
		log.info("机票测试参数："+ requestContent);
		String result = webserviceClient.getData(requestContent);
		log.info("机票测试返回结果"+result);
		return result;
	}
	
	@RequestMapping(value = "/testBus", method = {RequestMethod.GET} )
	public String testBus(@RequestParam(value = "idCard", required = false ) String idCard) {
		Map<String, Object> params = new HashMap<>();
		params.put("FWQQZMC", "王宏");
		params.put("DYTS", "100");
		params.put("BBH", "2.0");
		params.put("DQYM", "1");
		params.put("FWQQZBS", "370611300000");
		params.put("FWBS", TrafficConfig.busService);
		params.put("TOKEN", TrafficConfig.busToken);
		params.put("PXZD", "GPRQSJ DESC");
		List<String> fhzds = new LinkedList<>();
		StringBuffer sb = new StringBuffer();
		sb.append("GPRQSJ > to_date('20180101 00:00:00', 'yyyymmdd hh24:mi:ss')");
		if(StringUtils.isNotBlank(idCard)) {
			sb.append(" AND ZJHM = '"+ idCard+"'");
		}
		params.put("CXTJ",sb.toString());
		
		fhzds.add("SYSTEMID");
		fhzds.add("XZQH");
		fhzds.add("LKXM");
		fhzds.add("MZ");
		fhzds.add("ZJLX");
		fhzds.add("ZJHM");
		fhzds.add("GPRQSJ");
		fhzds.add("GPFS");
		fhzds.add("GPDD");
		fhzds.add("GPZ");
		fhzds.add("CC");
		fhzds.add("XL");
		fhzds.add("SCKCCPHM");
		fhzds.add("SCRYXM");
		fhzds.add("SCRYLXFS");
		fhzds.add("CFZ");
		fhzds.add("FCRQSJ");
		fhzds.add("PMSJ");
		fhzds.add("SFLSXFC");
		fhzds.add("DDZ");
		fhzds.add("TPJL");
		fhzds.add("CJSJ");
		fhzds.add("XLH");
		fhzds.add("CQBS");
		fhzds.add("GXSJ");
		fhzds.add("ZJK_RKSJ");
		fhzds.add("ZJK_XGSJ");
		params.put("FHZD", fhzds);
		String requestContent = JSON.toJSONString(params);
		log.info("汽车测试参数："+ requestContent);
		String result = webserviceClient.getData(requestContent);
		log.info("汽车测试返回结果"+result);
		return result;
	}
	
	@RequestMapping(value = "/testTrain", method = {RequestMethod.GET} )
	public String testTrain(@RequestParam(value = "idCard", required = false ) String idCard) {
		Map<String, Object> params = new HashMap<>();
		params.put("FWQQZMC", "王宏");
		params.put("DYTS", "100");
		params.put("BBH", "2.0");
		params.put("DQYM", "1");
		params.put("FWQQZBS", "370611300000");
		params.put("FWBS", TrafficConfig.trainService);
		params.put("TOKEN", TrafficConfig.trainToken);
		params.put("PXZD", "BZK_RKSJ DESC");
		List<String> fhzds = new LinkedList<>();
		StringBuffer sb = new StringBuffer();
		sb.append("BZK_RKSJ > to_date('20180101 00:00:00', 'yyyymmdd hh24:mi:ss')");
		if(StringUtils.isNotBlank(idCard)) {
			sb.append(" AND ID_NO = '"+ idCard+"'");
		}
		params.put("CXTJ",sb.toString());
		fhzds.add("KEY");
		fhzds.add("ID_KIND");
		fhzds.add("ID_KIND_ZW");
		fhzds.add("ID_NO");
		fhzds.add("ID_NAME");
		fhzds.add("TRAIN_DATE");
		fhzds.add("BOARD_TRAIN_CODE");
		fhzds.add("FROM_STATION_NAME");
		fhzds.add("TO_STATION_NAME");
		fhzds.add("TICKET_NO");
		fhzds.add("COACH_NO");
		fhzds.add("SEAT_NO");
		fhzds.add("TICKET_STATUS");
		fhzds.add("TICKET_STATUS_ZW");
		fhzds.add("CZ");
		fhzds.add("CZ_ZW");
		fhzds.add("FILENAME");
		fhzds.add("FILETIME");
		fhzds.add("DS");
		fhzds.add("DT");
		fhzds.add("BZK_RKSJ");
		fhzds.add("BZK_GXSJ");
		fhzds.add("BZK_SCBZ");
		fhzds.add("BZK_ZZJG");
		params.put("FHZD", fhzds);
		String requestContent = JSON.toJSONString(params);
		log.info("火车测试参数："+ requestContent);
		String result = webserviceClient.getData(requestContent);
		log.info("火车测试返回结果"+result);
		return result;
	}
	
	@RequestMapping(value = "/testRead", method = {RequestMethod.GET} )
	public void testRead() {
		trafficExecutorService.readJson("2");
	}
	
}
