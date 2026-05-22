package com.lz.server.model.vo;

import lombok.Data;

/**
 * @Author: lz
 * @Date: 2026/5/9 11:20
 * @Description: 问题分类视图对象，用于展示问题分类信息
 */
@Data
public class IssueCategoryVO {
    private Long id;
    private String name;
    private Integer sortOrder;
}
