package com.venusoft.developer.web.controller;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import com.venusoft.developer.common.exception.BizException;
import com.venusoft.developer.common.result.CommonResult;
import com.venusoft.developer.common.result.ICommonResult;
import com.venusoft.developer.obs.qo.UploadTokenQO;
import com.venusoft.developer.obs.service.*;
import com.venusoft.developer.obs.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * com.alibaba.fastjson.JSONObject
 *
 * @author : Luis
 * @date : 2021/4/1 13:34
 */
@Slf4j
@RestController
@Validated
@Tag(name = "上传相关", description = "支持本地上传，支持ali、腾讯、七牛、S3、华为等前端直传")
public class UploadController {

    @Value("${developer.upload.path:/home/app/services/dah-test/uploadFiles}")
    private String uploadPath;
    @Value("${developer.upload.host:https://baidu.com}")
    private String uploadHost;

    @Autowired(required = false)
    private AliOssService aliStorageService;
    @Autowired(required = false)
    private TencentCosService tencentStorageService;
    @Autowired(required = false)
    private QiniuKodoService qiniuStorageService;
    @Autowired(required = false)
    private HuaweiObsService huaweiObsService;
    @Autowired(required = false)
    private AmazonS3Service s3Service;

    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
    private static final DateTimeFormatter DAY_HOUR_FORMATTER = DateTimeFormatter.ofPattern("ddHH");
    private static final Pattern EXTENSION_PATTERN = Pattern.compile("[A-Za-z0-9]{1,10}");

    @Operation(summary = "本地上传")
    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ICommonResult upload(
            @RequestPart(name = "file")
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "文件",
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "object"),
                            schemaProperties = {
                                    @SchemaProperty(
                                            name = "file",
                                            schema = @Schema(type = "file", description = "文件", format = "binary")
                                    )
                            }
                    )
            ) MultipartFile file,
            @RequestParam(required = false) @Parameter(description = "文件存放的文件夹") String dir,
            HttpServletRequest request) throws IOException {

        LocalDateTime now = LocalDateTime.now();

        Path basePath = Paths.get(uploadPath).toAbsolutePath().normalize();
        Path targetPath;
        try {
            targetPath = basePath.resolve(ObjectUtils.isEmpty(dir) ? "" : dir).normalize();
        } catch (InvalidPathException e) {
            throw BizException.PARAMS_ERROR.withMessage("Illegal upload directory");
        }
        // 防止通过 dir 参数穿越到上传根目录之外
        if (!targetPath.startsWith(basePath)) {
            throw BizException.PARAMS_ERROR.withMessage("Illegal upload directory");
        }
        File path = targetPath.toFile();

        String yearMonth = YEAR_MONTH_FORMATTER.format(now);
        path = new File(path, yearMonth);
        if (!path.exists()) {
            path.mkdirs();
        }

        //新文件名
        String extName = FileNameUtil.extName(file.getOriginalFilename());
        if (!ObjectUtils.isEmpty(extName) && !EXTENSION_PATTERN.matcher(extName).matches()) {
            throw BizException.PARAMS_ERROR.withMessage("Illegal file extension");
        }
        String newFileName = DAY_HOUR_FORMATTER.format(now)
                + "-" + IdUtil.getSnowflakeNextIdStr()
                + (ObjectUtils.isEmpty(extName) ? "" : "." + extName);
        File newFile = new File(path, newFileName);

        //转存
        file.transferTo(newFile);

        String pathStr = (ObjectUtils.isEmpty(dir) ? "" : dir + "/") + yearMonth;
        Map<String, String> saveFile = new HashMap<>();
        saveFile.put("path", yearMonth);
        saveFile.put("name", newFileName);
        saveFile.put("url", uploadHost + "/" + pathStr + "/" + newFileName);

        return new CommonResult<>(saveFile);
    }


    @Operation(summary = "获取阿里云上传签名", description = "前端直传阿里云时使用")
    @PostMapping("/file/upload/getAliToken")
    public ICommonResult getAliUploadToken(@RequestBody @Valid UploadTokenQO qo) {
        //阿里云上传签名
        UploadAliSignatureVO signature = aliStorageService.getUploadToken(qo.getBucketName(), qo.getDir(), qo.getObjectName());
        return new CommonResult<>(signature);
    }

    @Operation(summary = "获取七牛上传签名", description = "前端直传七牛云时使用")
    @PostMapping("/file/upload/getQiniuToken")
    public ICommonResult getQiniuUploadToken(@RequestBody @Valid UploadTokenQO qo) {
        UploadQiniuTokenVO vo = qiniuStorageService.getUploadToken(qo.getBucketName(), qo.getDir(), qo.getObjectName());
        return new CommonResult<>(vo);
    }

    @Operation(summary = "获取腾讯上传签名", description = "前端直传腾讯云时使用")
    @PostMapping("/file/upload/getTencentToken")
    public ICommonResult getTencentUploadToken(@RequestBody @Valid UploadTokenQO qo) {
        UploadTencentTokenVO vo = tencentStorageService.getUploadToken(qo.getBucketName(), qo.getDir(), qo.getObjectName());
        return new CommonResult<>(vo);
    }

    @Operation(summary = "获取华为云上传签名", description = "前端直传华为云时使用")
    @PostMapping("/file/upload/getHuaweiToken")
    public ICommonResult getHuaweiUploadToken(@RequestBody @Valid UploadTokenQO qo) {
        UploadHuaweiSignatureVO vo = huaweiObsService.getUploadToken(qo.getBucketName(), qo.getDir(), qo.getObjectName());
        return new CommonResult<>(vo);
    }

    @Operation(summary = "获取亚马逊S3上传签名", description = "前端直传亚马逊云时使用")
    @PostMapping("/file/upload/getS3Token")
    public ICommonResult getS3UploadToken(@RequestBody @Valid UploadTokenQO qo) {
        UploadS3PresignedUrlVO vo = s3Service.getUploadToken(qo.getBucketName(), qo.getDir(), qo.getObjectName());
        return new CommonResult<>(vo);
    }

}
