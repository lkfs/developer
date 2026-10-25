package ${repositoryPackage};

import io.github.lkfs.developer.mongo.IBaseMongoRepository;
import ${entityPackage}.${entityClassName};
import org.springframework.stereotype.Repository;

/**
 * @author : Luis
 * @date : ${new Date().format("yyyy-MM-dd HH:mm")}
 */
@Repository
public interface ${repositoryClassName} extends IBaseMongoRepository<${entityClassName}, String> {

}


