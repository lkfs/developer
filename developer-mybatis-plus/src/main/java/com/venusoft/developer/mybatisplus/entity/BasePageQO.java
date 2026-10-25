package com.venusoft.developer.mybatisplus.entity;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author : Luis
 * @date : 2025/6/10 15:24
 */
@Data
public class BasePageQO<T> {
    private final static int MAXSIZE = 500;

    @Schema(description = "页码，从1开始")
    private int current = 1;
    @Schema(description = "每页行数")
    private int size = 20;

    @Schema(description = "MyBatisPlus Page", hidden = true)
    public IPage<T> getPage() {
        if (size > MAXSIZE) {
            size = MAXSIZE;
        }
        return new Page<>(current, size);
    }
}
