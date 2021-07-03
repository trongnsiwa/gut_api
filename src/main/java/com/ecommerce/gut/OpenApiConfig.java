package com.ecommerce.gut;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .servers(Lists.newArrayList(new Server().url("http://localhost:8080")))
        .info(new Info().title("GUT Application API")
            .description("GUT - Ecommerce clothing shop")
            .license(new License()
                .name("Apache 2.0")
                .url("http://www.apache.org/licenses/LICENSE-2.0.html"))
            .version("1.0.0"))
        .components(new Components()
            .addSecuritySchemes("mySecretHeader", new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")))
        .addSecurityItem(new SecurityRequirement().addList("mySecretHeader"));
  }

}
