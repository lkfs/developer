package io.github.lkfs.developer.crypto.autoconfigure;

import io.github.lkfs.developer.crypto.CryptoRequestBodyAdvice;
import io.github.lkfs.developer.crypto.CryptoResponseBodyAdvice;
import io.github.lkfs.developer.crypto.configuration.CryptoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
        CryptoConfiguration.class,
        CryptoRequestBodyAdvice.class,
        CryptoResponseBodyAdvice.class
})
public class DeveloperCryptoAutoConfiguration {
}
