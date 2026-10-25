package io.github.lkfs.developer.mongo;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.lang.Nullable;
import io.github.lkfs.developer.common.exception.BizException;
import io.github.lkfs.developer.mongo.entity.CommonEntity;
import io.github.lkfs.developer.mongo.entity.SysNextIdEntity;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.util.StreamUtils;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : Luis
 * @date : 2021/5/8 11:00
 */
@NoRepositoryBean
public class BaseMongoRepositoryImpl<T, ID extends Serializable> implements IBaseMongoRepository<T, ID> {

    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, ID> entityInformation;

    public BaseMongoRepositoryImpl(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
        this.entityInformation = metadata;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public List<T> findAll(@Nullable Query query) {
        return query == null ? Collections.emptyList() : this.mongoOperations.find(query, this.entityInformation.getJavaType(), this.entityInformation.getCollectionName());
    }

    @Override
    public Page<T> findAll(Query query, Pageable pageable) {
        Assert.notNull(pageable, "Pageable must not be null!");
        Long count = this.count(query);
        List<T> list = this.findAll(query.with(pageable));
        return new PageImpl(list, pageable, count);
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        return this.findAll(new Query((new Criteria(this.entityInformation.getIdAttribute())).in((Collection) Streamable.of(ids).stream().collect(StreamUtils.toUnmodifiableList()))));
    }

    @Override
    public <S extends T> S save(S entity) {
        Assert.notNull(entity, "Entity must not be null!");
        return this.entityInformation.isNew(entity) ? this.mongoOperations.insert(entity, this.entityInformation.getCollectionName()) : this.mongoOperations.save(entity, this.entityInformation.getCollectionName());
    }

    @Override
    public <S extends T> S save(S entity, String collectionName) {
        Assert.notNull(entity, "Entity must not be null!");
        return this.entityInformation.isNew(entity) ? this.mongoOperations.insert(entity, collectionName) : this.mongoOperations.save(entity, collectionName);
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        Assert.notNull(entities, "The given Iterable of entities not be null!");
        Streamable<S> source = Streamable.of(entities);
        boolean allNew = source.stream().allMatch((it) -> {
            return this.entityInformation.isNew(it);
        });
        if (allNew) {
            List<S> result = (List) source.stream().collect(Collectors.toList());
            return new ArrayList(this.mongoOperations.insert(result, this.entityInformation.getCollectionName()));
        } else {
            return (List) source.stream().map(this::save).collect(Collectors.toList());
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        Assert.notNull(id, "The given id must not be null!");
        return Optional.ofNullable(this.mongoOperations.findById(id, this.entityInformation.getJavaType(), this.entityInformation.getCollectionName()));
    }

    @Override
    public boolean existsById(ID id) {
        Assert.notNull(id, "The given id must not be null!");
        return this.mongoOperations.exists(this.getIdQuery(id), this.entityInformation.getJavaType(), this.entityInformation.getCollectionName());
    }

    private Query getIdQuery(Object id) {
        Criteria idCriteria = Criteria.where(this.entityInformation.getIdAttribute()).is(id);
        return new Query(idCriteria);
    }

    @Override
    public long count() {
        return this.count(new Query());
    }

    @Override
    public long count(Query query) {
        return this.mongoOperations.count(query, this.entityInformation.getCollectionName());
    }

    @Override
    public void deleteById(ID id) {
        this.deleteById(id, false);

    }

    @Override
    public void deleteById(ID id, boolean force) {
        if (force) {
            Assert.notNull(id, "The given id must not be null!");
            this.mongoOperations.remove(this.getIdQuery(id), this.entityInformation.getJavaType(), this.entityInformation.getCollectionName());
        } else {
            T entity = this.findById(id).orElseThrow(() -> BizException.DATA_NOT_FOUND);
            this.delete(entity, force);
        }
    }

    @Override
    public void delete(T entity) {
        this.delete(entity, false);
    }

    @Override
    public void delete(T entity, boolean force) {
        Assert.notNull(entity, "The given entity must not be null!");
        if (!force && entity instanceof CommonEntity) {
            CommonEntity bean = (CommonEntity) entity;
            bean.setDeletedFlag(true);
            this.save(entity);
        } else {
            DeleteResult deleteResult = this.mongoOperations.remove(entity, this.entityInformation.getCollectionName());
            if (this.entityInformation.isVersioned() && deleteResult.wasAcknowledged() && deleteResult.getDeletedCount() == 0L) {
                throw new OptimisticLockingFailureException(String.format("The entity with id %s with version %s in %s cannot be deleted! Was it modified or deleted in the meantime?", this.entityInformation.getId(entity), this.entityInformation.getVersion(entity), this.entityInformation.getCollectionName()));
            }
        }
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        Assert.notNull(entities, "The given Iterable of entities not be null!");
        entities.forEach(this::delete);
    }

    @Override
    public DeleteResult deleteAll(@Nullable Query query) {
        DeleteResult deleteResult = this.mongoOperations.remove(query, this.entityInformation.getCollectionName());
        return deleteResult;
    }

    @Override
    public DeleteResult deleteAll(@Nullable Query query, String conllectionName) {
        DeleteResult deleteResult = this.mongoOperations.remove(query, conllectionName);
        return deleteResult;
    }

    @Override
    public Long getNextId() {
        return getNextId(entityInformation.getCollectionName());
    }

    @Override
    public Long getNextId(long initial) {
        return getNextId(entityInformation.getCollectionName(), initial);
    }

    @Override
    public Long getNextId(String key) {
        return getNextId(key, 1);
    }

    /**
     * 获取下一个整数键值
     *
     * @param key     通常是表名，也可以自定义
     * @param initial 起始值
     * @return
     */
    @Override
    public Long getNextId(String key, long initial) {
        Query query = new Query(Criteria.where("collectionName").is(key));
        Update update = new Update();
        update.inc("nextId");
        FindAndModifyOptions options = FindAndModifyOptions
                .options()
                .upsert(true)
                .returnNew(true);
        SysNextIdEntity incr = mongoOperations.findAndModify(query, update, options, SysNextIdEntity.class);
        Long next = incr.getNextId();
        if (next < initial) {
            Update initUpdate = new Update();
            initUpdate.set("nextId", initial);
            mongoOperations.findAndModify(query, initUpdate, options, SysNextIdEntity.class);
            return initial;
        } else {
            return next;
        }
    }

    @Override
    public String getCollectionName() {
        return this.entityInformation.getCollectionName();
    }
}
