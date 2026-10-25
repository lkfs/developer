package com.venusoft.developer.crypto.autoconfigure;

import com.venusoft.developer.crypto.CryptoRequestBodyAdvice;
import com.venusoft.developer.crypto.CryptoResponseBodyAdvice;
import com.venusoft.developer.crypto.configuration.CryptoConfiguration;
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
