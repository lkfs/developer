package io.github.lkfs.developer.obs.service;

import cn.hutool.core.io.file.FileNameUtil;
import io.github.lkfs.developer.web.utils.IdUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author : Luis
 * @date : 2021/9/29 14:17
 */
public interface IBaseStorageService {

    String URL_SEPARATOR = "/";
    String PUBLIC = "public";
    String PRIVATE = "private";

    default String wrapperDir(String dir) {
        String yearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        dir = dir.trim();
        if (dir.endsWith(URL_SEPARATOR)) {
            dir = dir + yearMonth + URL_SEPARATOR;
        } else {
            dir = dir + URL_SEPARATOR + yearMonth + URL_SEPARATOR;
        }
        return dir;
    }

    /**
     * 获取对象的加密url
     *
     * @param objectName
     * @return
     */
    String getPrivateUrl(String objectName);

    /**
     * 获取对象的公开url
     *
     * @param objectName
     * @return
     */
    String getPublicUrl(String objectName);

    /**
     * 获取文件上传的签名
     *
     * @param bucketName
     * @param dir
     * @return
     */
    <T> T getUploadToken(String bucketName, String dir, String objectName);


    String getPublicBucket();

    String getPrivateBucket();

    default String getRawBucketName(String bucketName) {
        if (PUBLIC.equalsIgnoreCase(bucketName)) {
            return getPublicBucket();
        } else if (PRIVATE.equalsIgnoreCase(bucketName)) {
            return getPrivateBucket();
        } else {
            return bucketName;
        }
    }

    default String generateKey(String dir, String objectName) {
        String key = wrapperDir(dir) + IdUtils.nextSnowflakeId() + "." + FileNameUtil.extName(objectName);
        return key;
    }
}
