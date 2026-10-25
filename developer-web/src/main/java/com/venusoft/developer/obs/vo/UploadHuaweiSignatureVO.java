package com.venusoft.developer.obs.vo;

import lombok.Data;

import java.util.Map;

/**
 * @author : Luis
 * @date : 2021/9/26 10:40
 */
@Data
public class UploadHuaweiSignatureVO {
    /**
     * alioss服务端签名后直传，https://help.aliyun.com/document_detail/31926.html?spm=5176.doc31923.2.2.eVTahh
     */
    private Map<String, String> signature;

    /**
     * 对象的存储路径
     */
    private String key;
    /**
     * 上传地址
     */
    private String uploadUrl;
}
