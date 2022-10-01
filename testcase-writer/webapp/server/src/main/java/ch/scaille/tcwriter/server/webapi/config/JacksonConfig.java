package ch.scaille.tcwriter.server.webapi.config;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
  @Bean
  public JsonNullableModule jsonNullableModule() {
    return new JsonNullableModule();
  }
}