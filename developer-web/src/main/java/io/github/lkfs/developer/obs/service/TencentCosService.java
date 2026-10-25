package io.github.lkfs.developer.obs.service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import com.tencent.cloud.CosStsClient;
import io.github.lkfs.developer.common.exception.BizException;
import io.github.lkfs.developer.obs.vo.UploadTencentTokenVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TreeMap;

/**
 * @author Administrator
 */
@ConditionalOnClass(value = com.qcloud.cos.COSClient.class)
@ConditionalOnProperty(prefix = "tencent.cos", name = "secretId")
@Slf4j
@Data
@Service
public class TencentCosService implements IBaseStorageService {
    private static String UPLOAD_URL = "https://%s.cos.%s.myqcloud.com";

    @Value("${tencent.cos.secretId:}")
    private String secretId;
    @Value("${tencent.cos.secretKey:}")
    private String secretKey;
    @Value("${tencent.cos.region:}")
    private String region;
    @Value("${tencent.cos.publicBucket:}")
    private String publicBucket;
    @Value("${tencent.cos.privateBucket:}")
    private String privateBucket;

    @Override
    public String getPrivateUrl(String objectName) {
        if (ObjectUtils.isEmpty(objectName)) {
            return objectName;
        }

        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 2 设置 bucket 的地域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region regionRegion = new Region(region);
        ClientConfig clientConfig = new ClientConfig(regionRegion);
        // 这里建议设置使用 https 协议
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 3 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);

        Date expiration = Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant());
        URL url = cosClient.generatePresignedUrl(privateBucket, objectName, expiration);
        return url.toString();
    }


    @Override
    public String getPublicUrl(String objectName) {
        if (ObjectUtils.isEmpty(objectName)) {
            return objectName;
        }
        return String.format(UPLOAD_URL, publicBucket, region) + "/" + objectName;
    }

    /**
     * 获取腾讯直传签名，https://cloud.tencent.com/document/product/436/9067
     *
     * @return
     */
    @Override
    public UploadTencentTokenVO getUploadToken(String bucketName, String dir, String objectName) {
        bucketName = getRawBucketName(bucketName);
        TreeMap<String, Object> config = new TreeMap<String, Object>();

        try {
            // 云 api 密钥 SecretId
            config.put("secretId", secretId);
            // 云 api 密钥 SecretKey
            config.put("secretKey", secretKey);

            // 临时密钥有效时长，单位是秒
            config.put("durationSeconds", 1800);

            // 换成你的 bucket
            config.put("bucket", bucketName);
            // 换成 bucket 所在地区
            config.put("region", region);

            // 可以通过 allowPrefixes 指定前缀数组, 例子： a.jpg 或者 a/* 或者 * (使用通配符*存在重大安全风险, 请谨慎评估使用)
            config.put("allowPrefixes", new String[]{
                    "*"
            });

            // 密钥的权限列表。简单上传和分片需要以下的权限，其他权限列表请看 https://cloud.tencent.com/document/product/436/31923
            String[] allowActions = new String[]{
                    // 简单上传
                    "name/cos:PutObject",
                    "name/cos:PostObject",
                    // 分片上传
                    "name/cos:InitiateMultipartUpload",
                    "name/cos:ListMultipartUploads",
                    "name/cos:ListParts",
                    "name/cos:UploadPart",
                    "name/cos:CompleteMultipartUpload"
            };
            config.put("allowActions", allowActions);

            JSONObject response = CosStsClient.getCredential(config);
            if (response.has("credentials")) {
                JSONObject secret = (JSONObject) response.get("credentials");
                UploadTencentTokenVO vo = new UploadTencentTokenVO();
                vo.setTmpSecretId(secret.getString("tmpSecretId"));
                vo.setTmpSecretKey(secret.getString("tmpSecretKey"));
                vo.setSessionToken(secret.getString("sessionToken"));

                vo.setDir(wrapperDir(dir));

                vo.setUploadUrl(String.format(UPLOAD_URL, bucketName, region));
                return vo;
            } else {
                throw BizException.INTERFACE_ERROR;
            }
        } catch (Exception e) {
            log.error("获取腾讯对象存储签名异常", e);
            throw BizException.INTERFACE_ERROR.withMessage("获取腾讯签名异常, no valid secret !");
        }
    }


}
