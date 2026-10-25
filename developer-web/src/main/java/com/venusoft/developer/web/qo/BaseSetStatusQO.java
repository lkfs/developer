package com.venusoft.developer.web.qo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 通用设置状态QO对象
 *
 * @author : QRQY
 * @date : 2021-04-29 10:37
 */
@Data
public class BaseSetStatusQO<IdType, StatusType> {

    /**
     * entityId
     */
    @NotNull(message = "id can't be null")
    private IdType id;
    /**
     * 状态
     */
    @NotNull(message = "status can't be null")
    private StatusType status;
}
