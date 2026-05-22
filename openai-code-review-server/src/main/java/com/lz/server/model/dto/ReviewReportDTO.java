package com.lz.server.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: lz
 * @Date: 2026/4/20 15:15
 * @Description: 代码评审报告数据传输对象，用于接收GitHub Actions提交的评审结果
 */
@Data
public class ReviewReportDTO {
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
     * 变更文件列表
     */
    private List<String> fileList;

    /**
     * AI评审结果（Markdown格式）
     */
    private String reviewResult;

    /**
     * 使用的AI模型
     */
    private String aiModel;

    /**
     * 提交时间（毫秒时间戳）
     */
    private Long submitTime;

    /**
     * 状态：1-成功 0-失败
     */
    private Integer status;

    /**
     * 失败时的错误信息
     */
    private String errorMessage;
}
