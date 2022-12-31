package ch.scaille.tcwriter.server.webapi.config;

import java.util.List;
import java.util.Optional;

import org.openapitools.configuration.SpringDocConfiguration;
import org.springdoc.core.customizers.*;
import org.springdoc.core.filters.OpenApiMethodFilter;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.*;
import org.springdoc.core.service.*;
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
@Import({ SpringDocConfiguration.class, SpringDocConfigProperties.class })
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
			OperationService operationParser, SpringDocConfigProperties springDocConfigProperties,
			Optional<List<OperationCustomizer>> operationCustomizers,
			Optional<List<OpenApiCustomizer>> openApiCustomisers,
			Optional<List<RouterOperationCustomizer>> routerOperationCustomizers,
			Optional<List<OpenApiMethodFilter>> methodFilters,
			@Qualifier("webApiSpringDocProviders") SpringDocProviders springDocProviders) {
		return new OpenApiWebMvcResource(openAPIBuilderObjectFactory, requestBuilder, responseBuilder, operationParser,
				operationCustomizers, openApiCustomisers, routerOperationCustomizers, methodFilters,
				springDocConfigProperties, springDocProviders);
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
