package com.venusoft.developer.mymongo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * @author : Luis
 * @date : 2024-03-12 14:02
 */
@Data
public class DictRegionDetailVO extends DictRegionListVO {
    private static final long serialVersionUID = -5291385022313007160L;

    @Schema(name = "创建用户")
    private String createdAt;

}
