package com.lz.server.model.vo;

import lombok.Data;

/**
 * @Author: lz
 * @Date: 2026/5/6 17:30
 * @Description: 评审问题视图对象，包含问题描述和所属分类信息
 */
@Data
public class ReviewIssueVO {
    private Long id;
    private Long reviewId;
    private Integer categoryId;
    private String description;
    private String categoryName;
}
