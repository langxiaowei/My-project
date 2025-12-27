# IoT Platform（物联网设备数据监控与告警平台）

一个面向物联网场景的 **设备数据采集 + 在线状态管理 + 历史数据存储 + 异常告警 + 异步通知** 平台。  
基于 Spring Cloud 微服务架构，通过 **HTTP + MQ** 实现数据接入与解耦处理，支持设备在线判定、指标趋势分析与异常告警，适用于工业设备监控、环境监测等场景。

---

## 1. 技术栈（Tech Stack）

- **后端框架**：Spring Boot / Spring Cloud Gateway
- **服务治理**：Nacos
- **消息中间件**：RabbitMQ
- **缓存组件**：Redis
- **数据持久化**：MyBatis + MySQL
- **认证鉴权**：JWT
- **定时任务**：XXL-Job
- **服务通信**：OpenFeign
- **构建工具**：Maven

---

## 2. 系统架构（Architecture）

### 2.1 业务概览
系统将设备抽象为 **周期性上报数据的客户端**：
- 设备通过 HTTP 上报数据到网关
- 网关统一鉴权与路由
- 业务微服务处理与落库
- 异步链路通过 RabbitMQ 解耦
- 告警与通知由独立服务异步消费处理

### 2.2 系统架构示意（可复制，防乱版）

```text
设备端
↓ HTTP
Gateway（统一鉴权）
↓
业务微服务
↓
RabbitMQ（异步解耦）
↓
Consumer / 告警处理 / 通知服务
3. 核心功能设计（Key Features）
3.1 设备数据上报与异步解耦
设计 HTTP → RabbitMQ → Consumer 的设备数据上报链路，引入消息队列实现上报与处理解耦，提升系统抗峰值能力

通过 RabbitMQ Producer/Consumer + 手动 ACK 机制，确保消息可靠消费，避免数据丢失

使用 死信队列（DLQ） 处理消费失败的异常消息，增强系统可观测性与可恢复性

3.2 上报限流与重复数据治理
基于 Redis 实现设备上报 1 秒内限流/幂等 控制，防止设备频繁上报导致的数据重复写入

在消费端结合 消息唯一 ID（msgId）+ Redis 去重，解决 MQ 重投与重复消费问题，保证数据一致性

3.3 设备在线状态与心跳机制
使用 Redis Key TTL + 心跳刷新实现设备在线状态管理

结合定时任务（XXL-Job）扫描超时设备，批量更新离线状态，减少数据库压力
-（可优化）离线更新时若 DB 已是离线状态则跳过，降低无意义更新调用

3.4 告警检测与告警去重
基于设备上报数据实现阈值告警逻辑，支持多指标（温度、湿度、电量等）扩展

使用 Redis 实现告警去重与短时间内重复告警抑制，避免告警风暴

3.5 告警事件异步通知与解耦设计
将告警产生与通知处理解耦，基于 RabbitMQ 设计独立的 notification-service 通知服务

采用 手动 ACK + DLQ 机制，保障告警消息可靠投递与异常兜底

消费端落库记录通知事件，形成“告警 → 通知 → 可追溯”闭环

预留短信/邮件/Webhook 等多渠道通知扩展空间

3.6 微服务治理与接口安全
通过 Spring Cloud Gateway + JWT 实现统一鉴权与接口隔离

使用 OpenFeign 完成服务间调用，保持服务职责清晰

3.7 数据存储与查询
使用 MyBatis + MySQL 完成设备历史数据存储与查询

支持按设备维度进行历史数据查询与统计分析（可扩展：按时间区间、指标聚合等）

3.8 稳定性与可维护性设计
编写设备状态检测、历史数据清理等定时任务，提升系统长期运行稳定性

使用 Postman + 接口文档对服务进行独立验证，确保模块可单独部署与测试

4. 项目结构说明（Project Structure）
多模块 Maven 工程结构如下（可复制，防乱版）：

text
复制代码
iot-platform
├── iot-gateway               # 网关服务（统一鉴权）
├── iot-data                  # 设备数据服务（数据接入/存储/查询）
├── iot-device                # 设备管理服务（设备信息/状态等）
├── iot-user                  # 用户与鉴权服务（JWT/登录）
├── notification-service      # 告警通知服务（异步消费/落库/扩展通知渠道）
└── pom.xml                   # 父工程 POM
5. 运行环境（Environment）
你当前项目的 Java 版本是 JDK 17（Temurin 17.0.17），不是 JDK8。

JDK：17+（当前：Temurin 17.0.17）

MySQL：8.x

Redis：6.x/7.x 均可

RabbitMQ：3.x

Nacos：2.x

XXL-Job：2.x

Maven：3.6+（建议 3.8+）

6. 快速开始（Quick Start）
6.1 准备基础服务
请先确保以下服务可用：

MySQL

Redis

RabbitMQ

Nacos
-（可选）XXL-Job

6.2 配置项
各微服务的 application.yml 中需要配置：

MySQL 连接信息（url/username/password）

Redis 地址与密码

RabbitMQ 地址与账号

Nacos 地址与命名空间（如有）

建议：把敏感配置放在本地 application-dev.yml 或 Nacos 配置中心，避免提交到公网仓库。

6.3 启动顺序建议
Nacos

MySQL / Redis / RabbitMQ

iot-user（鉴权相关）

iot-gateway（网关）

iot-device / iot-data（业务服务）

notification-service（异步通知）

6.4 验证思路（建议）
先验证网关是否能路由到各服务

再验证设备上报接口（HTTP → MQ）

最后验证消费端落库、告警与通知链路

7. 接口与测试（API & Testing）
建议使用 Postman 保存一套 Collection：

登录获取 JWT

设备上报数据

查询设备历史数据

触发阈值告警（模拟测试数据）

8. Roadmap（可继续完善的点）
 上报接口加幂等/限流（同一设备 1 秒内多次上报只算一次心跳）

 离线判定优化：离线批处理跳过 DB 已离线设备，减少无意义调用

 关键日志与指标：上报量、消费耗时、离线批处理数量/耗时等

 通知渠道扩展：邮件/短信/Webhook

 接口文档（Swagger/OpenAPI）与部署文档补全

9. 说明（Notes）
本项目为个人学习与实战项目，重点在于：

分布式微服务架构

MQ 异步解耦

Redis 状态管理 + 幂等去重

告警链路闭环与可扩展通知

可维护性与稳定性设计

欢迎交流与改进建议。
