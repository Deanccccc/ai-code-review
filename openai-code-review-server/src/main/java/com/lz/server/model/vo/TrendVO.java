package com.lz.server.model.vo;

import lombok.Data;

/**
 * @Author: lz
 * @Date: 2026/5/8 14:00
 * @Description: 趋势视图对象，用于展示按日期的评审问题数量趋势
 */
@Data
public class TrendVO {
    private String date;
    private Long count;
}
