package com.freeshelf.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableTransactionManagement
public class AppConfig {

  @Bean
  AuditorAware<String> auditorProvider() {
    return new AuditorAwareImpl();
  }


  @Bean
  GroupedOpenApi apiV1() {
    return GroupedOpenApi.builder().group("v1").pathsToMatch("/v1/**").build();
  }

  @Bean
  OpenAPI rideShareUserApiV1() {
    return new OpenAPI().info(new Info().title("Free Shelf Api")
        .description("Api docs for Free Shelf application").version("0.0.1"));
  }

  @Bean
  @Primary
  public OpenAPI customOpenAPI() {
    return new OpenAPI().addSecurityItem(new SecurityRequirement().addList("BearerAuth")) // Enables
        .components(new Components().addSecuritySchemes("BearerAuth",
            new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")
                .bearerFormat("JWT")))
        .info(new Info().title("Free Shelf Api ").description("Api docs for Free Shelf application")
            .version("0.0.1")); // You can change this to Basic if using Basic Auth
  }

}
