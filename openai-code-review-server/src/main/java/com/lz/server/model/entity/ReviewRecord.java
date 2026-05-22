package com.lz.server.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: lz
 * @Date: 2026/4/20 15:15
 * @Description: 代码评审记录实体类，对应数据库表t_review_record
 */
@Data
public class ReviewRecord {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 分支名称
     */
    private String branchName;

    /**
     * 提交作者
     */
    private String commitAuthor;

    /**
     * 提交信息
     */
    private String commitMessage;

    /**
     * Git diff原始内容（unified format）
     */
    private String diffCode;

    /**
     * 变更文件数量
     */
    private Integer fileCount;

    /**
     * 增加行数
     */
    private Integer additions;

    /**
     * 删除行数
     */
    private Integer deletions;

    /**
     * 变更文件列表（JSON格式）
     */
    private String fileList;

    /**
     * AI评审结果（Markdown格式）
     */
    private String reviewResult;

    /**
     * 使用的AI模型
     */
    private String aiModel;

    /**
     * 状态：1-成功 0-失败
     */
    private Integer status;

    /**
     * 失败时的错误信息
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
