package io.github.lkfs.developer.mongo;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;

/**
 * @author : Luis
 * @date : 2021/5/8 9:35
 */
public class BaseMongoRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends MongoRepositoryFactoryBean<T, S, ID> {
    public BaseMongoRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected BaseMongoRepositoryFactory getFactoryInstance(MongoOperations operations) {
        return new BaseMongoRepositoryFactory(operations);
    }
}

