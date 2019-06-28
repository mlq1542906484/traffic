package com.jiadun.traffic.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@RestController
public class IndexController {

    public static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    @Qualifier("hiveDruidDataSource")
    private DataSource druidDataSource;

    @Autowired
    @Qualifier("hiveDruidTemplate")
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/table/show")
    public List<String> showtables() {
        List<String> list = new ArrayList<String>();
        Statement statement = null;
        try {
//        	jdbcTemplate.
            statement = druidDataSource.getConnection().createStatement();
            String sql = "show tables";
            logger.info("Running: " + sql);
            ResultSet res = statement.executeQuery(sql);
            while (res.next()) {
                list.add(res.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @RequestMapping("/table/create")
	public String createTable() {
		StringBuffer sql = new StringBuffer("CREATE TABLE IF NOT EXISTS ");
		sql.append("test_mlq1");
		sql.append("(user_num BIGINT, user_name STRING, user_gender STRING, user_age INT)");
		sql.append("ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n' "); // 定义分隔符
//		sql.append("STORED AS TEXTFILE"); // 作为文本存储
 
		logger.info("Running: " + sql);
		String result = "Create table successfully...";
		try {
			// hiveJdbcTemplate.execute(sql.toString());
			jdbcTemplate.execute(sql.toString());
		} catch (Exception e) {
			logger.error(result);
		}
		return result;
	}
    
    
    @RequestMapping("/table/insert")
	public String insertIntoTable() {
		String sql = "INSERT INTO TABLE test_mlq1(user_num,user_name,user_gender,user_age) VALUES(888,'Plum','M',32)";
		String result = "Insert into table successfully...";
		try {
			// hiveJdbcTemplate.execute(sql);
			jdbcTemplate.execute(sql);
		} catch (Exception dae) {
			result = "Insert into table encounter an error: " + dae.getMessage();
			logger.error(result);
		}
		return result;
	}

}
