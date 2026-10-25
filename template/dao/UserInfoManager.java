package ${managerPackage};

import ${entityPackage}.${entityClassName};
import ${repositoryPackage}.${repositoryClassName};
import io.github.lkfs.developer.mongo.BaseMongoManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author : Luis
 * @date : 2023-10-10 15:57
 */
@Component
public class ${managerClassName} extends BaseMongoManager<${repositoryClassName}, ${entityClassName}, String, String> {
    @Resource
    private ${repositoryClassName} repository;

    @Override
    protected ${repositoryClassName} getRepository() {
        return repository;
    }

    //todo 可在这里增加业务方法，不限于 BaseMongoManager 提供的基础方法

}


