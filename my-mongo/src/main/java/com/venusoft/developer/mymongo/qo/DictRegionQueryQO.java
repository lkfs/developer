package com.venusoft.developer.mymongo.qo;

import com.venusoft.developer.web.qo.PageableQO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;


/**
 * 参数支持动态查询后缀：Is、Eq、Neq、StartWith、EndWith、Like、Gt、Gte、Lt、Lte、Null、NotNull、In、NotIn
 *
 * @author : Luis
 * @date : 2024-03-12 14:02
 */
@Data
public class DictRegionQueryQO extends PageableQO {

    @Schema(name = "区域id")
    private String regionId_Like;

    @Schema(name = "区域名称")
    private String regionName_Like;

    @Schema(name = "区域级别")
    private Integer regionLevel;

    @Min(value = 5, message = "5002")
    @Schema(name = "排序")
    private Integer sort;


}
