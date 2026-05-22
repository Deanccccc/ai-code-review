# AI 智能代码评审平台 — 用户使用指南

## 一、系统简介

AI Code Review 是一个基于大模型的智能代码评审工具。每次你向 GitHub 仓库推送代码，系统会自动获取本次变更（Git Diff），调用 AI 进行代码评审，并将结果保存到你自己的数据库中。评审完成后，你会通过**微信模板消息**和**飞书卡片消息**收到通知，点击通知中的链接即可在可视化看板中查看详细的评审报告和代码 Diff 对比。

**所有数据都存储在你自己的服务器上，不会经过第三方。**

---

## 二、使用方式一览

```
你的 GitHub 仓库                       你的服务器 / 本机
┌───────────────────┐                 ┌──────────────────────┐
│                   │    push 触发    │                      │
│   GitHub Actions  │ ──────────────→ │  SDK (JAR)           │
│   CI 流水线       │                 │  ├─ git diff          │
│                   │                 │  ├─ AI 评审           │
└───────────────────┘                 │  ├─ 入库              │
                                      │  ├─ 微信通知          │
                                      │  └─ 飞书通知          │
                                      │                      │
                                      │  你的 Spring Boot 后端│
                                      │  你的 MySQL 数据库    │
                                      │  你的 Vue3 前端看板   │
                                      └──────────────────────┘
```

---

## 三、环境要求

| 组件 | 要求 |
|------|------|
| JDK | 17 及以上 |
| MySQL | 8.0 及以上 |
| Maven | 3.6 及以上 |
| Node.js | 16 及以上 |

---

## 四、第一步：部署后端

克隆代码 → 执行建表脚本 → 改数据库连接 → 启动：

```bash
git clone <你的后端仓库地址>
cd openai-code-review-server

# 初始化数据库
mysql -u root -p < sql/init.sql

# 修改 src/main/resources/application.yml 中的数据库连接（三行）
#   username: root       → 改成你的 MySQL 用户名
#   password: 123456     → 改成你的 MySQL 密码
#   internal.api-token   → 改成你自己的密钥（后面要和 Secrets 一致）

# 编译并启动
mvn clean package -DskipTests
java -jar target/openai-code-review-server-1.0.jar
# 看到 "Tomcat started on port(s): 8080" 即成功
```

---

## 五、第二步：部署前端

克隆代码 → 改代理地址 → 安装启动：

```bash
git clone <你的前端仓库地址>
cd openai-code-review-front

# 编辑 vue.config.js，target 指向后端：
#   proxy: { '/api': { target: 'http://localhost:8080' } }

npm install
npm run serve
# 浏览器打开 http://localhost:3000
```

| 页面 | 路由 | 功能 |
|------|------|------|
| 评审列表 | `/` | 搜索、筛选、分页查看评审记录 |
| 评审详情 | `/detail/:id` | Markdown 评审结果 + 代码 Diff 对比 |
| 统计看板 | `/stats` | 评审总数、趋势、问题分布 |

---

## 六、第三步：接入 GitHub Actions

在你的仓库中创建 `.github/workflows/code-review.yml`, 注意sdk模块的Jar包放在本仓库release下：

```yaml
name: AI Code Review

on:
  push:
    branches:
      - '**'

jobs:
  review:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout repository
        uses: actions/checkout@v4
        with:
          fetch-depth: 2

      - name: Setup JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'adopt'
          java-version: '17'

      - name: Download SDK JAR
        run: |
          mkdir -p ./libs
          curl -L -o ./libs/openai-code-review-sdk-1.0.jar \
            https://github.com/<你的账号>/<SDK发布仓库>/releases/download/v1.0/openai-code-review-sdk-1.0.jar

      - name: Get Commit Info
        run: |
          echo "COMMIT_AUTHOR=$(git log -1 --pretty=format:'%an')" >> $GITHUB_ENV
          echo "COMMIT_PROJECT=${GITHUB_REPOSITORY##*/}" >> $GITHUB_ENV
          echo "COMMIT_BRANCH=${GITHUB_REF#refs/heads/}" >> $GITHUB_ENV
          echo "COMMIT_MESSAGE=$(git log -1 --pretty=format:'%s')" >> $GITHUB_ENV

      - name: Run Code Review
        run: java -jar ./libs/openai-code-review-sdk-1.0.jar
        env:
          COMMIT_AUTHOR: ${{ env.COMMIT_AUTHOR }}
          COMMIT_PROJECT: ${{ env.COMMIT_PROJECT }}
          COMMIT_BRANCH: ${{ env.COMMIT_BRANCH }}
          COMMIT_MESSAGE: ${{ env.COMMIT_MESSAGE }}

          LLM_API_HOST: ${{ secrets.LLM_API_HOST }}
          LLM_API_SECRET_KEY: ${{ secrets.LLM_API_SECRET_KEY }}
          LLM_MODEL_NAME: ${{ secrets.LLM_MODEL_NAME }}

          BACKEND_URL: ${{ secrets.BACKEND_URL }}
          BACKEND_API_TOKEN: ${{ secrets.BACKEND_API_TOKEN }}
          FRONTEND_URL: ${{ secrets.FRONTEND_URL }}

          WEIXIN_APPID: ${{ secrets.WEIXIN_APPID }}
          WEIXIN_APP_SECRET: ${{ secrets.WEIXIN_APP_SECRET }}
          WEIXIN_TOUSER: ${{ secrets.WEIXIN_TOUSER }}
          WEIXIN_TEMPLATE_ID: ${{ secrets.WEIXIN_TEMPLATE_ID }}

          FEISHU_WEBHOOK: ${{ secrets.FEISHU_WEBHOOK }}
```

然后在仓库 **Settings → Secrets and variables → Actions** 中配置以下密钥：

| Secret 名称 | 填写内容 | 说明 |
|------------|---------|------|
| `LLM_API_HOST` | `https://api.deepseek.com/chat/completions` | AI 模型接口地址 |
| `LLM_API_SECRET_KEY` | `Bearer sk-xxxxxxxxxxxxxxxx` | AI 模型 API Key |
| `LLM_MODEL_NAME` | `deepseek-chat` | 使用的模型名称 |
| `BACKEND_URL` | `http://你的服务器IP:8080` | 你自己的后端地址 |
| `BACKEND_API_TOKEN` | 和 `application.yml` 中 `internal.api-token` 一致 | 接口认证 Token |
| `FRONTEND_URL` | `http://你的服务器IP:3000` | 你自己的前端地址 |
| `WEIXIN_APPID` | 微信公众号 AppID（可选） | 微信模板消息通知 |
| `WEIXIN_APP_SECRET` | 微信公众号 AppSecret（可选） | 同上 |
| `WEIXIN_TOUSER` | 用户 OpenID（可选） | 消息接收人 |
| `WEIXIN_TEMPLATE_ID` | 模板 ID（可选） | 微信公众平台申请 |
| `FEISHU_WEBHOOK` | 飞书机器人 Webhook（可选） | 群聊中添加机器人获取 |

> LLM 支持所有兼容 OpenAI 接口的模型，切换只需改 Secret 值：DeepSeek、GPT-4o、GLM-4、通义千问等均可。
> 微信和飞书为可选配置，不配不影响核心评审流程。

---

## 七、验证

推送一次代码触发评审：

```bash
git add .
git commit -m "feat: 测试AI代码评审"
git push
```

然后在仓库 **Actions** 标签页观察工作流运行，完成后打开前端 `http://localhost:3000` 查看评审结果。

---

## 八、常见问题

### Q1：GitHub Actions 报错 `key is null`

某个环境变量未配置，检查 GitHub Secrets 是否遗漏。

### Q2：后端启动报数据库连接错误

确认 MySQL 已启动、`application.yml` 中用户名/密码正确、已执行 `sql/init.sql`。

### Q3：前端页面空白

浏览器 F12 → Network，看 `/api/` 请求状态，确认后端已启动且 `vue.config.js` 代理地址正确。

### Q4：微信/飞书收不到通知

不配不影响核心功能。如需使用，按对应平台文档申请权限即可。

---

## 九、配置检查清单

```
□ MySQL 数据库已创建并执行 init.sql
□ application.yml 中数据库连接 + api-token 已修改
□ 后端已启动（java -jar）
□ 前端 vue.config.js proxy 指向后端，npm run serve 启动
□ GitHub 仓库中已创建 .github/workflows/code-review.yml
□ GitHub Secrets 中 LLM_API_HOST / LLM_API_SECRET_KEY / LLM_MODEL_NAME 已配置
□ GitHub Secrets 中 BACKEND_URL / BACKEND_API_TOKEN / FRONTEND_URL 已配置
□ 推送一次代码验证全流程
```
