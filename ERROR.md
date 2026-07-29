# High Concurrency Project Error Log

## 1. 文档目的
- 记录在生成文档、方案、代码过程中出现的错误与偏差。
- 将错误沉淀为可执行改进项，用于后续自我强化与迭代。
- 避免重复犯同类错误，提升输出一致性与质量。

## 2. 使用规则
1. 每次开始生成内容前，先快速阅读本文件。
2. 发现错误后，当次会话内立即补充记录，不延后。
3. 记录必须包含：错误现象、根因、修复动作、预防措施。
4. 同类错误出现 2 次以上时，必须抽象为“检查清单项”。

## 3. 错误记录模板
```markdown
### [YYYY-MM-DD] 错误标题
- 场景：
- 错误现象：
- 根因分析：
- 修复动作：
- 预防措施（下次如何避免）：
- 状态：已修复 / 待修复
```

## 4. 错误记录

### [2026-07-29] LambdaQueryWrapperX 链式混用 eq 导致编译失败
- 场景：P3 新增 `selectAppPage`，先 `.eq(userId)` 再 `.eqIfPresent(...)`。
- 错误现象：编译报找不到 `eqIfPresent`（接收类型变成父类 `LambdaQueryWrapper`）。
- 根因分析：父类 `eq` 返回 `LambdaQueryWrapper`，丢失子类扩展方法。
- 修复动作：统一用 `eqIfPresent`（或先保证返回类型为 `LambdaQueryWrapperX`）。
- 预防措施（下次如何避免）：`LambdaQueryWrapperX` 链上避免直接调用父类 `eq/like` 后再接 `*IfPresent`。
- 状态：已修复

### [2026-07-23] 业务层用 try-catch 吞掉缓存异常
- 场景：C 端场次详情 Redis 短缓存。
- 错误现象：`SessionServiceImpl` 用 try-catch 降级，失败只打日志，与项目「不满足条件抛错误码」风格不一致。
- 根因分析：把基础设施失败当成可忽略分支，而不是显式校验 + 业务错误码。
- 修复动作：去掉 try-catch；详情直查 DB，用 `if` + `SESSION_NOT_EXISTS` 等错误码；缓存失效留空待 P2。
- 预防措施（下次如何避免）：Service 层禁止 try-catch 吞异常；分支用 `if`，失败 `throw exception(ErrorCode)`。
- 状态：已修复

### [2026-07-22] MyBatis-Plus 3.5.9 分页插件类找不到
- 场景：搭建 `tkt-server` 脚手架编译。
- 错误现象：`PaginationInnerInterceptor` 找不到（`com.baomidou.mybatisplus.extension.plugins.inner`）。
- 根因分析：MyBatis-Plus 3.5.9 起分页/拦截相关类拆至 `mybatis-plus-jsqlparser` 模块，仅引入 `mybatis-plus-spring-boot3-starter` 不够。
- 修复动作：父 POM 与 `tkt-server` 增加 `mybatis-plus-jsqlparser` 依赖。
- 预防措施（下次如何避免）：升级 MyBatis-Plus 后核对官方拆分模块说明；脚手架检查清单增加「分页插件依赖」。
- 状态：已修复

### [2026-04-20] 初始建立错误日志
- 场景：项目规范治理初始化。
- 错误现象：暂无历史错误沉淀文档，无法形成可复用的纠错闭环。
- 根因分析：前期重点在输出需求/技术/设计文档，缺少独立“错误学习”载体。
- 修复动作：创建 `ERROR.md`，定义模板与记录规则。
- 预防措施（下次如何避免）：新项目启动时同步创建 `MEMORY.md` + `ERROR.md`。
- 状态：已修复

## 5. 个人检查清单（持续补充）
- [ ] 生成前是否已阅读 `MEMORY.md` 与 `ERROR.md`
- [ ] 输出是否与已定业务边界一致（首期无选座、模拟支付）
- [ ] 文档路径与索引是否同步更新
- [ ] 是否出现了与规则冲突的表达或结构
- [ ] MyBatis-Plus >=3.5.9 是否已引入 `mybatis-plus-jsqlparser`（分页/乐观锁拦截器）
- [ ] Service 层是否避免 try-catch 吞异常；业务分支是否用 `if` + `throw exception(错误码)`
- [ ] `LambdaQueryWrapperX` 链上是否避免父类 `eq` 后再接 `*IfPresent`（保持 X 类型）
