# 动物园小程序

## 演示视频

### 微信小程序端

https://github.com/user-attachments/assets/e79a6d39-0a41-4952-af2b-9472408eb395

### Web管理后台

https://github.com/user-attachments/assets/bfdb10a4-0e8d-4ea1-a9cb-5dc3d464abfc


## 项目介绍

​		动物园管理系统是一个集动物信息管理、园区导览、用户互动于一体的综合性平台，包括三个主要组成部分：

1. **Web管理后台** - 基于Vue3和Element Plus构建，为管理员提供动物信息管理、用户管理、订单管理等功能
2. **微信小程序** - 为游客提供地图导航、动物信息查询、活动浏览、在线购票等服务
3. **SpringBoot后端** - 提供RESTful API接口，处理业务逻辑和数据存储

系统具有以下特点：

- 动物信息管理：支持动物分类、详细信息、位置坐标等管理
- 地图导航功能：基于高德地图API，提供园区导览和位置查询
- 用户互动功能：评论、收藏、分享等社交功能
- 在线购票系统：支持门票预订、优惠券领取、活动展示等功能
- 数据统计分析：提供一些数据的可视化展示

## 技术架构

**后端技术栈**

- **SpringBoot 3.2.0** - 主要框架
- **MyBatis-Plus** - ORM框架
- **Redis** - 缓存和分布式锁
- **RabbitMQ** - 消息队列
- **JWT** - 用户身份认证
- **MySQL** - 主要数据存储
- **Redisson** - Redis客户端
- **Lombok** - 简化Java代码
- **Hutool** - Java工具库
- **Alipay SDK** - 支付功能集成

**前端技术栈**

- **Vue 3** - Web管理后台主要框架
- **Element Plus** - UI组件库
- **Vite** - 构建工具
- **Axios** - HTTP客户端
- **ECharts** - 数据可视化

**小程序技术栈**

- **微信小程序原生开发**
- **高德地图微信小程序SDK**
- **AMAP微信小程序插件**

## 功能模块

- **后端服务模块（Spring Boot）** 

​		基于Spring Boot框架构建RESTful API服务，提供用户认证授权、动物信息管理、分类管理、票务订单处理、优惠券管理、园区活动发布、用户评论及点赞、收藏与浏览记录等核心功能模块。

- **管理端界面模块（Vue 3 + Element Plus）** 

​		提供直观的管理操作界面，包含首页数据统计展示、动物信息管理、动物分类设置、用户管理、管理员个人信息修改、门票与优惠券设置、订单处理、活动管理、评论管理等后台管理功能。

- **用户端小程序模块（微信小程序）** 

​		为游客提供便捷的动物园服务体验，包含首页信息展示、园区地图导航、动物详情浏览、门票购买、优惠券领取和使用、最新活动展示、个人中心、订单管理、收藏管理、浏览历史记录等用户功能。

## 项目结构

**后端项目结构 (SpringBoot)**

```
springboot
├── src
│   ├── main
│   │   ├── java/com/example
│   │   │   ├── common      // 通用配置和类
│   │   │   ├── controller  // 控制器层
│   │   │   ├── entity      // 实体类
│   │   │   ├── exception   // 异常处理
│   │   │   ├── interceptor // 拦截器
│   │   │   ├── listener    // 监听器
│   │   │   ├── mapper      // 数据访问层
│   │   │   ├── service     // 业务逻辑层
│   │   │   └── utils       // 工具类
│   │   └── resources       // 资源文件
│   └── test                // 测试代码
└── pom.xml                 // Maven依赖配置
```

**Web前端项目结构 (Vue3)**

```
vue
├── src
│   ├── assets              // 静态资源
│   ├── components          // 公共组件
│   ├── router              // 路由配置
│   ├── utils               // 工具类
│   ├── views               // 页面视图
│   ├── App.vue             // 根组件
│   └── main.js             // 入口文件
├── index.html              // HTML模板
└── package.json            // 项目依赖配置
```

**微信小程序项目结构**

```
MiniProgram/zoo
├── pages                   // 主要页面
│   ├── animals             // 动物列表页
│   ├── index               // 首页
│   ├── map                 // 地图导览页
│   ├── me                  // 个人中心页
│   └── subPages            // 子页面
│       ├── activity        // 活动相关页面
│       ├── animal/detail   // 动物详情页
│       ├── comment         // 评论相关页面
│       ├── favor           // 收藏页面
│       ├── look            // 浏览记录页面
│       ├── order           // 订单相关页面
│       ├── pay             // 支付页面
│       ├── share           // 分享页面
│       ├── ticket/detail   // 门票详情页
│       ├── user            // 用户相关页面
│       ├── voucher         // 优惠券页面
│       └── webview         // 支付宝网页支付页面
├── resources               // 资源文件
├── utils                   // 工具类
├── app.js                  // 小程序入口文件
├── app.json                // 小程序全局配置
└── config.js               // 配置文件
```

## 快速启动

### 环境准备

**后端环境要求**

- JDK 22
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.8+

**前端环境要求**

- Node.js 16+
- npm 8+

**小程序环境要求**

- 微信开发者工具

### 启动步骤

**1. 后端服务启动**

- 创建数据库并导入初始化脚本 (需要先创建zoo数据库)

- 修改 application.yml 配置文件中的数据库、Redis等连接信息

- 运行springboot项目

**2. Web管理后台启动**

1. 进入前端目录

```
cd vue
```

2. 安装依赖

```
npm install
```

3. 启动开发服务器

```
npm run dev
```

**3. 微信小程序启动**

1. 使用微信开发者工具打开MiniProgram的zoo目录
2. 修改config.js中的配置信息
3. 点击"编译"运行小程序

### 默认账号

- 管理员账号: admin/123456
- 普通用户: 在application.yml修改微信appid和secret可在小程序端用微信登录

