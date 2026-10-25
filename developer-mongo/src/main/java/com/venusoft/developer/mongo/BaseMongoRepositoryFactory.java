package com.venusoft.developer.mongo;

import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

/**
 * @author : Luis
 * @date : 2021/5/8 9:17
 */
public class BaseMongoRepositoryFactory extends MongoRepositoryFactory {
    private final MongoOperations operations;
    private final MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext;

    public BaseMongoRepositoryFactory(MongoOperations mongoOperations) {
        super(mongoOperations);
        this.operations = mongoOperations;
        this.mappingContext = mongoOperations.getConverter().getMappingContext();
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return BaseMongoRepositoryImpl.class;
    }

}

