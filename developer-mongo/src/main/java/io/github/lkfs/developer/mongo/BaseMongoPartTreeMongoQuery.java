package io.github.lkfs.developer.mongo;

import io.github.lkfs.developer.mongo.entity.CommonEntity;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.ConvertingParameterAccessor;
import org.springframework.data.mongodb.repository.query.MongoEntityMetadata;
import org.springframework.data.mongodb.repository.query.MongoQueryMethod;
import org.springframework.data.mongodb.repository.query.PartTreeMongoQuery;
import org.springframework.data.repository.query.ValueExpressionDelegate;

/**
 * @author : Luis
 * @date : 2021/5/8 9:20
 */
public class BaseMongoPartTreeMongoQuery extends PartTreeMongoQuery {
    public BaseMongoPartTreeMongoQuery(MongoQueryMethod method, MongoOperations mongoOperations, ValueExpressionDelegate valueExpressionDelegate) {
        super(method, mongoOperations, valueExpressionDelegate);
    }

    @Override
    protected Query createQuery(ConvertingParameterAccessor accessor) {
        Query query = super.createQuery(accessor);
        MongoQueryMethod queryMethod = super.getQueryMethod();
        MongoEntityMetadata<?> entityInformation = queryMethod.getEntityInformation();
        Class<?> javaType = entityInformation.getJavaType();
        if (CommonEntity.class.isAssignableFrom(javaType)) {
            query.addCriteria(Criteria.where("deletedFlag").is(0));
        }
        return query;
    }
}
