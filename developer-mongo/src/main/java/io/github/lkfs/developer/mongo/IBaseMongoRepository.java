package io.github.lkfs.developer.mongo;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.lang.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author : Luis
 * @date : 2021/5/10 16:56
 */
public interface IBaseMongoRepository<T, ID> extends Repository<T, ID> {

    List<T> findAll(Query query);

    Page<T> findAll(Query query, Pageable pageable);

    List<T> findAllById(Iterable<ID> ids);

    <S extends T> S save(S entity);

    <S extends T> S save(S entity, String collectionName);

    <S extends T> List<S> saveAll(Iterable<S> entities);

    Optional<T> findById(ID id);

    boolean existsById(ID id);

    long count();

    long count(Query query);

    void delete(T entity);

    void delete(T entity, boolean force);

    void deleteById(ID id);

    void deleteAll(Iterable<? extends T> entities);

    DeleteResult deleteAll(@Nullable Query query);

    DeleteResult deleteAll(@Nullable Query query, String conllectionName);

    void deleteById(ID id, boolean force);

    Long getNextId();

    Long getNextId(long initial);

    Long getNextId(String key);

    Long getNextId(String key, long initial);

    String getCollectionName();
}
