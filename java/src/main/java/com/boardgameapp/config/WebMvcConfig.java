package com.boardgameapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * WebMvc の設定（アップロード画像の静的な配信パス）。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /** /api/uploads/** をアップロードディレクトリから配信する。 */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path dir = Path.of(uploadDir).toAbsolutePath();
        String location = "file:" + dir + "/";
        registry.addResourceHandler("/api/uploads/**")
                .addResourceLocations(location)
                .resourceChain(true);
    }
}
