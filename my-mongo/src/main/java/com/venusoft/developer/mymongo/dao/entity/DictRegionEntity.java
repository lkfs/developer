package com.venusoft.developer.mymongo.dao.entity;

import com.venusoft.developer.mongo.entity.CommonEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Map;

/**
 * dict_region
 *
 * @author : Luis
 * @date : 2022-05-24 17:44
 */

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "dict_region")
public class DictRegionEntity extends CommonEntity<String, String> implements Serializable {
    /**
     * 区域id
     */
    @Id
    private String regionId;
    /**
     * 区域名称
     */
    private String regionName;
    /**
     * 区域级别
     */
    private Integer regionLevel;
    /**
     * 父级id
     */
    private String parentId;

}

