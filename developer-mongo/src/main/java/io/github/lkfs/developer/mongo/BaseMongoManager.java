package io.github.lkfs.developer.mongo;

import com.mongodb.client.result.DeleteResult;
import io.github.lkfs.developer.common.exception.BizException;
import io.github.lkfs.developer.mongo.entity.CommonEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * mongo基础管理类
 *
 * @param <R>      Repository仓库
 * @param <T>      Entity 表
 * @param <STATUS> 表中状态字段的类型
 * @param <ID>     表中id字段的类型
 * @author lkfs
 */
public abstract class BaseMongoManager<R extends IBaseMongoRepository, T, STATUS, ID> {

    protected abstract R getRepository();


    public void delete(T tab) {
        this.getRepository().delete(tab, true);
    }

    public void delete(T tab, boolean force) {
        this.getRepository().delete(tab, force);
    }

    public void deleteById(ID id) {
        this.getRepository().deleteById(id, true);
    }

    public void deleteById(ID id, boolean force) {
        this.getRepository().deleteById(id, force);
    }

    public DeleteResult deleteAll(Query query) {
        return this.getRepository().deleteAll(query);
    }

    public DeleteResult deleteAll(Query query, String collectionName) {
        return this.getRepository().deleteAll(query, collectionName);
    }

    public T get(ID id) {
        Optional<T> beanOptional = this.getRepository().findById(id);
        if (!beanOptional.isPresent()) {
            throw BizException.DATA_NOT_FOUND;
        }

        return beanOptional.get();
    }


    public void setSort(ID id, Integer sort) {
        Optional<T> beanOptional = this.getRepository().findById(id);
        if (!beanOptional.isPresent()) {
            throw BizException.DATA_NOT_FOUND;
        }

        T bean = beanOptional.get();

        //反射设置排序值
        try {
            Method setSortMethod = bean.getClass().getMethod("setSort", Integer.class);
            setSortMethod.invoke(bean, sort);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        this.getRepository().save(bean);

    }

    public void setStatus(ID id, STATUS status) {
        Optional<T> beanOptional = this.getRepository().findById(id);
        if (!beanOptional.isPresent()) {
            throw BizException.DATA_NOT_FOUND;
        }

        T bean = beanOptional.get();
        if (bean instanceof CommonEntity) {
            CommonEntity<STATUS, ID> b = (CommonEntity<STATUS, ID>) bean;
            b.setStatus(status);
            this.getRepository().save(b);
        } else {
            throw BizException.DATA_ACCESS_ERROR;
        }

    }


    public Optional<T> findById(ID id) {
        return this.getRepository().findById(id);
    }

    public List<T> findById(Iterable<ID> ids) {
        return this.getRepository().findAllById(ids);
    }


    public Optional<T> findOne(Query query) {
        Pageable pageable = PageRequest.of(0, query.getLimit() == 0 ? 2 : query.getLimit());
        Page<T> page = this.getRepository().findAll(query, pageable);

        if (page.getContent().size() <= 0) {
            return Optional.ofNullable(null);
        } else if (page.getContent().size() > 1) {
            throw BizException.DATA_ACCESS_ERROR.withMessage("findOne, data is not unique");
        }

        return Optional.ofNullable(page.getContent().get(0));

    }

    public Optional<T> findFirst(Query query) {
        Pageable pageable = PageRequest.of(0, 1);
        Page<T> page = this.getRepository().findAll(query, pageable);

        if (page.getContent().size() <= 0) {
            return Optional.ofNullable(null);
        }

        return Optional.ofNullable(page.getContent().get(0));

    }

    public Page<T> findAll(Query query, Pageable pageable) {

        Page<T> page = this.getRepository().findAll(query, pageable);
        return page;

    }

    public List<T> findAll(Query query) {

        List<T> list = this.getRepository().findAll(query);
        return list;

    }

    public void save(T tab) {
        this.getRepository().save(tab);
    }

    public void saveAll(Iterable<T> entities) {
        this.getRepository().saveAll(entities);
    }

    public long count() {
        return this.getRepository().count();
    }

    public long count(Query query) {
        return this.getRepository().count(query);
    }

    public Long getNextId() {
        return this.getRepository().getNextId();
    }

    public Long getNextId(long initial) {
        return this.getRepository().getNextId(initial);
    }

    public Long getNextId(String key) {
        return this.getRepository().getNextId(key);
    }

    public Long getNextId(String key, long initial) {
        return this.getRepository().getNextId(key, initial);
    }

}
