package com.example.shopuserservice.config;

import io.undertow.UndertowOptions;
import org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowReactiveWebServerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UndertowConfig implements WebServerFactoryCustomizer<UndertowReactiveWebServerFactory> {

    @Override
    public void customize(UndertowReactiveWebServerFactory factory) {
        factory.setIoThreads(16);
        factory.setWorkerThreads(256);
        // SSE 와 버퍼고려
        factory.addBuilderCustomizers((UndertowBuilderCustomizer) builder -> {
            builder.setServerOption(UndertowOptions.ALLOW_UNESCAPED_CHARACTERS_IN_URL, Boolean.TRUE);
            builder.setServerOption(UndertowOptions.NO_REQUEST_TIMEOUT,10000);
            builder.setServerOption(UndertowOptions.MAX_CONCURRENT_REQUESTS_PER_CONNECTION, 2000);
            builder.setServerOption(UndertowOptions.HTTP2_SETTINGS_MAX_CONCURRENT_STREAMS, 2000);
            builder.setServerOption(UndertowOptions.MAX_QUEUED_READ_BUFFERS, 1000);
        });
    }
}
