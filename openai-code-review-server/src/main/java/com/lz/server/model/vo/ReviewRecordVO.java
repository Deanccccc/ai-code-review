package com.lz.server.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: lz
 * @Date: 2026/4/20 15:15
 * @Description: 代码评审记录视图对象，用于列表展示
 */
@Data
public class ReviewRecordVO {
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
     * 使用的AI模型
     */
    private String aiModel;

    /**
     * 状态：1-成功 0-失败
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
