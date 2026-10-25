package com.venusoft.developer.mymongo;

import com.venusoft.developer.mongo.BaseMongoRepositoryFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.venusoft.developer"})
@EnableMongoRepositories(
        basePackages = {
                "com.venusoft.developer.mymongo.dao.repository"
        },
        repositoryFactoryBeanClass = BaseMongoRepositoryFactoryBean.class
)
@EnableMongoAuditing


//@EnableAspectJAutoProxy(proxyTargetClass = true)
// 表示通过aop框架暴露该代理对象,AopContext能够访问
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
@EnableAsync
@ConfigurationPropertiesScan
@EnableConfigurationProperties

public class MyMongoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyMongoApplication.class, args);
    }

}
