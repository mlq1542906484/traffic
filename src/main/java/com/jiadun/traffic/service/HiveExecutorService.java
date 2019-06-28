package com.jiadun.traffic.service;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class HiveExecutorService {
	
	private static Logger log = LoggerFactory.getLogger(HiveExecutorService.class);
	
    @Autowired
    @Qualifier("hiveDruidDataSource")
    private DataSource druidDataSource;

    @Autowired
    @Qualifier("hiveDruidTemplate")
    private JdbcTemplate jdbcTemplate;
	
    public String insertIntoTable() {
		String sql = "INSERT INTO TABLE test_mlq1(user_num,user_name,user_gender,user_age) VALUES(888,'Plum','M',32)";
		String result = "Insert into table successfully...";
		try {
			jdbcTemplate.execute(sql);
		} catch (Exception e) {
			result = "Insert into table encounter an error: " + e.getMessage();
			log.error(result);
		}
		return result;
	}	
}