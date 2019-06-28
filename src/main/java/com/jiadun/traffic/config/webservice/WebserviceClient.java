package com.jiadun.traffic.config.webservice;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.jiadun.traffic.config.TrafficConfig;
import com.jiadun.traffic.controller.vo.ModelWarnContentVo;

@Component
public class WebserviceClient {
	
	private static Logger log = LoggerFactory.getLogger(WebserviceClient.class);
	private static String endpoint = "http://10.48.147.135:7005/basic/servicebus/webservice/serviceUnifiedPortal?wsdl";
	
	public String query(String feature, ModelWarnContentVo contentVo, String startDate, String endDate) {
		Map<String, Object> params = new HashMap<>();
		params.put("FWQQZMC", "王宏");
		params.put("DYTS", "100");
		params.put("BBH", "2.0");
		params.put("DQYM", "1");
		params.put("FWQQZBS", "370611300000");
		List<String> fhzds = new LinkedList<>();
		if ("1".equals(contentVo.getBooking())) { // 火车票
			params.put("FWBS", TrafficConfig.trainService);
			params.put("TOKEN", TrafficConfig.trainToken);
			params.put("PXZD", "BZK_RKSJ DESC");
			
			// 拼查询条件
			StringBuffer temp = new StringBuffer();
			temp.append("ID_NO ='" + feature + "'");
			if (StringUtils.isNotBlank(startDate)) {
				temp.append(" AND BZK_RKSJ>=to_date('" + startDate + "','yyyymmdd hh24:mi:ss')");
			}
			params.put("CXTJ", temp.toString());
			log.info("火车票查询参数{}", temp.toString());
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

		} else if ("2".equals(contentVo.getBooking())) { // 长途汽车
			params.put("FWBS", TrafficConfig.busService);
			params.put("TOKEN", TrafficConfig.busToken);
			params.put("PXZD", "GPRQSJ DESC");
			
			// 拼查询条件
			StringBuffer temp = new StringBuffer();
			temp.append("ZJHM ='" + feature + "'");
			if (StringUtils.isNotBlank(startDate)) {
				temp.append(" AND GPRQSJ >= to_date('" + startDate + "','yyyymmdd hh24:mi:ss' )");
			}
			params.put("CXTJ", temp.toString());
			log.info("车票查询参数{}", temp.toString());
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
		} else if ("3".equals(contentVo.getBooking())) { // 机票
			params.put("FWBS", TrafficConfig.plainService);
			params.put("TOKEN", TrafficConfig.plainToken);
			params.put("PXZD", "D_RKSJ DESC");

			// 拼查询条件
			StringBuffer temp = new StringBuffer();
			temp.append("PAS_ID_NBR ='" + feature + "'");
			if(StringUtils.isNotBlank(startDate)) {
				temp.append(" AND D_RKSJ>=to_date('" + startDate + "','yyyymmdd hh24:mi:ss')");
			}
			log.info("民航查询参数{}", temp);
			params.put("CXTJ", temp.toString());
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
		}
		params.put("FHZD", fhzds);
		String requestContent = JSON.toJSONString(params);
		String result = getData(requestContent);
		
		return result;
	}
	
	public String getData(String requestContent) {
		log.info("传入参数{}", requestContent);
		String result = "call failed!";
		Service service = new Service();
		Call call;
		try {
			call = (Call) service.createCall();
			call.setTargetEndpointAddress(endpoint);
			call.setOperationName(new QName("http://ws.fwdy.fwdt.jzpt.sinobest.cn/","queryService"));
			call.addParameter("param", 
					XMLType.XSD_STRING,
					ParameterMode.IN);
			// 设置返回值类型
			call.setReturnType(XMLType.XSD_STRING);
			result = (String) call.invoke(new Object[] { requestContent.toString()});
		}catch (Exception e) {
			e.printStackTrace();
			log.error("接口报错，传入参数{}", requestContent);
		}
		return result;
	}
	
}
