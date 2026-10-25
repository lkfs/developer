package com.venusoft.developer.obs.service;

import com.qiniu.util.Auth;
import com.venusoft.developer.obs.vo.UploadQiniuTokenVO;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


@ConditionalOnClass(value = com.qiniu.util.Auth.class)
@ConditionalOnProperty(prefix = "qiniu.kodo", name = "accessKey")
@Service
@Data
@Slf4j
public class QiniuKodoService implements IBaseStorageService {
    @Value("${qiniu.kodo.publicBucket:public.qiniu}")
    private String publicBucket;
    @Value("${qiniu.kodo.privateBucket:private.qiniu}")
    private String privateBucket;
    @Value("${qiniu.kodo.accessKey:}")
    private String accessKey;
    @Value("${qiniu.kodo.secretKey:}")
    private String secretKey;
    @Value("${qiniu.kodo.host:}")
    private String host;

    @Getter
    private Auth auth;

    @PostConstruct
    public void init() {
        auth = Auth.create(accessKey, secretKey);
    }


    @Override
    public String getPublicUrl(String objectName) {
        if (ObjectUtils.isEmpty(objectName)) {
            return objectName;
        }
        if (objectName.startsWith("http") || objectName.startsWith("https")) {
            return objectName;
        } else {
            return "https://" + publicBucket + "." + host + "/" + objectName;
        }
    }

    @Override
    public String getPrivateUrl(String objectName) {
        if (ObjectUtils.isEmpty(objectName)) {
            return objectName;
        }
        String baseUrl = null;
        if (objectName.startsWith("http") || objectName.startsWith("https")) {
            baseUrl = objectName;
        } else {
            baseUrl = "https://" + privateBucket + "." + host + "/" + objectName;
        }
        Auth auth = Auth.create(accessKey, secretKey);
        return auth.privateDownloadUrl(baseUrl, 3600);
    }

    /**
     * 七牛直传token，https://developer.qiniu.com/kodo/1312/upload
     *
     * @param bucketName
     * @param dir
     * @return
     */
    @Override
    public UploadQiniuTokenVO getUploadToken(String bucketName, String dir, String objectName) {
        bucketName = getRawBucketName(bucketName);
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucketName);

        UploadQiniuTokenVO vo = new UploadQiniuTokenVO();
        vo.setToken(upToken);

        vo.setHost("https://" + bucketName + "." + host + "/");
        vo.setKey(this.generateKey(dir, objectName));
        return vo;
    }

}
