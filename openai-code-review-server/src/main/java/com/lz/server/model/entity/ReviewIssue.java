package com.lz.server.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: lz
 * @Date: 2026/5/3 15:20
 * @Description: 代码评审问题实体类，对应数据库表t_review_issue
 */
@Data
public class ReviewIssue {
    private Long id;
    private Long reviewId;
    private Integer categoryId;
    private String description;
    private LocalDateTime createTime;
}
