package com.venusoft.developer.obs.service;


import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.Protocol;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.venusoft.developer.common.exception.BizException;
import com.venusoft.developer.obs.vo.UploadAliSignatureVO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : Luis
 * @date : 2021/4/16 13:17
 * https://help.aliyun.com/document_detail/31926.html
 */
@ConditionalOnClass(value = com.aliyun.oss.OSS.class)
@ConditionalOnProperty(prefix = "ali.oss", name = "accessKeyId")
@Slf4j
@Data
@Service
public class AliOssService implements IBaseStorageService {
    @Value("${ali.oss.accessKeyId:}")
    private String accessKeyId;
    @Value("${ali.oss.accessKeySecret:}")
    private String accessKeySecret;
    @Value("${ali.oss.endpoint:}")
    private String endpoint;

    @Value("${ali.oss.publicBucket:}")
    private String publicBucket;
    @Value("${ali.oss.privateBucket:}")
    private String privateBucket;

    OSS ossClient;

    @PostConstruct
    public void init() {
        ClientBuilderConfiguration clientConfiguration = new ClientBuilderConfiguration();
        clientConfiguration.setProtocol(Protocol.HTTPS);

        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, clientConfiguration);
    }

    @PreDestroy
    public void destroy() {
        //业务完成一定要调用shutdown
        ossClient.shutdown();
    }

    private static final Pattern HTTP_PATTERN = Pattern.compile("^https?://");

    /**
     * 获取对象的加密url
     *
     * @param objectName
     * @return
     */
    @Override
    public String getPrivateUrl(String objectName) {
        if (ObjectUtils.isEmpty(objectName)) {
            return objectName;
        }
        Matcher matcher = HTTP_PATTERN.matcher(objectName);
        if (matcher.find()) {
            return objectName;
        }

        Date expiration = Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant());
        URL url = ossClient.generatePresignedUrl(privateBucket, objectName, expiration);

        return url.toString();
    }

    /**
     * 获取对象的公开url
     *
     * @param objectName
     * @return
     */
    @Override
    public String getPublicUrl(String objectName) {
        if (ObjectUtils.isEmpty(objectName)) {
            return objectName;
        }
        Matcher matcher = HTTP_PATTERN.matcher(objectName);
        if (matcher.find()) {
            return objectName;
        }

        return Protocol.HTTPS.toString() + "://" + publicBucket + "." + endpoint + "/" + objectName;
    }

    public void deletePublic(String objectName) {
        ossClient.deleteObject(getRawBucketName("public"), objectName);
    }

    public void deletePrivate(String objectName) {
        ossClient.deleteObject(getRawBucketName("private"), objectName);
    }


    /**
     * 获取文件上传的签名
     *
     * @param rawDir
     * @return
     */
    @Override
    public UploadAliSignatureVO getUploadToken(String bucketName, String rawDir, String objectName) {
        bucketName = getRawBucketName(bucketName);
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            //根据参数dir计算的policy，如果和前端uploadfile中参数key的相应字段不一致的话是会报错的
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, rawDir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes();
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            UploadAliSignatureVO vo = new UploadAliSignatureVO();
            vo.setAccessId(accessKeyId);
            vo.setSignature(postSignature);
            vo.setPolicy(encodedPolicy);

            String key = generateKey(rawDir, objectName);
            vo.setKey(key);

            vo.setHost(Protocol.HTTPS.toString() + "://" + bucketName + "." + endpoint);

            return vo;

        } catch (Exception e) {
            log.error("获取ali对象存储签名异常,{}", e);
            throw new BizException(4577, "");
        }
    }


}

