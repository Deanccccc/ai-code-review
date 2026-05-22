package com.lz.server.mapper;

import com.lz.server.model.entity.ReviewRecord;
import com.lz.server.model.vo.StatItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: lz
 * @Date: 2026/5/2 16:45
 * @Description: 评审记录数据访问接口，提供评审记录的新增、查询及多维度统计方法
 */
@Mapper
public interface ReviewRecordMapper {

    /**
     * 插入一条评审记录
     *
     * @param record 评审记录实体
     * @return int 影响行数
     */
    int insert(ReviewRecord record);

    /**
     * 根据ID查询评审记录
     *
     * @param id 评审记录ID
     * @return ReviewRecord 评审记录实体
     */
    ReviewRecord selectById(@Param("id") Long id);

    /**
     * 多条件查询评审记录列表
     *
     * @param projectName 项目名称（可选）
     * @param author      提交作者（可选）
     * @param keyword     关键词（可选）
     * @param categoryId  问题分类ID（可选）
     * @return List<ReviewRecord> 评审记录列表
     */
    List<ReviewRecord> selectByQueryParams(@Param("projectName") String projectName,
                                           @Param("author") String author,
                                           @Param("keyword") String keyword,
                                           @Param("categoryId") Integer categoryId);

    /**
     * 按项目分组统计评审次数
     *
     * @return List<StatItemVO> 项目统计数据
     */
    List<StatItemVO> countGroupByProject();

    /**
     * 按作者分组统计评审次数
     *
     * @return List<StatItemVO> 作者统计数据
     */
    List<StatItemVO> countGroupByAuthor();

    /**
     * 按AI模型分组统计评审次数
     *
     * @return List<StatItemVO> 模型统计数据
     */
    List<StatItemVO> countGroupByModel();

    /**
     * 统计评审记录总数
     *
     * @return Long 记录总数
     */
    Long countTotal();

    /**
     * 按状态统计评审记录数量
     *
     * @param status 状态值（1-成功 0-失败）
     * @return Long 符合条件的记录数
     */
    Long countByStatus(@Param("status") Integer status);

    /**
     * 统计所有评审的变更文件总数
     *
     * @return Long 文件总数
     */
    Long sumFileCount();
}
