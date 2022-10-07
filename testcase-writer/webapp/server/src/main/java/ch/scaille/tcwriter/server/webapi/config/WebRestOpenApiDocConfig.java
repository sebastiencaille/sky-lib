package ch.scaille.tcwriter.server.webapi.config;

import java.util.List;
import java.util.Optional;

import org.springdoc.core.AbstractRequestService;
import org.springdoc.core.GenericResponseService;
import org.springdoc.core.OpenAPIService;
import org.springdoc.core.OperationService;
import org.springdoc.core.PropertyResolverUtils;
import org.springdoc.core.SecurityService;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.SpringDocProviders;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.customizers.RouterOperationCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.filters.OpenApiMethodFilter;
import org.springdoc.core.providers.ActuatorProvider;
import org.springdoc.core.providers.CloudFunctionProvider;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.core.providers.RepositoryRestResourceProvider;
import org.springdoc.core.providers.RouterFunctionProvider;
import org.springdoc.core.providers.SecurityOAuth2Provider;
import org.springdoc.core.providers.SpringWebProvider;
import org.springdoc.core.providers.WebConversionServiceProvider;
import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springdoc.webmvc.core.SpringWebMvcProvider;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;

@Configuration
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
	public OpenApiWebMvcResource webApiOpenApiResource(
			@Qualifier("webApiOpenAPIBuilder") ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory,
			AbstractRequestService requestBuilder, GenericResponseService responseBuilder,
			OperationService operationParser, SpringDocConfigProperties springDocConfigProperties,
			Optional<List<OperationCustomizer>> operationCustomizers,
			Optional<List<OpenApiCustomiser>> openApiCustomisers,
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
