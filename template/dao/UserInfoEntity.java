package ${entityPackage};

import io.github.lkfs.developer.common.entity.CommonEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * ${collection}
 *
 * @author : Luis
 * @date : ${new Date().format("yyyy-MM-dd HH:mm")}
 */

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "${collection}")
public class ${entityClassName} extends CommonEntity<String, String> implements Serializable {
    @Id
    /**
     * id
     */
    private String bannerId;

    /**
     * name
     */
    private String bannerName;

}

