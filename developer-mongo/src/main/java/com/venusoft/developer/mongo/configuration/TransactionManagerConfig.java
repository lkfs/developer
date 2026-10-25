package com.venusoft.developer.mongo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

//@Configuration
public class TransactionManagerConfig {

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(MongoDatabaseFactory factory) {
        PlatformTransactionManager mongoTransactionManager = new MongoTransactionManager(factory);
        return mongoTransactionManager;
    }

}

