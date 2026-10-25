package com.venusoft.developer.common.security;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Security;

/**
 * 通信加密工具类
 * android refer: https://blog.csdn.net/BADAO_LIUMANG_QIZHI/article/details/111540501
 */
@Data
@Slf4j
@Component
public class AesUtils {
    @Value("${crypto.aesSecretKey:}")
    private String aesSecretKey;
    @Value("${crypto.aesIv:}")
    private String aesIv;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public String encrypt(String plainText) {
        if (ObjectUtils.isEmpty(plainText)) {
            return null;
        }
        try {
            return this.encrypt(plainText, aesSecretKey, aesIv, AlgorithmEnum.CBC_PKCS7);
        } catch (Exception e) {
            log.error("AesUtils encrypt", e);
            throw new IllegalStateException("AES 加密失败", e);
        }
    }

    public String decrypt(String encryptText) {
        if (ObjectUtils.isEmpty(encryptText)) {
            return null;
        }
        try {
            return this.decrypt(encryptText, aesSecretKey, aesIv, AlgorithmEnum.CBC_PKCS7);
        } catch (Exception e) {
            log.error("AesUtils decrypt error", e);
            throw new IllegalStateException("AES 解密失败", e);
        }
    }

    /**
     * 先AES加密，再将进行Base64转码，输出密钥字符串
     *
     * @param wait      待加密字符串
     * @param key       密码，16、24、32位
     * @param iv        向量，必须16位
     * @param algorithm 算法
     * @return
     * @throws Exception
     */
    public String encrypt(@NotNull String wait, @NotNull String key, String iv, @NotNull AesUtils.AlgorithmEnum algorithm) throws Exception {
        return encrypt(wait, key, iv, algorithm, StandardCharsets.UTF_8);
    }

    /**
     * 先AES加密，再将进行Base64转码，输出密钥字符串
     *
     * @param wait      待加密字符串
     * @param key       密码，16、24、32位
     * @param iv        向量，必须16位
     * @param algorithm 算法
     * @param charset   字符集
     * @return
     * @throws Exception
     */
    public String encrypt(@NotNull String wait, @NotNull String key, String iv, @NotNull AesUtils.AlgorithmEnum algorithm, @NotNull Charset charset) throws Exception {
        // 按照 mode 生成实例
        Cipher cipher = cipherInstance(Cipher.ENCRYPT_MODE, key, iv, algorithm, charset);
        // 字节码加密
        byte[] b = cipher.doFinal(wait.getBytes(charset));
        // 字节码转字符串
        return Base64Encoder.encode(b);
    }

    /**
     * 先将密钥base64解码，再进行AES解密，输出解密字符串
     *
     * @param wait      待解密密钥
     * @param key       密码，16、24、32位
     * @param iv        向量，必须16位
     * @param algorithm 算法
     * @return
     * @throws Exception
     */
    public String decrypt(@NotNull String wait, @NotNull String key, String iv, @NotNull AesUtils.AlgorithmEnum algorithm) throws Exception {
        return decrypt(wait, key, iv, algorithm, StandardCharsets.UTF_8);
    }

    /**
     * 先将密钥base64解码，再进行AES解密，输出字符串
     *
     * @param wait      待解密密钥
     * @param key       密码，16、24、32位
     * @param iv        向量，必须16位
     * @param algorithm 算法
     * @param charset   字符集
     * @return
     * @throws Exception
     */
    public String decrypt(@NotNull String wait, @NotNull String key, String iv, @NotNull AesUtils.AlgorithmEnum algorithm, @NotNull Charset charset) throws Exception {
        // 按照 mode 生成实例
        Cipher cipher = cipherInstance(Cipher.DECRYPT_MODE, key, iv, algorithm, charset);
        // Base64 解码后解密
        byte[] b = cipher.doFinal(Base64Decoder.decode(wait));
        // 字节码转字符串
        return new String(b, charset);
    }

    /**
     * 按照配置生成 Cipher 实例
     *
     * @param mode      加密或解密
     * @param key       密码，16、24、32位
     * @param iv        向量，必须16位
     * @param algorithm 算法
     * @param charset   字符集
     * @return
     * @throws Exception
     */
    public static Cipher cipherInstance(@NotNull int mode, @NotNull String key, String iv, @NotNull AesUtils.AlgorithmEnum algorithm, @NotNull Charset charset) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm.name);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(charset), "AES");
        if (algorithm.ivFlag && null != iv) {
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(charset));
            cipher.init(mode, keySpec, ivSpec);
        } else {
            cipher.init(mode, keySpec);
        }
        return cipher;
    }

    public enum AlgorithmEnum {
        /*
            算法/模式/填充             16字节加密后数据长度    不满16字节加密后长度
            AES/CBC/NoPadding             16                    不支持
            AES/CBC/PKCS5Padding          32                    16
            AES/CBC/ISO10126Padding       32                    16
            AES/CFB/NoPadding             16                    原始数据长度
            AES/CFB/PKCS5Padding          32                    16
            AES/CFB/ISO10126Padding       32                    16
            AES/ECB/NoPadding             16                    不支持
            AES/ECB/PKCS5Padding          32                    16
            AES/ECB/ISO10126Padding       32                    16
            AES/OFB/NoPadding             16                    原始数据长度
            AES/OFB/PKCS5Padding          32                    16
            AES/OFB/ISO10126Padding       32                    16
            AES/PCBC/NoPadding            16                    不支持
            AES/PCBC/PKCS5Padding         32                    16
            AES/PCBC/ISO10126Padding      32                    16
         */

        // 算法/模式/补码方式
        CBC_NOPADDING("AES/CBC/NoPadding", true),
        CBC_PKCS5("AES/CBC/PKCS5Padding", true),
        ECB_NOPADDING("AES/ECB/NoPadding", true),
        ECB_PKCS5("AES/ECB/PKCS5Padding", true),

        CBC_PKCS7("AES/CBC/PKCS7Padding", true),
        CBC_ISO10126("AES/CBC/ISO10126Padding", true),

        CFB_NO("AES/CFB/NoPadding", true),
        CFB_PKCS5("AES/CFB/PKCS5Padding", true),
        CFB_PKCS7("AES/CFB/PKCS7Padding", true),
        CFB_ISO10126("AES/CFB/ISO10126Padding", true),

        ECB_PKCS7("AES/ECB/PKCS7Padding", false),
        ECB_ISO10126("AES/ECB/ISO10126Padding", false),

        OFB_NO("AES/OFB/NoPadding", true),
        OFB_PKCS5("AES/OFB/PKCS5Padding", true),
        OFB_PKCS7("AES/OFB/PKCS7Padding", true),
        OFB_ISO10126("AES/OFB/ISO10126Padding", true),

        PCBC_PKCS5("AES/PCBC/PKCS5Padding", true),
        PCBC_ISO10126("AES/PCBC/ISO10126Padding", true);

        /**
         * 算法
         */
        private String name;
        /**
         * IV标识
         */
        private boolean ivFlag;

        AlgorithmEnum(String name, boolean ivFlag) {
            this.name = name;
            this.ivFlag = ivFlag;
        }

        public String getName() {
            return name;
        }

        public boolean getIvFlag() {
            return ivFlag;
        }
    }
}
