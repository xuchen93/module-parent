# module-parent

#### 介绍

快速搭建项目的基础框架，通过springboot集成redis、mysql数据库的curd、jwt、请求日志拦截、springMVC的valid校验等微服务项目通用功能

#### 软件架构

springboot为核心，主要依赖mysql、mybatis、mybatis-plus、redis

#### 使用说明

依赖项目，可根据实际需要依赖不同模块，全都要依赖module-server base模块为基础模块 core模块封装了普通项目通用功能 web模块为依赖web，并封装了web一些常用功能
database模块封装了数据库的基本功能，包括创建、更新信息、乐观锁等增强功能 可根据XuchenProperties配置作为参考入口
