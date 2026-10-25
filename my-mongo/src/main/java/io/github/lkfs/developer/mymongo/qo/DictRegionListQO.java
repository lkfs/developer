package io.github.lkfs.developer.mymongo.qo;

import io.github.lkfs.developer.web.qo.BaseQO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 参数支持动态查询后缀：Is、Eq、Neq、StartWith、EndWith、Like、Gt、Gte、Lt、Lte、Null、NotNull、In、NotIn
 *
 * @author : Luis
 * @date : 2024-03-12 14:02
 */
@Data
public class DictRegionListQO extends BaseQO {

    @Schema(name = "区域id")
    private String regionId;

    @Schema(name = "区域名称")
    private String regionName;

    @Schema(name = "区域级别")
    private Integer regionLevel;

    @Schema(name = "父级id")
    private String parentId;


}
