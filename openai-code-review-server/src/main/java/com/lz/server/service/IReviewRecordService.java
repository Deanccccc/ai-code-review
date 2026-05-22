package com.lz.server.service;

import com.github.pagehelper.PageInfo;
import com.lz.server.model.dto.ReviewReportDTO;
import com.lz.server.model.dto.ReviewRecordQueryDTO;
import com.lz.server.model.vo.IssueCategoryVO;
import com.lz.server.model.vo.ReviewDetailVO;
import com.lz.server.model.vo.ReviewRecordVO;
import com.lz.server.model.vo.ReviewStatsVO;
import com.lz.server.model.vo.StatItemVO;
import com.lz.server.model.vo.TrendVO;

import java.util.List;

/**
 * @Author: lz
 * @Date: 2026/5/1 14:30
 * @Description: 代码评审记录服务接口，定义评审数据的增删查改与统计分析业务方法
 */
public interface IReviewRecordService {

    /**
     * 创建评审记录
     * 将AI评审报告持久化到数据库，并解析其中发现的问题
     *
     * @param dto 评审报告数据
     * @return Long 创建的评审记录ID
     */
    Long createReviewRecord(ReviewReportDTO dto);

    /**
     * 分页查询评审记录
     *
     * @param queryDTO 查询条件（项目名、作者、关键词、分类ID）
     * @return PageInfo<ReviewRecordVO> 分页评审记录列表
     */
    PageInfo<ReviewRecordVO> queryReviewPage(ReviewRecordQueryDTO queryDTO);

    /**
     * 获取评审详情
     * 包含diff内容和关联的问题列表
     *
     * @param id 评审记录ID
     * @return ReviewDetailVO 评审详细信息
     */
    ReviewDetailVO getReviewDetail(Long id);

    /**
     * 获取评审综合统计数据
     * 包含总评审次数、成功/失败次数、按项目/作者/模型的分布统计
     *
     * @return ReviewStatsVO 综合统计数据
     */
    ReviewStatsVO getReviewStatistics();

    /**
     * 获取问题分类统计
     * 按问题类别统计各分类下的问题数量
     *
     * @return List<StatItemVO> 问题分类统计数据
     */
    List<StatItemVO> getIssueStats();

    /**
     * 获取趋势统计数据
     * 按日期统计指定天数内的评审问题趋势
     *
     * @param days 统计天数
     * @return List<TrendVO> 趋势数据
     */
    List<TrendVO> getTrendStats(int days);

    /**
     * 获取所有问题分类
     *
     * @return List<IssueCategoryVO> 问题分类列表
     */
    List<IssueCategoryVO> getAllCategories();
}
