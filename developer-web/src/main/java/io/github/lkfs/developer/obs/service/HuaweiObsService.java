package io.github.lkfs.developer.obs.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.model.*;
import io.github.lkfs.developer.obs.vo.UploadHuaweiSignatureVO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 华为对象存储，客户端直传
 * https://support.huaweicloud.com/intl/zh-cn/bestpractice-obs/obs_05_1800.html
 * https://github.com/huaweicloud/huaweicloud-sdk-java-obs/blob/master/app/src/test/java/samples_java/PostObjectSample.java
 */
@Component
@Slf4j
@Data
@ConditionalOnClass(value = com.obs.services.ObsClient.class)
@ConditionalOnProperty(prefix = "huawei.obs", name = "accessKey")
public class HuaweiObsService implements IBaseStorageService {
    @Value("${huawei.obs.endPoint:}")
    private String endPoint;
    @Value("${huawei.obs.accessKey:}")
    private String accessKey;
    @Value("${huawei.obs.secretKey:}")
    private String secretKey;

    @Value("${huawei.obs.publicBucket:}")
    private String publicBucket;
    @Value("${huawei.obs.privateBucket:}")
    private String privateBucket;

    @Value("${huawei.obs.publicCdn:}")
    private String publicCdn;
    @Value("${huawei.obs.privateCdn:}")
    private String privateCdn;
    @Value("${huawei.obs.privateCdnAuthKey:}")
    private String privateCdnAuthKey;

    private ObsClient obsClient;
    private static AuthTypeEnum authType = AuthTypeEnum.OBS;

    private static final Pattern HTTP_PATTERN = Pattern.compile("^https?://");

    @PostConstruct
    public void init() {
        ObsConfiguration config = new ObsConfiguration();
        config.setEndPoint(endPoint);
        config.setAuthType(authType);
        obsClient = new ObsClient(accessKey, secretKey, config);
    }

    @PreDestroy
    public void destroy() {
        try {
            obsClient.close();
        } catch (IOException e) {
            log.error("destroy huawei obs error", e);
        }
    }


    @Override
    public String getPrivateUrl(String objectName) {
        return getPrivateUrl(objectName, false);
    }

    /**
     * @param objectName
     * @param origin     true:使用原始桶地址不使用cdn
     * @return
     */
    public String getPrivateUrl(String objectName, Boolean origin) {
        if (ObjectUtils.isEmpty(objectName)) {
            return objectName;
        }
        Matcher matcher = HTTP_PATTERN.matcher(objectName);
        if (matcher.find()) {
            return objectName;
        }

        long expireSeconds = 3600L;
        TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.GET, expireSeconds);
        request.setBucketName(getRawBucketName(PRIVATE));
        request.setObjectKey(objectName);
        TemporarySignatureResponse response = obsClient.createTemporarySignature(request);
        if (ObjectUtils.isEmpty(privateCdn) || origin) {
            return response.getSignedUrl();
        } else {
            //return response.getSignedUrl().replaceFirst("^http[s]?://.*?/", (privateCdn.endsWith("/") ? privateCdn : privateCdn + "/"));
            //华为云cdn配置  【域名管理】->【设置】->【缓存配置】-【缓存规则】-【忽略参数】
            //华为云cdn配置  【域名管理】->【设置】->【访问控制】-【URL鉴权配置】-【方式A】 鉴权参数 auth

            long timeMillis = System.currentTimeMillis();
            String hash = StrUtil.format("/{}-{}-0-0-{}", objectName, timeMillis, privateCdnAuthKey);
            return privateCdn + "/" + objectName + StrUtil.format("?auth={}-0-0-{}", timeMillis, DigestUtil.sha256Hex(hash));
        }
    }

    @Override
    public String getPublicUrl(String objectName) {
        return getPublicUrl(objectName, false);
    }

    /**
     * @param objectName
     * @param origin     true:使用原始桶地址不使用cdn
     * @return
     */
    public String getPublicUrl(String objectName, Boolean origin) {
        if (ObjectUtils.isEmpty(objectName)) {
            return objectName;
        }
        Matcher matcher = HTTP_PATTERN.matcher(objectName);
        if (matcher.find()) {
            return objectName;
        }

        if (ObjectUtils.isEmpty(publicCdn) || origin) {
            return "https://" + publicBucket + "." + endPoint + "/" + objectName;
        } else {
            return publicCdn + "/" + objectName;
        }
    }

    /**
     * 获取文件上传的签名
     *
     * @param rawDir
     * @return
     */
    @Override
    public UploadHuaweiSignatureVO getUploadToken(String bucketName, String rawDir, String objectName) {
        bucketName = getRawBucketName(bucketName);


        String key = generateKey(rawDir, objectName);

        PostSignatureRequest request = new PostSignatureRequest();
        request.setExpires(3600);
        // Bind policy to the exact bucket/object we expect, preventing client overrides
        request.setBucketName(bucketName);
        request.setObjectKey(key);

        Map<String, Object> formParams = new HashMap<>();

        String contentType = "text/plain";
        if (authType == AuthTypeEnum.OBS) {
            formParams.put("x-obs-acl", "public-read");
        } else {
            formParams.put("acl", "public-read");
        }
        formParams.put("content-type", contentType);
        formParams.put("key", key);

        request.setFormParams(formParams);

        PostSignatureResponse postSignature = obsClient.createPostSignature(request);


        Map<String, String> signature = new HashMap<>();
        signature.put("content-type", "text/plain");
        signature.put("x-obs-acl", "public-read");
        signature.put("key", key);
        signature.put("AccessKeyId", accessKey);
        signature.put("policy", postSignature.getPolicy());
        signature.put("signature", postSignature.getSignature());

        UploadHuaweiSignatureVO vo = new UploadHuaweiSignatureVO();

        vo.setSignature(signature);

        vo.setKey(key);
        vo.setUploadUrl("https://" + bucketName + "." + endPoint);

        return vo;
    }


}
