package com.lz.server.constant;

/**
 * @Author: lz
 * @Date: 2026/5/22
 * @Description: 代码评审常量类
 */
public final class ReviewConstants {

    private ReviewConstants() {}

    public static final int CATEGORY_CODE_STYLE = 1;
    public static final int CATEGORY_LOGIC = 2;
    public static final int CATEGORY_SECURITY = 3;
    public static final int CATEGORY_PERFORMANCE = 4;
    public static final int CATEGORY_BEST_PRACTICE = 5;
    public static final int CATEGORY_OTHER = 6;

    /**
     * 问题分类规则表，按关键词匹配自动归类
     * 第一列为关键词，第二列为对应的分类ID
     */
    public static final Object[][] CATEGORY_RULES = {
            {"安全", CATEGORY_SECURITY},
            {"漏洞", CATEGORY_SECURITY},
            {"注入", CATEGORY_SECURITY},
            {"泄露", CATEGORY_SECURITY},
            {"XSS", CATEGORY_SECURITY},
            {"权限", CATEGORY_SECURITY},
            {"加密", CATEGORY_SECURITY},
            {"性能", CATEGORY_PERFORMANCE},
            {"效率", CATEGORY_PERFORMANCE},
            {"慢查询", CATEGORY_PERFORMANCE},
            {"N+1", CATEGORY_PERFORMANCE},
            {"风格", CATEGORY_CODE_STYLE},
            {"命名", CATEGORY_CODE_STYLE},
            {"格式", CATEGORY_CODE_STYLE},
            {"注释", CATEGORY_CODE_STYLE},
            {"硬编码", CATEGORY_CODE_STYLE},
            {"重复", CATEGORY_CODE_STYLE},
            {"逻辑", CATEGORY_LOGIC},
            {"Bug", CATEGORY_LOGIC},
            {"错误", CATEGORY_LOGIC},
            {"异常", CATEGORY_LOGIC},
            {"空指针", CATEGORY_LOGIC},
            {"并发", CATEGORY_LOGIC},
            {"线程", CATEGORY_LOGIC},
            {"事务", CATEGORY_LOGIC},
            {"规范", CATEGORY_BEST_PRACTICE},
            {"最佳实践", CATEGORY_BEST_PRACTICE},
            {"设计模式", CATEGORY_BEST_PRACTICE},
            {"重构", CATEGORY_BEST_PRACTICE},
            {"可读", CATEGORY_CODE_STYLE},
            {"冗余", CATEGORY_CODE_STYLE},
            {"简化", CATEGORY_CODE_STYLE},
    };
}
