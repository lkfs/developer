package io.github.lkfs.developer.obs.vo;

import lombok.Data;

/**
 * @author : Luis
 * @date : 2021/9/26 10:40
 */
@Data
public class UploadS3PresignedUrlVO {
    /**
     * 预签名url
     */
    String presignedUrl;
    /**
     * 对象的存储路径
     */
    String key;

    /**
     * cdn地址
     */
    String cdnUrl;
}
