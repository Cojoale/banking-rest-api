package com.gohenry.bank.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@Configuration
@EnableJpaAuditing
@EnableSwagger2
public class AppConfig {

    @Bean
    public Docket docket() {
        return new Docket(SWAGGER_2).apiInfo(apiInfo())
                                    .select()
                                    .apis(basePackage("com.gohenry.bank"))
                                    .paths(PathSelectors.any())
                                    .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("GoHenry Bank REST API")
                                   .description("Our public API gives you the possibility to create and manage multiple bank accounts")
                                   .version("1.0.0")
                                   .build();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return new Jackson2ObjectMapperBuilderCustomizer() {

            @Override
            public void customize(Jackson2ObjectMapperBuilder builder) {
                builder.serializers(new LocalDateTimeSerializer()).deserializers(new LocalDateTimeDeserializer());
            }
        };
    }
}
