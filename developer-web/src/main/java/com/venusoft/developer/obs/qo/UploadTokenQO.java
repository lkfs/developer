package com.venusoft.developer.obs.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author : Luis
 * @date : 2021/9/26 10:36
 */
@Data
public class UploadTokenQO {

    @Schema(description = "存储桶名:public或private")
    @NotBlank
    private String bucketName;

    @Schema(description = "文件在存储桶中的目录")
    @NotBlank
    private String dir;

    @Schema(description = "上传的文件名,根据文件名的后缀判断文件类型")
    @NotBlank
    private String objectName;
}
