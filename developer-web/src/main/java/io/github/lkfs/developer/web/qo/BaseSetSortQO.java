package io.github.lkfs.developer.web.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 通用设置排序QO对象
 *
 * @author : lkfs
 * @date : 2021-04-29 10:37
 */
@Data
public class BaseSetSortQO<IdType> {

    @Deprecated
    @Schema(description = "单个id")
    private IdType id;

    @Deprecated
    @Schema(description = "单个排序值")
    private Integer sort;

    @Schema(description = "id顺序列表")
    private List<IdType> ids;

    @Schema(description = "页码，从1开始")
    private int current = 1;

    @Schema(description = "每页行数")
    private int size = 20;

    @Schema(description = "分页后的开始索引", hidden = true)
    public int getStart() {
        int start = (current - 1) * size;
        if (start < 0) {
            start = 0;
        }
        return start;
    }
}
