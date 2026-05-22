package com.lz.server.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: lz
 * @Date: 2026/5/7 09:45
 * @Description: 评审统计视图对象，包含总评审次数、成功/失败数、按项目/作者/模型的分布等综合统计数据
 */
@Data
public class ReviewStatsVO {
    private Long totalCount;
    private Long successCount;
    private Long failCount;
    private Long totalFiles;
    private List<StatItemVO> byProject;
    private List<StatItemVO> byAuthor;
    private List<StatItemVO> byModel;
}
