package io.github.lkfs.developer.obs.vo;

import lombok.Data;

/**
 * @author : Luis
 * @date : 2021/9/26 10:40
 */
@Data
public class UploadTencentTokenVO {
    /**
     * 腾讯服务端签名后直传，https://cloud.tencent.com/document/product/436/14048
     */
    String tmpSecretId;
    String tmpSecretKey;
    String sessionToken;
    /**
     * 对象的存储路径
     */
    String dir;
    /**
     * 上传地址
     */
    String uploadUrl;
}
