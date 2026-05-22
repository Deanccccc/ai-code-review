package com.lz.server.service.impl;

import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lz.server.constant.ReviewConstants;
import com.lz.server.mapper.ReviewIssueMapper;
import com.lz.server.mapper.ReviewRecordMapper;
import com.lz.server.model.dto.ReviewRecordQueryDTO;
import com.lz.server.model.dto.ReviewReportDTO;
import com.lz.server.model.entity.ReviewIssue;
import com.lz.server.model.entity.ReviewRecord;
import com.lz.server.model.vo.*;
import com.lz.server.service.IReviewRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author: lz
 * @Date: 2026/5/2 10:00
 * @Description: 代码评审记录服务实现类，负责评审数据入库、分页查询、统计分析及问题自动分类等核心业务逻辑
 */
@Service
public class ReviewRecordServiceImpl implements IReviewRecordService {

    private static final Integer STATUS_SUCCESS = 1;
    private static final Integer STATUS_DEFAULT = 1;

    @Resource
    private ReviewRecordMapper reviewRecordMapper;

    @Resource
    private ReviewIssueMapper reviewIssueMapper;

    /**
     * 创建评审记录
     * 校验数据完整性，将评审记录和解析出的问题批量入库，使用事务保证一致性
     *
     * @param dto 评审报告数据
     * @return Long 创建的评审记录ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReviewRecord(ReviewReportDTO dto) {
        validateReviewData(dto);

        ReviewRecord record = new ReviewRecord();
        BeanUtils.copyProperties(dto, record);
        if (dto.getFileList() != null) {
            record.setFileList(JSON.toJSONString(dto.getFileList()));
        }
        if (dto.getSubmitTime() != null) {
            record.setCreateTime(LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(dto.getSubmitTime()),
                    ZoneId.systemDefault()));
        }
        record.setStatus(dto.getStatus() != null ? dto.getStatus() : STATUS_DEFAULT);
        reviewRecordMapper.insert(record);

        if (dto.getReviewResult() != null && !dto.getReviewResult().isEmpty()) {
            List<ReviewIssue> issues = parseReviewIssues(record.getId(), dto.getReviewResult());
            if (!issues.isEmpty()) {
                reviewIssueMapper.insertBatch(issues);
            }
        }

        return record.getId();
    }

    /**
     * 分页查询评审记录
     * 集成PageHelper分页，支持多条件筛选（项目名、作者、关键词、分类）
     *
     * @param queryDTO 查询条件
     * @return PageInfo<ReviewRecordVO> 分页评审记录列表
     */
    @Override
    public PageInfo<ReviewRecordVO> queryReviewPage(ReviewRecordQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<ReviewRecord> records = reviewRecordMapper.selectByQueryParams(
                queryDTO.getProjectName(),
                queryDTO.getAuthor(),
                queryDTO.getKeyword(),
                queryDTO.getCategoryId()
        );
        PageInfo<ReviewRecord> pageInfo = new PageInfo<>(records);

        List<ReviewRecordVO> voList = records.stream()
                .map(record -> {
                    ReviewRecordVO vo = new ReviewRecordVO();
                    BeanUtils.copyProperties(record, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        PageInfo<ReviewRecordVO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(voList);
        return result;
    }

    /**
     * 获取评审详情
     * 查询评审记录并关联对应的问题列表
     *
     * @param id 评审记录ID
     * @return ReviewDetailVO 评审详细信息，记录不存在时返回null
     */
    @Override
    public ReviewDetailVO getReviewDetail(Long id) {
        ReviewRecord record = reviewRecordMapper.selectById(id);
        if (record == null) {
            return null;
        }
        ReviewDetailVO detail = new ReviewDetailVO();
        detail.setId(record.getId());
        detail.setProjectName(record.getProjectName());
        detail.setBranchName(record.getBranchName());
        detail.setCommitAuthor(record.getCommitAuthor());
        detail.setCommitMessage(record.getCommitMessage());
        detail.setDiffCode(record.getDiffCode());
        detail.setFileCount(record.getFileCount());
        detail.setAdditions(record.getAdditions());
        detail.setDeletions(record.getDeletions());
        detail.setReviewResult(record.getReviewResult());
        detail.setAiModel(record.getAiModel());
        detail.setStatus(record.getStatus());
        detail.setErrorMessage(record.getErrorMessage());
        if (record.getCreateTime() != null) {
            detail.setCreateTime(record.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        List<ReviewIssueVO> issues = reviewIssueMapper.selectByReviewId(id);
        detail.setIssues(issues != null ? issues : new ArrayList<>());

        return detail;
    }

    /**
     * 获取评审综合统计数据
     * 统计总评审次数、成功/失败数量、总文件数以及按项目/作者/模型的分布
     *
     * @return ReviewStatsVO 综合统计数据
     */
    @Override
    public ReviewStatsVO getReviewStatistics() {
        ReviewStatsVO vo = new ReviewStatsVO();
        vo.setTotalCount(reviewRecordMapper.countTotal());
        vo.setSuccessCount(reviewRecordMapper.countByStatus(STATUS_SUCCESS));
        vo.setFailCount(reviewRecordMapper.countByStatus(0));
        vo.setTotalFiles(reviewRecordMapper.sumFileCount());
        vo.setByProject(reviewRecordMapper.countGroupByProject());
        vo.setByAuthor(reviewRecordMapper.countGroupByAuthor());
        vo.setByModel(reviewRecordMapper.countGroupByModel());
        return vo;
    }

    /**
     * 获取问题分类统计
     *
     * @return List<StatItemVO> 问题分类统计数据
     */
    @Override
    public List<StatItemVO> getIssueStats() {
        return reviewIssueMapper.countGroupByCategory();
    }

    /**
     * 获取趋势统计数据
     *
     * @param days 统计天数
     * @return List<TrendVO> 趋势数据
     */
    @Override
    public List<TrendVO> getTrendStats(int days) {
        return reviewIssueMapper.countGroupByDate(days);
    }

    /**
     * 获取所有问题分类
     *
     * @return List<IssueCategoryVO> 问题分类列表
     */
    @Override
    public List<IssueCategoryVO> getAllCategories() {
        return reviewIssueMapper.selectAllCategories();
    }

    /**
     * 校验评审数据完整性
     *
     * @param dto 评审报告数据
     * @throws IllegalArgumentException 当项目名、分支名或提交作者为空时抛出
     */
    private void validateReviewData(ReviewReportDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("评审数据不能为空");
        }
        if (!StringUtils.hasText(dto.getProjectName())) {
            throw new IllegalArgumentException("项目名称不能为空");
        }
        if (!StringUtils.hasText(dto.getBranchName())) {
            throw new IllegalArgumentException("分支名称不能为空");
        }
        if (!StringUtils.hasText(dto.getCommitAuthor())) {
            throw new IllegalArgumentException("提交作者不能为空");
        }
    }

    /**
     * 问题行的正则匹配模式，匹配Markdown列表项（- 、* 、数字编号等）
     */
    private static final Pattern ISSUE_LINE_PATTERN = Pattern.compile(
            "^\\s*(?:[-*]|\\d+[.、])\\s*(.+)$");

    /**
     * 解析AI评审结果中的问题列表
     * 将Markdown格式的评审结果逐行解析，提取问题描述并自动分类
     *
     * @param reviewId     评审记录ID
     * @param reviewResult AI评审结果（Markdown格式）
     * @return List<ReviewIssue> 解析出的问题列表
     */
    private List<ReviewIssue> parseReviewIssues(Long reviewId, String reviewResult) {
        List<ReviewIssue> issues = new ArrayList<>();
        if (reviewResult == null || reviewResult.isEmpty()) {
            return issues;
        }

        String[] lines = reviewResult.split("\n");
        for (String line : lines) {
            Matcher matcher = ISSUE_LINE_PATTERN.matcher(line);
            if (!matcher.find()) {
                continue;
            }
            String content = matcher.group(1).trim();
            if (content.length() < 8 || content.length() > 200) {
                continue;
            }

            int categoryId = classifyIssue(content);
            if (categoryId == 0) {
                continue;
            }

            ReviewIssue issue = new ReviewIssue();
            issue.setReviewId(reviewId);
            issue.setCategoryId(categoryId);
            issue.setDescription(content);
            issues.add(issue);
        }
        return issues;
    }

    /**
     * 根据问题描述内容自动分类
     * 遍历分类规则表，匹配关键词确定分类ID
     *
     * @param content 问题描述内容
     * @return int 问题分类ID，未匹配到时返回CATEGORY_OTHER
     */
    private int classifyIssue(String content) {
        String lower = content.toLowerCase();
        for (Object[] rule : ReviewConstants.CATEGORY_RULES) {
            if (lower.contains(((String) rule[0]).toLowerCase())) {
                return (int) rule[1];
            }
        }
        return ReviewConstants.CATEGORY_OTHER;
    }
}
