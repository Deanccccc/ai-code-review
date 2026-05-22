package com.lz.server.controller;

import com.github.pagehelper.PageInfo;
import com.lz.server.model.Result;
import com.lz.server.model.dto.ReviewReportDTO;
import com.lz.server.model.dto.ReviewRecordQueryDTO;
import com.lz.server.model.vo.IssueCategoryVO;
import com.lz.server.model.vo.ReviewDetailVO;
import com.lz.server.model.vo.ReviewRecordVO;
import com.lz.server.model.vo.ReviewStatsVO;
import com.lz.server.model.vo.StatItemVO;
import com.lz.server.model.vo.TrendVO;
import com.lz.server.service.IReviewRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: lz
 * @Date: 2026/5/1 09:15
 * @Description: 代码评审记录控制器，提供评审提交、查询、统计等REST API接口
 */
@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/review")
public class ReviewRecordController {

    @Resource
    private IReviewRecordService reviewRecordService;

    /**
     * 提交代码评审报告
     * 接收GitHub Actions等CI工具提交的AI评审结果
     *
     * @param dto 评审报告数据
     * @return Result<Long> 创建的评审记录ID
     */
    @PostMapping("/submit")
    public Result<Long> submitReview(@RequestBody ReviewReportDTO dto) {
        System.out.println("接收到SDK的数据: " + dto);
        if (dto == null) {
            return Result.error(400, "请求参数不能为空");
        }
        Long id = reviewRecordService.createReviewRecord(dto);
        return Result.success(id);
    }

    /**
     * 分页查询评审记录
     *
     * @param queryDTO 查询条件（项目名、作者、关键词、分类等）
     * @return Result<PageInfo<ReviewRecordVO>> 分页评审记录列表
     */
    @GetMapping("/page")
    public Result<PageInfo<ReviewRecordVO>> getReviewPage(@ModelAttribute ReviewRecordQueryDTO queryDTO) {
        PageInfo<ReviewRecordVO> pageInfo = reviewRecordService.queryReviewPage(queryDTO);
        log.info("查询评审数据成功");
        return Result.success(pageInfo);
    }

    /**
     * 查看评审详情
     *
     * @param id 评审记录ID
     * @return Result<ReviewDetailVO> 评审详细信息，包含diff内容和问题列表
     */
    @GetMapping("/{id}")
    public Result<ReviewDetailVO> getReviewDetail(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error(400, "无效的记录ID");
        }
        ReviewDetailVO detail = reviewRecordService.getReviewDetail(id);
        if (detail == null) {
            return Result.error(404, "记录不存在");
        }
        return Result.success(detail);
    }

    /**
     * 获取评审综合统计数据
     * 包含总评审次数、成功/失败次数、按项目/作者/模型的分布统计
     *
     * @return Result<ReviewStatsVO> 综合统计数据
     */
    @GetMapping("/statistics")
    public Result<ReviewStatsVO> getReviewStatistics() {
        return Result.success(reviewRecordService.getReviewStatistics());
    }

    /**
     * 获取问题分类统计
     * 按问题类别统计各分类下的问题数量
     *
     * @return Result<List<StatItemVO>> 问题分类统计数据
     */
    @GetMapping("/statistics/issues")
    public Result<List<StatItemVO>> getIssueStatistics() {
        return Result.success(reviewRecordService.getIssueStats());
    }

    /**
     * 获取趋势统计数据
     * 按日期统计指定天数内的评审问题趋势
     *
     * @param days 统计天数，默认30天
     * @return Result<List<TrendVO>> 趋势数据
     */
    @GetMapping("/statistics/trend")
    public Result<List<TrendVO>> getTrendStatistics(@RequestParam(defaultValue = "30") int days) {
        return Result.success(reviewRecordService.getTrendStats(days));
    }

    /**
     * 获取所有问题分类
     *
     * @return Result<List<IssueCategoryVO>> 问题分类列表
     */
    @GetMapping("/categories")
    public Result<List<IssueCategoryVO>> getCategories() {
        return Result.success(reviewRecordService.getAllCategories());
    }
}
