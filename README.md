IoT Platform（物联网设备数据监控与告警平台）

一个面向物联网场景的 设备数据采集、在线状态管理、历史数据存储、异常告警与异步通知 平台。
基于 Spring Cloud 微服务架构，通过 HTTP + MQ 实现数据接入与业务解耦，适用于工业设备监控、环境监测等场景。

一、项目背景

在物联网场景中，设备数量大、上报频率高，如果所有数据都同步处理，系统在高峰期容易出现性能瓶颈。

本项目通过 网关统一接入 + 消息队列异步解耦 + Redis 状态管理 的方式，构建一个具备可扩展性、稳定性与可维护性的设备数据平台。

二、系统架构（Architecture）
2.1 业务概览

系统将设备抽象为 周期性上报数据的客户端，整体流程如下：

设备通过 HTTP 接口上报数据到网关

网关统一完成鉴权与路由

业务微服务处理核心逻辑并落库

异步链路通过 RabbitMQ 解耦

告警与通知由独立服务异步消费处理

2.2 系统架构示意（可复制，防乱版）

设备端
↓ HTTP
Gateway（统一鉴权）
↓
业务微服务
↓
RabbitMQ（异步解耦）
↓
Consumer / 告警处理 / 通知服务

三、技术架构与选型
3.1 技术选型

后端框架：Spring Boot / Spring Cloud Gateway

服务治理：Nacos

消息中间件：RabbitMQ

缓存组件：Redis

数据持久化：MyBatis + MySQL

认证鉴权：JWT

定时任务：XXL-Job

服务通信：OpenFeign

构建工具：Maven

四、核心功能设计（Key Features）
4.1 设备数据上报与异步解耦

设计 HTTP → RabbitMQ → Consumer 的设备数据上报链路

使用消息队列实现上报与处理解耦，提升系统抗峰值能力

Producer / Consumer 采用 手动 ACK，保证消息可靠消费

引入 死信队列（DLQ） 处理消费失败的异常消息

4.2 上报限流与重复数据治理

基于 Redis 实现设备 1 秒内上报限流，防止设备频繁请求

使用 消息唯一 ID（msgId）+ Redis 去重，解决 MQ 重复消费问题

保证设备数据写入的幂等性与一致性

4.3 设备在线状态与心跳机制

使用 Redis Key TTL + 心跳刷新实现设备在线状态管理

定时任务（XXL-Job）扫描超时设备，批量更新离线状态

减少频繁数据库更新，降低系统压力

4.4 告警检测与告警去重

基于设备上报数据实现阈值告警逻辑

支持多指标扩展（温度、湿度、电量等）

使用 Redis 实现告警去重与短时间内重复告警抑制，避免告警风暴

4.5 告警事件异步通知与解耦设计

将告警产生与通知处理解耦

基于 RabbitMQ 设计独立的 notification-service

采用 手动 ACK + DLQ，保障告警消息可靠投递

消费端落库记录通知事件，形成“告警 → 通知 → 可追溯”闭环

预留短信 / 邮件 / Webhook 等多渠道通知扩展能力

4.6 微服务治理与接口安全

使用 Spring Cloud Gateway + JWT 实现统一鉴权

网关隔离内部微服务，避免服务直接暴露

通过 OpenFeign 完成服务间调用，保持职责清晰

4.7 数据存储与查询

使用 MyBatis + MySQL 存储设备历史数据

支持按设备维度进行历史数据查询与统计分析

为后续数据可视化与趋势分析提供基础

4.8 稳定性与可维护性设计

编写设备状态检测、历史数据清理等定时任务

提升系统长期运行稳定性

使用 Postman + 接口文档对服务进行独立验证

支持服务模块独立部署与测试

五、项目结构说明（Project Structure）

iot-platform
├── iot-gateway    网关服务（统一鉴权）
├── iot-data     设备数据服务
├── iot-device    设备管理服务
├── iot-user     用户与鉴权服务
├── notification-service 告警通知服务
└── pom.xml     父工程 POM

六、运行环境（Environment）

JDK：17+（当前使用 Temurin 17.0.17）

MySQL：8.x

Redis：6.x / 7.x

RabbitMQ：3.x

Nacos：2.x

XXL-Job：2.x

Maven：3.6+

七、快速启动说明

启动基础组件：MySQL、Redis、RabbitMQ、Nacos

配置各服务的数据库、Redis、MQ、Nacos 地址

启动顺序建议：

iot-user

iot-gateway

iot-device / iot-data

notification-service

使用 Postman 验证登录、设备上报、告警链路

八、说明（Notes）

本项目为个人学习与实战项目，重点在于：

分布式微服务架构设计

MQ 异步解耦与可靠消费

Redis 状态管理与幂等控制

告警链路闭环与系统稳定性设计
