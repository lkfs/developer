package io.github.lkfs.developer.mymongo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


/**
 * @author : Luis
 * @date : 2024-03-12 14:02
 */
@Data
public class DictRegionListVO implements Serializable {
    private static final long serialVersionUID = -6277852866759353602L;


    @Schema(name = "区域id")
    private String regionId;

    @Schema(name = "区域名称")
    private String regionName;

    @Schema(name = "区域级别")
    private Integer regionLevel;

    @Schema(name = "父级id")
    private String parentId;


}
