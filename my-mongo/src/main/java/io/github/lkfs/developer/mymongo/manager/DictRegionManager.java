
package io.github.lkfs.developer.mymongo.manager;

import io.github.lkfs.developer.mymongo.dao.entity.DictRegionEntity;
import io.github.lkfs.developer.mymongo.dao.repository.DictRegionRepository;
import io.github.lkfs.developer.mongo.BaseMongoManager;
import org.springframework.stereotype.Component;
import jakarta.annotation.Resource;

/**
 * @author : Luis
 * @date : 2022-05-24 17:44
 */
@Component
public class DictRegionManager extends BaseMongoManager<DictRegionRepository, DictRegionEntity, String, String> {
    @Resource
    private DictRegionRepository repository;

    @Override
    protected DictRegionRepository getRepository() {
        return repository;
    }

    //todo 可在这里增加业务方法，不限于 BaseMongoManager 提供的基础方法

}


