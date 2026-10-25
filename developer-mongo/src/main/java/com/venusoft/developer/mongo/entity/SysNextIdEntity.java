package com.venusoft.developer.mongo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author lkfs
 */
@Data
@Document(collection = "sys_next_id")
public class SysNextIdEntity {
    @Id
    private String id;

    private String collectionName;

    private Long nextId = 100000L;
}
