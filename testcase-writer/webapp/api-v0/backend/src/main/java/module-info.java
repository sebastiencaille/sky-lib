import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.webapp.api.v0.backend {
	exports ch.scaille.tcwriter.server.webapi.v0.autoconfigure to spring.beans, spring.context, spring.core;
	exports ch.scaille.tcwriter.server.webapi.v0.controllers to spring.web;
	exports ch.scaille.tcwriter.generated.api.config.v0;

	opens ch.scaille.tcwriter.server.webapi.v0.autoconfigure to spring.beans, spring.context, spring.core;
	opens ch.scaille.tcwriter.server.webapi.v0.controllers to spring.beans, spring.context, spring.core;
	opens ch.scaille.tcwriter.server.webapi.v0.mappers to org.mapstruct;
	
	opens ch.scaille.tcwriter.generated.api.model.v0 to tools.jackson.databind, org.hibernate.validator;
	
	requires transitive testcase.writer.webapp.backend;
	requires testcase.writer.webapp.api.validators;
	requires spring.core;
	requires spring.context;
	requires spring.web;
	requires spring.boot.autoconfigure;
	requires spring.boot;
	requires spring.webmvc;
	requires spring.tx;
	requires com.fasterxml.jackson.databind;
	requires org.openapitools.jackson.nullable;
	requires com.google.common;
	requires jakarta.annotation;
	requires jakarta.validation;
	requires jakarta.servlet;
	requires io.swagger.v3.oas.annotations;
	requires io.swagger.v3.oas.models;
	requires java.compiler;
	requires org.mapstruct;
}