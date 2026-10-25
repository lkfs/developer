package io.github.lkfs.developer.web.language;

/**
 * 错误码多语言服务接口
 * @author Luis
 * @date 2023/10/20 15:17
 */
public interface IErrorLanguageService {

    /***
     * 提取多语言内容
     * @date 2023/10/20 15:54
     */
    String getMessage(String code);
}
