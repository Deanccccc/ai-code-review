package com.lz.server.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: lz
 * @Date: 2026/5/6 10:15
 * @Description: 评审详情视图对象，包含diff内容和关联的问题列表
 */
@Data
public class ReviewDetailVO {
    private Long id;
    private String projectName;
    private String branchName;
    private String commitAuthor;
    private String commitMessage;
    private String diffCode;
    private Integer fileCount;
    private Integer additions;
    private Integer deletions;
    private String reviewResult;
    private String aiModel;
    private Integer status;
    private String errorMessage;
    private String createTime;
    private List<ReviewIssueVO> issues;
}
