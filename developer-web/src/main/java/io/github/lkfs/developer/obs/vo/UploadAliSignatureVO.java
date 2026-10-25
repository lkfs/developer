package io.github.lkfs.developer.obs.vo;

import lombok.Data;

/**
 * @author : Luis
 * @date : 2021/9/26 10:40
 */
@Data
public class UploadAliSignatureVO {
    /**
     * alioss服务端签名后直传，https://help.aliyun.com/document_detail/31926.html?spm=5176.doc31923.2.2.eVTahh
     */
    String accessId;
    String signature;
    String policy;
    /**
     * 对象的存储路径
     */
    String key;
    /**
     * 上传地址
     */
    String host;
}
