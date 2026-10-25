# developer

## 介绍
开发平台

- 基于 Spring Boot 4.0.9 构建
- 自动 valid 校验
- 数据层使用 MyBatis-Plus；`developer-mongo` 模块提供 MongoDB 支持

## 版本
- JDK 17
- Spring Boot 4.0.9
- Spring Cloud 2025.1.4（[版本对照](https://github.com/spring-cloud/spring-cloud-release/wiki/Supported-Versions)）
- 当前版本 9.0.0（`io.github.lkfs:developer`）

> Maven groupId 与 Java 包名均为 `io.github.lkfs`。

## 软件架构

### 模块
| 模块 | 说明 |
|---|---|
| developer-common | 公共结果/异常、AES 加解密、FastJson 全局配置、日志脱敏 |
| developer-mybatis-plus | MyBatis-Plus 分页、审计字段填充、实体基类 |
| developer-web | WebMvc、Validator、全局异常、文件上传、脱敏与对象存储过滤器 |
| developer-redis | RedisTemplate、CacheManager、Redisson、分布式锁 |
| developer-auth | `@RequireRoles` / `@RequirePermissions` 接口鉴权 |
| developer-cloud | Feign 请求头透传 |
| developer-crypto | 请求体解密、响应体加密 |
| developer-mongo | MongoDB 自动配置 |

### 开发环境
- JDK 17
- IDEA 2023.1 及以上
- Groovy 3.0.7 以上

### idea必装插件
- GsonFormatPlus
- JRebel and XRebel
- Rainbow Brackets
- RestfulToolkit
- Alibaba Java Coding Guidelines


## 使用说明
### Maven 依赖
```xml
<dependency>
    <groupId>io.github.lkfs</groupId>
    <artifactId>developer-web</artifactId>
    <version>9.0.0</version>
</dependency>
```
按需替换 `developer-web` 为 `developer-common`、`developer-mybatis-plus`、`developer-redis`、`developer-auth`、`developer-cloud`、`developer-crypto`、`developer-mongo`。

在项目根目录执行以下命令，统一升级各子模块版本：
```bash
groovy pomUpgrade.groovy
```

### 示例工程环境变量
`my-web`、`my-mongo` 的配置通过环境变量注入，未设置时使用 localhost 默认值：

| 变量 | 说明 |
|---|---|
| `MYSQL_HOST` / `MYSQL_PORT` / `MYSQL_DATABASE` / `MYSQL_USERNAME` / `MYSQL_PASSWORD` | MySQL 连接 |
| `REDIS_HOST` / `REDIS_PORT` / `REDIS_PASSWORD` | Redis 连接 |
| `MONGO_URI` | MongoDB 连接串 |
| `RABBITMQ_HOST` / `RABBITMQ_PORT` / `RABBITMQ_USERNAME` / `RABBITMQ_PASSWORD` / `RABBITMQ_VHOST` | RabbitMQ 连接 |
| `ALIYUN_OSS_ACCESS_KEY_ID` / `ALIYUN_OSS_ACCESS_KEY_SECRET` / `ALIYUN_OSS_ENDPOINT` / `ALIYUN_OSS_PUBLIC_BUCKET` / `ALIYUN_OSS_PRIVATE_BUCKET` | 阿里云 OSS |
| `TENCENT_COS_SECRET_ID` / `TENCENT_COS_SECRET_KEY` | 腾讯云 COS |
| `WECHAT_MP_APP_ID` / `WECHAT_MP_APP_SECRET` / `WECHAT_MP_TOKEN` | 微信公众号 |
| `WECHAT_MINIPROGRAM_STAFF_APP_ID` / `WECHAT_MINIPROGRAM_STAFF_APP_SECRET` | 员工端小程序 |
| `WECHAT_MINIPROGRAM_RESIDENT_APP_ID` / `WECHAT_MINIPROGRAM_RESIDENT_APP_SECRET` | 居民端小程序 |
| `CRYPTO_AES_SECRET_KEY` / `CRYPTO_AES_IV` | 通信加密密钥 |

### 数据库建模
实体继承 `developer-mybatis-plus` 提供的基类后，框架自动填充审计字段：

- `BaseLogicDeleteTab`：继承 `BaseAuditTab`，额外通过 `@TableLogic` 按 `deleted_flag` 软删除
- `BaseAuditTab`：只填充 `created_by`、`created_at`、`updated_by`、`updated_at`，无软删除

表结构需要包含对应字段（`created_by` / `updated_by` 保存 String 类型的用户 ID）：
```mysql
   created_by           varchar(64) comment '创建用户',
   created_at           datetime default CURRENT_TIMESTAMP comment '创建时间戳',
   updated_by           varchar(64) comment '更新用户',
   updated_at           datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间戳',
   deleted_flag         smallint default 0 comment '删除标记',
```
`status`、`note`、`sort` 等为业务字段，按需添加，框架不自动维护。

### 项目构建
- 项目采用多模块方式构建，其中至少有一个sdk的模块，用于存放entity、repository、manager
- 同时项目下可以有多个业务模块，并且每个模块下可以建立多个子package，用于业务划分

### 代码生成
1. 准备
- 使用groovy3.0.7以上版本，并配置Windows环境变量，[下载](https://groovy.apache.org/download.html)
- 在项目的根目录下，创建 jCoder.json 文件（文件名区分大小写），最小内容如下，其余字段（`context`、`tags`、`routeRaw`、`entityClassName` 等）由脚本交互式写入
```json
{
  "workModuleFolder": "my-mysql",
  "workModulePackage": "io.github.lkfs.developer.mysql.mymysql",
  "sdkModuleFolder": "my-mysql",
  "sdkModulePackage": "io.github.lkfs.developer.mysql.mymysql"
}
```

2. 生成数据层，包括 entity、repository、manager
- 拷贝 jMySQL.groovy 到 idea的数据库扩展脚本目录下，路径形如 `C:\Users\<用户>\AppData\Roaming\JetBrains\<IDE版本>\extensions\com.intellij.database\schema`
- 修改文件中的 baseDir 变量为项目的根目录
- 建立数据库连接
- 在库表中，右键 -> Scripted Extensions -> jMySQL.groovy
- 在 sdkModuleFolder 指定的模块中生成对应文件

3. 生成业务层（controller、qo、vo）
- 拷贝 jCoder.groovy 及 template 目录到项目的根目录下
- 在项目的根目录下，执行 groovy jCoder.groovy，依提示输入模块、路由及实体名
- 路由以 `-` 分隔，最后一段为操作：`query`、`list`、`get`、`save`、`delete`、`set-sort`、`set-status`；以 `-crud` 结尾时生成整套接口
- 在 workModuleFolder 指定的模块中生成对应文件

4. 生成 Vue 页面（可选）
- 在项目的根目录下，执行 groovy jVue.groovy，依提示输入 vueWorkFolder、vuePath，生成列表页、编辑页、弹窗及 API 文件

> 注意：`template/` 下的模板仍指向已移除的 JPA/SQL 层（如 `io.github.lkfs.developer.sql.*`、`io.github.lkfs.developer.common.entity.*`），当前 9.0.0 的构建模块不包含这些类。使用代码生成前需先按现有模块（`developer-mybatis-plus`、`developer-common`、`developer-web`）调整模板。

### 自动装配
各模块自带 Spring Boot 自动装配（`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`），引入依赖即生效，不需要在启动类上扫描 `io.github.lkfs`。

旧项目即使保留 `scanBasePackages = "io.github.lkfs"` 也不会重复注册 Bean，可以按需逐步移除。

### JSON 序列化
`FastJsonConfig` 与 `HttpMessageConverters` 由 developer-common 统一创建（`DeveloperJsonAutoConfiguration`）：

- 日期格式固定为 `millis`
- 容器中的 fastjson2 `Filter` Bean 会自动加入 writerFilters（web 模块以此提供脱敏、枚举、对象存储过滤器）
- 需要覆盖时，声明同类型的 `FastJsonConfig` 或 `HttpMessageConverters` Bean 即可

### 注解
1. 脱敏注解
```java
    //返回前端的dto中的字段增加如下注解
    @Desensitization(type = DesensitizationType.PHONE) //手机号脱敏
    private String phoneNumber;
    
    @Desensitization(type = DesensitizationType.ID_CARD) //身份证脱敏
    private String idCardNum;
```    
2.  阿里云对象存储注解
```
    //yml中的配置
    ali:
      oss:
        endpoint: oss-cn-beijing.aliyuncs.com  #接入点地址
        accessKeyId: ${ALIYUN_OSS_ACCESS_KEY_ID:}   #用户id
        accessKeySecret: ${ALIYUN_OSS_ACCESS_KEY_SECRET:} #秘钥
        publicBucket: ${ALIYUN_OSS_PUBLIC_BUCKET:}   #开放桶名称
        privateBucket: ${ALIYUN_OSS_PRIVATE_BUCKET:}    #私有桶名称
```
配置 `accessKeyId` 且 SDK 在 classpath 时注册 `AliOssService`。其他厂商的配置前缀与触发属性：

- 华为云：`huawei.obs.accessKey`，注解 `@HuaweiObs`
- 腾讯云：`tencent.cos.secretId`，注解 `@TencentCos`
- 七牛云：`qiniu.kodo.accessKey`，注解 `@QiniuKodo`
- AWS S3：`aws.s3.accessKey`

```java
    //返回前端的dto中的字段增加如下注解
    @AliOss(type = BucketType.PRIVATE) //私有桶解密后转为url
    private String idCardImg;
        
    @AliOss(type = BucketType.PUBLIC) //开放桶转为url
    private String avatar;
```    
3. 日期格式化
```java
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
```
全局默认日期格式为 `millis`，字段上的 `@JSONField(format = ...)` 可单独覆盖。

### 通信 body 加解密
由 developer-crypto 提供；若在网关统一处理，可不引入该模块。
```yaml
crypto:
  enabled: false          #是否启用通信加密
  aesSecretKey: 32位密钥
  aesIv: 16位偏移量
  excludes:               #排除的路径，支持 Ant 风格
    - /**/system-user-query
    - /**/system-user-delete
```
- `enabled=true` 时，请求体需为 `{"data":"密文"}`，响应体同样包装为 `{"data":"密文"}`
- 命中 `excludes` 的路径不加密
- `enabled=false` 时，若请求体中存在非空的 `data` 字段，仍会按密文解密

### 文件上传
本地存储：
- `POST /file/upload`，`multipart/form-data`，参数名 `file`，可选参数 `dir`
- 配置 `developer.upload.path`（存储根目录）与 `developer.upload.host`（返回 URL 前缀）
- `dir` 只能是 `developer.upload.path` 下的子目录

对象存储直传签名，请求体均为 `{"bucketName":"public|private","dir":"...","objectName":"..."}`：
- `POST /file/upload/getAliToken`
- `POST /file/upload/getQiniuToken`
- `POST /file/upload/getTencentToken`
- `POST /file/upload/getHuaweiToken`
- `POST /file/upload/getS3Token`

以上为 Controller 映射路径，实际访问路径是否带 `/dp` 等前缀取决于消费方的 `server.servlet.context-path` 或网关路由。

### Redis
分布式锁，方法上增加该注解，支持 spel 表达式
```java
@RedisLock(key = "AdminWorkNoticeQueryService", value = "#qo.search?:'default'", ttl = 100)
```
- `key` 为锁前缀，`value` 为 SpEL 后缀
- `ttl` 为等待加锁超时，单位毫秒
- `leaseTime` 为锁持有时长，单位毫秒；默认 `-1` 表示启用 Redisson watchdog 自动续期

支持 @Cacheable 缓存
```java
@Cacheable(value = "cache_menu_tree", key = "#root.method.name")
public List<MenuTreeVoBean> getRootMenuTreeVoList() {
    ...
}
```
- 默认缓存过期时间 10 分钟
- 可通过 `spring.cache.ttl.<cacheName>` 按缓存名覆盖，单位秒
```yaml
spring:
  cache:
    ttl:
      cache_menu_tree: 600
```
连接配置使用 `spring.data.redis.*`（host、port、password、database、timeout、lettuce.pool.*）。
`developer.redis.auto-type-packages` 控制反序列化允许的类名前缀，默认 `io.github.lkfs.,java.util.,java.math.`。

### 鉴权
引入 developer-auth 后，可在方法上使用：
```java
@RequireRoles({"admin"})
@RequirePermissions({"system:user:list"})
```
角色与权限从 `SecurityContextHolder` 中的 `IBaseUser` 读取。

### MyBatis-Plus
- `MybatisPlusConfig` 已注册 MySQL 分页插件
- `MyMetaObjectHandler` 自动填充 `createdBy`、`createdAt`、`updatedBy`、`updatedAt`，用户 ID 来自 `SecurityUtils.getUserId(String.class)`
- 分页 QO 继承 `BasePageQO`，`getPage()` 返回 MyBatis-Plus 的 `IPage`（内部为 `Page`，页码从 1 开始，单页最大 500）
```java
    IPage<UserTab> page = userMapper.selectPage(qo.getPage(), wrapper);
```

#### 意见与反馈
<https://github.com/lkfs/developer/issues>

## License
本项目基于 [Apache License 2.0](LICENSE) 开源。
