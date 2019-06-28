package com.jiadun.traffic.controller.vo;

import lombok.Data;

import java.util.List;

/**
 *
 * @author TangLin
 * @date 2018/10/25
 */
@Data
public class ModelWarnContentVo {

    private String booking;

    private List<ModelWarnContentDestinationVo> destination;

    private List<String> AllSelct;

    private List<String> ExSelct;

    private String alarmFrequency;

    private String alarmNum;

}
