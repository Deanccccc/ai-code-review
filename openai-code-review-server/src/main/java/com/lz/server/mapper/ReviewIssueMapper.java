package com.lz.server.mapper;

import com.lz.server.model.entity.ReviewIssue;
import com.lz.server.model.vo.IssueCategoryVO;
import com.lz.server.model.vo.ReviewIssueVO;
import com.lz.server.model.vo.StatItemVO;
import com.lz.server.model.vo.TrendVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: lz
 * @Date: 2026/5/3 09:30
 * @Description: 评审问题数据访问接口，提供问题的增删查改及分类统计、趋势统计等查询方法
 */
@Mapper
public interface ReviewIssueMapper {

    /**
     * 插入单条问题记录
     *
     * @param issue 问题实体
     * @return int 影响行数
     */
    int insert(ReviewIssue issue);

    /**
     * 批量插入问题记录
     *
     * @param issues 问题列表
     * @return int 影响行数
     */
    int insertBatch(List<ReviewIssue> issues);

    /**
     * 按问题分类统计各分类下的问题数量
     *
     * @return List<StatItemVO> 分类统计数据
     */
    List<StatItemVO> countGroupByCategory();

    /**
     * 按日期统计指定天数内的问题趋势
     *
     * @param days 统计天数
     * @return List<TrendVO> 趋势数据
     */
    List<TrendVO> countGroupByDate(int days);

    /**
     * 根据评审记录ID查询关联的问题列表
     *
     * @param reviewId 评审记录ID
     * @return List<ReviewIssueVO> 问题列表
     */
    List<ReviewIssueVO> selectByReviewId(@Param("reviewId") Long reviewId);

    /**
     * 查询所有问题分类
     *
     * @return List<IssueCategoryVO> 问题分类列表
     */
    List<IssueCategoryVO> selectAllCategories();
}
