CREATE DATABASE IF NOT EXISTS openai_code_review DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE openai_code_review;

CREATE TABLE IF NOT EXISTS t_review_record (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY         COMMENT '主键ID',

    project_name     VARCHAR(128)  NOT NULL                   COMMENT '项目名称',
    branch_name      VARCHAR(128)  NOT NULL                   COMMENT '分支名称',
    commit_author    VARCHAR(64)   NOT NULL                   COMMENT '提交者',
    commit_message   VARCHAR(512)                             COMMENT '提交信息',

    diff_code        LONGTEXT                                  COMMENT 'git diff原始内容(unified format)',
    file_count       INT DEFAULT 0                         COMMENT '变更文件数量',
    additions        INT DEFAULT 0                         COMMENT '增加行数',
    deletions        INT DEFAULT 0                         COMMENT '删除行数',
    file_list        JSON                                     COMMENT '变更文件列表(JSON数组)',
    review_result    MEDIUMTEXT                                COMMENT 'AI评审结果(Markdown格式)',

    ai_model         VARCHAR(64)                               COMMENT '使用的AI模型',

    status           TINYINT       DEFAULT 1                  COMMENT '1-成功 0-失败',
    error_message    TEXT                                     COMMENT '失败时的错误信息',

    create_time      DATETIME      DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',

    INDEX idx_project  (project_name),
    INDEX idx_author   (commit_author),
    INDEX idx_time     (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI代码评审记录表';

CREATE TABLE IF NOT EXISTS t_issue_category (
    id          INT AUTO_INCREMENT PRIMARY KEY  COMMENT '主键ID',
    name        VARCHAR(32)  NOT NULL UNIQUE    COMMENT '分类名称',
    sort_order  INT          DEFAULT 0          COMMENT '排序序号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评审问题分类字典表';

INSERT INTO t_issue_category (id, name, sort_order) VALUES
(1, '代码风格', 1),
(2, '逻辑缺陷', 2),
(3, '安全隐患', 3),
(4, '性能优化', 4),
(5, '最佳实践', 5),
(6, '其他', 99);

CREATE TABLE IF NOT EXISTS t_review_issue (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY         COMMENT '主键ID',
    review_id        BIGINT        NOT NULL                    COMMENT '关联的评审记录ID',
    category_id      INT           NOT NULL                    COMMENT '分类ID，关联t_issue_category',
    description      VARCHAR(512)  NOT NULL                    COMMENT '问题描述',
    create_time      DATETIME      DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',

    INDEX idx_review_id    (review_id),
    INDEX idx_category_id  (category_id),
    INDEX idx_create_time  (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评审问题分类表';

-- 模拟评审数据
INSERT INTO t_review_record (project_name, branch_name, commit_author, commit_message, diff_code, file_count, additions, deletions, file_list, review_result, ai_model, status, error_message, create_time) VALUES
('openai-code-review-server', 'main', 'zhangsan', 'feat: 新增代码评审保存接口',
'diff --git a/src/main/java/com/lz/server/controller/ReviewRecordController.java b/src/main/java/com/lz/server/controller/ReviewRecordController.java\n--- a/src/main/java/com/lz/server/controller/ReviewRecordController.java\n+++ b/src/main/java/com/lz/server/controller/ReviewRecordController.java\n@@ -10,6 +10,15 @@ public class ReviewRecordController {\n+    @PostMapping(\"/save\")\n+    public Result<String> save(@RequestBody ReviewReportDTO dto,\n+                               @RequestHeader(value = \"Authorization\", required = false) String auth) {\n+        if (auth == null || !auth.equals(\"Bearer \" + internalApiToken)) {\n+            return Result.error(403, \"token invalid\");\n+        }\n+        reviewRecordService.save(dto);\n+        return Result.success(\"ok\");\n+    }',
1, 8, 0, '["src/main/java/com/lz/server/controller/ReviewRecordController.java"]',
'## AI 代码评审报告\n\n### 总体评价：✅ 良好\n\n**评分：8/10**\n\n### 优点\n- 接口设计简洁，RESTful 风格规范\n- 使用 `@RequestHeader` 进行 Token 校验，安全性较好\n- 返回统一的 `Result` 对象，响应格式一致\n\n### 建议改进\n1. Token 校验逻辑建议抽取为拦截器或 AOP，避免每个接口重复校验\n2. 建议对 `dto` 参数添加 `@Valid` 进行参数校验\n3. 硬编码的 \"Bearer \" 前缀建议定义为常量',
'gpt-4o', 1, NULL, '2026-05-06 10:30:00'),

('openai-code-review-server', 'feature/user-auth', 'lisi', 'feat: 添加用户认证模块',
'diff --git a/src/main/java/com/lz/server/service/AuthService.java b/src/main/java/com/lz/server/service/AuthService.java\nnew file mode 100644\n--- /dev/null\n+++ b/src/main/java/com/lz/server/service/AuthService.java\n@@ -0,0 +1,12 @@\n+public class AuthService {\n+    public String login(String username, String password) {\n+        User user = userMapper.findByUsername(username);\n+        if (user == null) {\n+            throw new BusinessException(\"用户不存在\");\n+        }\n+        if (!passwordEncoder.matches(password, user.getPassword())) {\n+            throw new BusinessException(\"密码错误\");\n+        }\n+        return jwtUtil.generateToken(user.getId());\n+    }\n+}',
1, 12, 0, '["src/main/java/com/lz/server/service/AuthService.java"]',
'## AI 代码评审报告\n\n### 总体评价：⚠️ 需要注意\n\n**评分：6/10**\n\n### 问题\n1. **安全风险**：登录失败时应返回统一的错误信息，区分 "用户不存在" 和 "密码错误" 可能被用于枚举攻击\n2. 建议添加登录失败次数限制，防止暴力破解\n3. JWT Token 未设置过期时间和刷新机制\n\n### 优点\n- 使用 BCrypt 加密密码，符合安全最佳实践\n- Service 层职责划分清晰',
'gpt-4o', 1, NULL, '2026-05-06 14:20:00'),

('openai-code-review-frontend', 'dev', 'wangwu', 'fix: 修复列表分页Bug',
'diff --git a/src/api/review.js b/src/api/review.js\n--- a/src/api/review.js\n+++ b/src/api/review.js\n@@ -25,7 +25,7 @@ const fetchList = async (params) => {\n-    const res = await axios.get(\"/api/review/list\", { params: { ...params, page: 1 } });\n+    const res = await axios.get(\"/api/review/list\", { params });',
1, 1, 1, '["src/api/review.js"]',
'## AI 代码评审报告\n\n### 总体评价：✅ 良好\n\n**评分：7/10**\n\n### 评审意见\n- 修复了分页参数被硬编码为 `page: 1` 的问题，现在正确传递分页参数\n- 改动用到了展开运算符，代码简洁\n\n### 建议\n- 建议前端添加分页参数的默认值处理，防止未传参时的异常',
'gpt-4o-mini', 1, NULL, '2026-05-06 16:00:00'),

('openai-code-review-frontend', 'dev', 'zhangsan', 'refactor: 重构Chart组件使用ECharts',
'diff --git a/src/components/ReviewChart.jsx b/src/components/ReviewChart.jsx\n--- a/src/components/ReviewChart.jsx\n+++ b/src/components/ReviewChart.jsx\n@@ -1,15 +1,14 @@\n-import { Line } from \"react-chartjs-2\";\n+import ReactECharts from \"echarts-for-react\";\n \n-const data = {\n-  labels: labels,\n-  datasets: [{\n-    label: \"评审数量\",\n-    data: values,\n-  }]\n-};\n-\n-<Line data={data} />\n+\n+const option = {\n+  xAxis: { type: \"category\", data: labels },\n+  yAxis: { type: \"value\" },\n+  series: [{ data: values, type: \"line\" }]\n+};\n+\n+<ReactECharts option={option} />',
1, 30, 15, '["src/components/ReviewChart.jsx"]',
'## AI 代码评审报告\n\n### 总体评价：✅ 良好\n\n**评分：8/10**\n\n### 评审意见\n- 从 chartjs 迁移到 ECharts，图表配置更加灵活\n- 删除了 30 行配置代码，改用 ECharts option 对象，代码更简洁\n- ECharts 在国内生态更好，文档更全\n\n### 建议\n- 注意检查 `echarts-for-react` 包的体积，如果过大可以考虑按需引入',
'claude-3.5-sonnet', 1, NULL, '2026-05-07 09:15:00'),

('openai-code-review-server', 'feature/ci-cd', 'lisi', 'chore: 添加GitHub Actions自动评审流水线',
'diff --git a/.github/workflows/code-review.yml b/.github/workflows/code-review.yml\nnew file mode 100644\n--- /dev/null\n+++ b/.github/workflows/code-review.yml\n@@ -0,0 +1,12 @@\n+name: AI Code Review\n+on:\n+  pull_request:\n+    branches: [main]\n+jobs:\n+  review:\n+    runs-on: ubuntu-latest\n+    steps:\n+      - uses: actions/checkout@v4\n+      - name: Get diff\n+        run: git diff origin/main...HEAD > diff.txt\n+      - name: Send to Review Server\n+        uses: fjogeleit/http-request-action@v1',
1, 12, 0, '[".github/workflows/code-review.yml"]',
'## AI 代码评审报告\n\n### 总体评价：⚠️ 需要注意\n\n**评分：5/10**\n\n### 问题\n1. **安全风险**：`curl` 命令中的 URL 硬编码了生产地址，建议使用 GitHub Secrets 管理\n2. Token 通过命令行传递，可能在进程列表中泄露\n3. 未设置 `timeout-minutes`，可能导致流水线长时间挂起\n4. diff.txt 文件只在当前 step 有效，跨 step 需要 artifact 传递\n\n### 建议\n- 将 API URL 和 Token 都放入 GitHub Secrets\n- 使用 `actions/upload-artifact` 传递 diff 文件',
'gpt-4o', 0, 'JSON payload 格式错误：缺少 projectName 字段', '2026-05-07 09:45:00'),

('data-analytics-platform', 'main', 'zhaoliu', 'perf: 优化SQL查询性能，添加索引',
'diff --git a/sql/init.sql b/sql/init.sql\n--- a/sql/init.sql\n+++ b/sql/init.sql\n@@ -15,6 +15,7 @@ CREATE TABLE t_order (\n+    INDEX idx_user_id (user_id),\n+    INDEX idx_create_time (create_time),\n@@ -45,7 +46,8 @@ SELECT o.*, u.name\n FROM t_order o\n LEFT JOIN t_user u ON o.user_id = u.id\n-WHERE o.create_time >= \'2026-01-01\'\n+WHERE o.user_id = 10001\n+  AND o.create_time >= \'2026-01-01\'\n ORDER BY o.create_time DESC',
1, 2, 1, '["sql/init.sql"]',
'## AI 代码评审报告\n\n### 总体评价：✅ 优秀\n\n**评分：9/10**\n\n### 评审意见\n- 添加了 `user_id` 和 `create_time` 索引，精准命中查询条件，查询效率大幅提升\n- 查询条件增加 `user_id` 过滤，利用索引缩小扫描范围\n\n### 建议\n- 如果该 SQL 是列表查询，建议加上 `LIMIT` 限制返回行数\n- 建议用 `EXPLAIN` 验证索引命中情况',
'claude-3.5-sonnet', 1, NULL, '2026-05-07 10:00:00');
