# High Concurrency Project Memory

## 1. 项目定位
- 项目类型：高并发抢票票务平台（大麦类场景简化版）。
- 当前范围：票档级库存、无选座、模拟支付、超时关单释放库存。
- 目标：形成可演示、可压测、可迭代的高并发全链路样例（网关限流、缓存预扣、MQ削峰、MySQL最终落库、对账补偿）。
- 扩展规划（文档）：分库分表、微服务边界、跨服务最终一致与 MQ 可靠投递，见 `docs/高并发能力地图与扩展架构-抢票票务平台.md`（分期落地，与代码实现进度区分）。

## 2. 当前业务结论（已定）
- 业务方向：抢票票务，而非权益领取。
- 库存口径：`remaining = total_stock - sold_stock`。
- `sold_stock` 口径：包含待支付占用 + 已支付；超时关单后回退。
- 订单状态主线：待支付 -> 已支付 / 已关闭。
- 首期不做：选座、真实支付清结算、多地多活、复杂实名核验。

## 3. 技术与架构约束（摘要）
- 架构分层：`Controller -> Service -> DAL`，禁止越层。
- 模块边界：`{module}-api`（接口契约）与 `{module}-server`（实现）分离。
- 跨模块调用：仅依赖 `*-api`，禁止直接依赖实现类。
- CRUD规范：按 DO -> Mapper -> VO -> Controller -> Service -> 错误码流程执行。
- 数据规范：金额用分（整数），主键 BIGINT，统一公共字段（creator/create_time/updater/update_time/deleted/tenant_id）。
- 后端坐标：`groupId=com.hc.ticket`，包名 `com.hc.ticket.module.tkt`；启动类 `com.hc.ticket.TktServerApplication`。
- 技术栈落地：Java 17 + Spring Boot 3.3.6 + MyBatis-Plus 3.5.9 + Redis + RocketMQ（默认关闭，profile `mq` 启用）。

## 4. 文档索引（docs）

### 4.1 规范与参考
1. `docs/通用架构设计与代码结构规范.md`
2. `docs/CRUD开发流程规范.md`
3. `docs/MySQL并发性能优化.md`
4. `docs/高并发系统通用设计.md`

### 4.2 项目输出文档
1. `docs/需求分析-高并发抢票票务平台.md`
2. `docs/数据库设计-抢票票务平台.md`
3. `docs/技术文档-抢票票务平台.md`
4. `docs/业务逻辑文档-抢票票务平台.md`
5. `docs/设计文档-抢票票务平台.md`
6. `docs/高并发能力地图与扩展架构-抢票票务平台.md`

### 4.3 数据库脚本
1. `schema-ticket-mysql8.sql`

### 4.4 后端工程（代码）
1. 根 `pom.xml`（多模块父工程）
2. `tkt/tkt-api`：枚举、错误码、API 常量
3. `tkt/tkt-server`：启动类、框架公共类、DO/Mapper、管理端 CRUD（演出/场次/票档 + 订单只读）、MQ/Job 骨架、探活接口

### 4.5 运行记忆与纠错文档
1. `MEMORY.md`
2. `ERROR.md`

## 5. 使用说明（给 AI / 开发者）
- 在输出新文档、方案、代码前，先阅读本文件以快速对齐上下文。
- 若新增或修改了文档、脚本、关键业务结论，必须同步更新本文件对应章节。
- 若业务范围变更，优先更新“项目定位”“当前业务结论”和“文档索引”。

## 6. 最近更新记录
- 2026-07-22：落地管理端 CRUD（Show/Session/Tier 增删改查分页；Order 只读详情+分页），遵循 `docs/CRUD开发流程规范.md`；补 `BeanUtils.toBean`、票档库存校验错误码 `TIER_STOCK_INVALID`。
- 2026-07-22：落地后端脚手架（Java 17 / Spring Boot 3.3 / `tkt-api`+`tkt-server`）；含 DO/Mapper、错误码与枚举、RocketMQ 消费者骨架（默认关闭）、关单 Job 占位、探活接口；`mvn -DskipTests package` 通过。
- 2026-05-10：初始化 Git 仓库并推送到远程 `git@github.com:Pderi/High-Concurrency-Project.git`（默认分支 `main`）；新增根目录 `.gitignore`（忽略 `.idea/` 与常见构建产物）。
- 2026-05-10：新增 `docs/高并发能力地图与扩展架构-抢票票务平台.md`（能力地图 + 分库分表/微服务/一致性/MQ 扩展叙述），并更新本文档索引与项目定位中的扩展说明。
- 2026-05-10：新增 Cursor 始终生效规则：`Agent工作流-命令前提示词优化.mdc`、`Agent工作流-质量优先与任务拆分.mdc`、`Agent工作流-方案确认后再编码.mdc`。
- 2026-04-20：创建 memory 文档，补齐抢票项目上下文与文档索引。
- 2026-04-20：新增 `ERROR.md`，用于记录生成过程中的错误与改进闭环。
