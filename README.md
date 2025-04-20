[TOC]

# Quinn原创账本

## 项目简介

>声明：该项目为 Quinn 原创
>
>编号：002
>
>[项目页面展示](./项目展示.md)

一个基于Spring Boot 3和Vue 3的 **个人账本** ，提供收支记录、预算管理、财务报表和理财建议等功能，基础功能完善
可以作为 课设、毕设 或者个人学习

## 技术栈

前后端 分离

### 后端技术栈
- Spring Boot 3.2.1
- Spring Security
- MyBatis-Plus
- JWT认证
- Swagger3接口文档
- MySQL数据库

### 前端技术栈
- Vue 3
- Element Plus
- Vue Router
- Pinia状态管理
- Axios HTTP客户端
- ECharts图表

## 项目结构
```
📁 002_finance
├── 📁 finance_springboot （Spring Boot后端）
├── 📁 finance_vue （Vue 3前端）
├── 📁 quinn-core-jar （核心jar包）
└── 📁 sql （SQL脚本与数据）
```

## 启动说明

### 数据库初始化
1. 创建MySQL数据库：`create database finance_db default charset utf8mb4;`
2. 执行`sql/finance_db.sql`创建表结构、导入测试数据

### 授权模块引入

1. 将 `quinn-core-jar`目录下的 `quinn`复制到，你本地maven仓库的`com`目录下

### 后端启动
1. 进入`finance_springboot`目录
2. 修改`application.yml`中的数据库配置,和授权配置 `quinn`

### 前端启动
1. 进入`finance_vue`目录
2. 执行`npm install`安装依赖
3. 执行`npm run dev`启动开发服务器
4. 默认端口：`5173`


## 前后端联调
1. 确保后端服务已启动（8080端口）
2. 前端开发服务器代理配置已指向后端地址
3. 使用测试账号登录：
   - 用户名：john_doe
   - 密码：123456

## 结语

虽然这个项目设计的功能已经能够满足各位同学的需求😎

But 🚫

如果基础功能无法满足你的骚操作🤹，或者在安装部署时遇到各种玄学问题👻......

**别慌！Quinn 提供以下贴心付费服务喔：**

🔧 **远程安装调试**（手残党专享 😅）

🌟 **定制全新功能**（学霸特权 🧠）

### 授权说明

最后最后✨，原创不易，为了防止有人悄悄搬运我的「摸鱼防秃心血结晶」🐾，只需花 10 秒钟 加 Quinn 好友📱，获取专属授权码～

But！别慌别慌🙅，统统免费🆓！加好友时记得备注 `002_途径_怎么称呼_邮箱`，方便快速通过哦～




> 📬 **联系方式如下**（欢迎各位帅哥美女发起好友请求~ ✨）
>
> 📱 **微信号**：`QuinnBox21`
> 
> 📧 **邮箱**：`c_qingyun2002@outlook.com`
> 
> 快来添加 **Quinn** 为好友吧～一起搞事，一起变强！💪😎
> 
> <img src="https://cqy-markdown-img.oss-cn-hangzhou.aliyuncs.com/markdown-img/202504172322012.png" alt="cdcf1614921bafdab34cbbdfadd4db1" style="zoom:20%;" />

