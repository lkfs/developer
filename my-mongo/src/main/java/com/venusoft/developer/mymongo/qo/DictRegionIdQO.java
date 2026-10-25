package com.venusoft.developer.mymongo.qo;

import com.venusoft.developer.web.qo.BaseQO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 参数支持动态查询后缀：Is、Eq、Neq、StartWith、EndWith、Like、Gt、Gte、Lt、Lte、Null、NotNull、In、NotIn
 *
 * @author : Luis
 * @date : 2024-03-12 14:02
 */
@Data
public class DictRegionIdQO extends BaseQO {

    @Schema(name = "区域id")
    private String regionId;


}
