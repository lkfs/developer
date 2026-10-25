package io.github.lkfs.developer.obs.service;

import cn.hutool.core.date.DateUtil;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import io.github.lkfs.developer.common.exception.BizException;
import io.github.lkfs.developer.obs.vo.UploadS3PresignedUrlVO;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Date;

/**
 * aws s3 文件工具类
 * S3访问秘钥
 * https://blog.csdn.net/qq_36622490/article/details/120214785
 * 存储桶创建
 * https://piaohua.github.io/post/learn/20240830-aws-amazon-s3-public-read/
 * 前端直传
 * https://aws.amazon.com/cn/blogs/china/s3-multipul-upload-practice/
 * https://docs.aws.amazon.com/AmazonS3/latest/userguide/upload-objects.html
 * https://docs.aws.amazon.com/zh_cn/AmazonS3/latest/userguide/PresignedUrlUploadObject.html
 * https://blog.csdn.net/youbl/article/details/128240960
 *
 * @author LHL
 */
@ConditionalOnClass(value = com.amazonaws.services.s3.AmazonS3.class)
@ConditionalOnProperty(prefix = "aws.s3", name = "accessKey")
@Slf4j
@Component
@Data
public class AmazonS3Service implements IBaseStorageService {

    private AmazonS3 s3Client;

    @Value("${aws.s3.accessKey}")
    private String accessKey;

    @Value("${aws.s3.secretKey}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.uploadUrl}")
    private String uploadUrl;

    @Value("${aws.s3.cdnUrl}")
    private String cdnUrl;

    @Value("${aws.s3.publicBucket}")
    private String publicBucket;

    @Value("${aws.s3.privateBucket}")
    private String privateBucket;

    @PostConstruct
    public void init() {
        ClientConfiguration config = new ClientConfiguration();
        config.setProtocol(Protocol.HTTPS);
        config.disableSocketProxy();
        // 初始化S3上传操作类
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        this.s3Client = AmazonS3ClientBuilder
                .standard()
                .withClientConfiguration(config)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .enablePathStyleAccess()
                .build();
    }

    /**
     * 生成一个预签名的url，给前端js上传
     * 注: Method aws官方要求必须是PUT
     *
     * @param rawDir     上传到s3的文件相对路径
     * @param objectName 上传到s3的文件相对路径
     * @return 签名后的url
     */
    @Override
    public UploadS3PresignedUrlVO getUploadToken(String bucketName, String rawDir, String objectName) {
        bucketName = getRawBucketName(bucketName);
        try {
            String key = generateKey(rawDir, objectName);
            Date expiration = DateUtil.offsetHour(new Date(), 1);
            GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, key)
                    .withExpiration(expiration)
                    .withMethod(HttpMethod.PUT);
            URL url = s3Client.generatePresignedUrl(urlRequest);

            UploadS3PresignedUrlVO vo = new UploadS3PresignedUrlVO();
            vo.setPresignedUrl(url.toString());

            vo.setKey(key);

            vo.setCdnUrl(cdnUrl);

            return vo;

        } catch (Exception e) {
            log.error("获取s3对象存储预签名url异常", e);
            throw new BizException(4577, "");
        }
    }

    @Override
    public String getPrivateUrl(String objectName) {
        return null;
    }

    @Override
    public String getPublicUrl(String objectName) {
        return null;
    }


    public void deletePublic(String objectName) {
        s3Client.deleteObject(getRawBucketName("public"), objectName);
    }

    public void deletePrivate(String objectName) {
        s3Client.deleteObject(getRawBucketName("private"), objectName);
    }

}
