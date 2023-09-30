# xuecheng-plus-project

![JDK](https://img.shields.io/badge/JDK-1.8-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.3.7.RELEASE-green)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-Hoxton.SR9-purple)
![Elasticsearch](https://img.shields.io/badge/Elasticsearch-7.12.1-yellow)
![Mybatis-plus](https://img.shields.io/badge/Mybatis%20plus-3.4.1-orange)

## 学成在线微服务项目

### 项目说明
```
├── api-test                   // api-test模块
├── doc                        // 项目各个章节文档
├── spl                        // 项目用到的数据库sql文件
├── static                     // 静态资源
├── xuecheng-plus-auth         // 认证授权模块
├── xuecheng-plus-base         // 项目配置/声明
├── xuecheng-plus-checkcode    // 验证码模块
├── xuecheng-plus-content      // 课程内容模块
├── xuecheng-plus-gateway      // 网关模块
├── xuecheng-plus-generator    // mybatis-plus generator生成器
├── xuecheng-plus-learning     // 选课学习模块
├── xuecheng-plus-media        // 媒资模块
├── xuecheng-plus-message-sdk  // 消息模块
├── xuecheng-plus-orders       // 订单支付模块
├── xuecheng-plus-parent       // 项目父工程
├── xuecheng-plus-search       // 搜索模块
└── xuecheng-plus-system       // 基础属性定义
```

### 项目介绍
![project](./static/project.png)

**内容模块介绍**

![content](./static/content_pic.png)

**媒资模块介绍**

![meida](./static/media_pic.png)

**认证授权模块介绍**

![anth](./static/auth_pic.png)

**选课学习授权模块介绍**

![learning](./static/learning_pic.png)

**订单支付授权模块介绍**

![order](./static/pay_pic.png)

### Maven说明
![maven](./static/maven.png)

### 技术栈
![技术栈](./static/stack.png)

### 集成说明
![devops](./static/devops.png)

### 网关说明
![devops](./static/spring-cloud-gateway.png)

### nginx配置文件

[nginx配置](./static/nginx.conf)

## 说明

1. 现阶段支付宝支付沙箱关键支持在浏览器直接模拟，无需安装沙箱支付宝app。只需要在沙箱账户注册账号即可。

2. 因为项目部署在jenkins侧流水线配置较为复杂，所以项目暂未在服务器实际部署测试。