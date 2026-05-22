package com.lz.server.model.dto;

import lombok.Data;

/**
 * @Author: lz
 * @Date: 2026/5/5 08:30
 * @Description: 评审记录查询数据传输对象，封装分页参数和多条件筛选条件
 */
@Data
public class ReviewRecordQueryDTO {

    private int pageNum = 1;
    private int pageSize = 10;
    private String projectName;
    private String author;
    private String keyword;
    private Integer categoryId;

}
