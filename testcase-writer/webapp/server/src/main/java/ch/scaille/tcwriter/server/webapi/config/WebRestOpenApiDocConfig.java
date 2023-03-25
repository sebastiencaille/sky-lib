package ch.scaille.tcwriter.server.webapi.config;

import java.util.List;
import java.util.Optional;

import org.openapitools.configuration.SpringDocConfiguration;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.customizers.SpringDocCustomizers;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.ActuatorProvider;
import org.springdoc.core.providers.CloudFunctionProvider;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.core.providers.RepositoryRestResourceProvider;
import org.springdoc.core.providers.RouterFunctionProvider;
import org.springdoc.core.providers.SecurityOAuth2Provider;
import org.springdoc.core.providers.SpringDocProviders;
import org.springdoc.core.providers.SpringWebProvider;
import org.springdoc.core.providers.WebConversionServiceProvider;
import org.springdoc.core.service.AbstractRequestService;
import org.springdoc.core.service.GenericResponseService;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.OperationService;
import org.springdoc.core.service.SecurityService;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springdoc.webmvc.core.providers.SpringWebMvcProvider;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.swagger.v3.oas.models.OpenAPI;

@Configuration
@Import({SpringDocConfiguration.class, SpringDocConfigProperties.class})
public class WebRestOpenApiDocConfig {

    @Bean
    SpringDocProviders webApiSpringDocProviders(Optional<ActuatorProvider> actuatorProvider,
                                                Optional<CloudFunctionProvider> springCloudFunctionProvider,
                                                Optional<SecurityOAuth2Provider> springSecurityOAuth2Provider,
                                                Optional<RepositoryRestResourceProvider> repositoryRestResourceProvider,
                                                Optional<RouterFunctionProvider> routerFunctionProvider,
                                                @Qualifier("webApiSpringWebProvider") Optional<SpringWebProvider> springWebProvider,
                                                Optional<WebConversionServiceProvider> webConversionServiceProvider,
                                                ObjectMapperProvider objectMapperProvider) {
        return new SpringDocProviders(actuatorProvider, springCloudFunctionProvider, springSecurityOAuth2Provider,
                repositoryRestResourceProvider, routerFunctionProvider, springWebProvider, webConversionServiceProvider,
                objectMapperProvider);
    }

    @Bean
    OpenApiWebMvcResource webApiOpenApiResource(
            @Qualifier("webApiOpenAPIBuilder") ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory,
            AbstractRequestService requestBuilder, GenericResponseService responseBuilder,
            OperationService operationParser,
            SpringDocConfigProperties springDocConfigProperties,
            @Qualifier("webApiSpringDocProviders") SpringDocProviders springDocProviders,
            SpringDocCustomizers springDocCustomizers) {
        return new OpenApiWebMvcResource(openAPIBuilderObjectFactory, requestBuilder, responseBuilder, operationParser,
                springDocConfigProperties,
                springDocProviders, springDocCustomizers);
    }


    @Bean
    OpenAPIService webApiOpenAPIBuilder(Optional<OpenAPI> openAPI, SecurityService securityParser,
                                        SpringDocConfigProperties springDocConfigProperties, PropertyResolverUtils propertyResolverUtils,
                                        Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomisers,
                                        Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomisers,
                                        Optional<JavadocProvider> javadocProvider) {
        return new OpenAPIService(openAPI, securityParser, springDocConfigProperties, propertyResolverUtils,
                openApiBuilderCustomisers, serverBaseUrlCustomisers, javadocProvider);
    }

    @Bean
    SpringWebProvider webApiSpringWebProvider() {
        return new SpringWebMvcProvider();
    }

}
