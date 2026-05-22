package com.lz.server.model.vo;

import lombok.Data;

/**
 * @Author: lz
 * @Date: 2026/5/5 16:00
 * @Description: 统计项视图对象，用于各类分组统计数据的展示
 */
@Data
public class StatItemVO {
    private String name;
    private Long count;
}
