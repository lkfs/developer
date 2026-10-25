package io.github.lkfs.developer.obs.vo;

import lombok.Data;

/**
 * @author : Luis
 * @date : 2021/9/26 10:40
 */
@Data
public class UploadQiniuTokenVO {
    /**
     * qiniu简单上传的凭证 https://developer.qiniu.com/kodo/1239/java#simple-uptoken
     */
    String token;

    /**
     * 对象的存储路径
     */
    String key;
    /**
     * 上传地址
     */
    String host;
}
