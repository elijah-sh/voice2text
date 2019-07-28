/**
 * 文件名: SwaggerConfig.java
 * 版权：Copyright 2017-2022 CMCC All Rights Reserved.
 * 描述:
 */
package voice2text.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

/**
 * @description:
 * @author: Shenshuaihu
 * @version: 1.0
 * @data: 2019-06-03 09:41
 */
@Configuration
@EnableSwagger2     //启用 Swagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }
    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Spring Boot 语音转文字 项目集成 Swagger 实例文档",
                "用于音频转文字",
                "API V1.0",
                "Terms of service",
                new Contact("名字想好没", "https://itweknow.cn", "gancy.programmer@gmail.com"),
                "Apache", "http://www.apache.org/", Collections.emptyList());
    }
}
