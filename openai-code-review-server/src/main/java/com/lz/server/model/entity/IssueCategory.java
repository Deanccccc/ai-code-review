package com.lz.server.model.entity;

import lombok.Data;

/**
 * @Author: lz
 * @Date: 2026/5/4 11:00
 * @Description: 问题分类实体类，对应数据库表t_issue_category
 */
@Data
public class IssueCategory {
    private Integer id;
    private String name;
    private Integer sortOrder;
}
