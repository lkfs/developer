package com.venusoft.developer.web.qo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * @author : Luis
 * @date : 2021/4/8 10:47
 * dbSort = Sort.by(Sort.Direction.DESC, "createdDatetime");
 */
@Data
public class PageableQO extends BaseQO {
    private final static int MAXSIZE = 500;

    @Schema(description = "页码，从0开始")
    private int pageNumber = 0;
    @Schema(description = "每页行数")
    private int pageSize = 20;

    @JsonIgnore
    public Pageable getPageable() {
        if (pageSize > MAXSIZE) {
            pageSize = MAXSIZE;
        }
        return PageRequest.of(pageNumber, pageSize);
    }
}
