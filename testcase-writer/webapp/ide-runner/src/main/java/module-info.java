import org.jspecify.annotations.NullMarked;

@NullMarked 
module testcase.writer.webapp.ide.runner {
	
	requires testcase.writer.webapp.backend;
	requires testcase.writer.webapp.api.v0.backend;
	requires testcase.writer.webapp.api.validators;
	
	requires jakarta.servlet;
	requires jakarta.validation;
	requires java.compiler;
	
	requires spring.boot;
	requires spring.boot.webmvc;
	requires spring.boot.autoconfigure;
	
	requires spring.tx;
	requires spring.beans;
	requires spring.context;
	requires spring.orm;
	requires spring.core;
	requires spring.messaging;
	requires spring.web;
	requires spring.webmvc;
	requires spring.websocket;
	requires spring.security.core;
	requires spring.security.crypto;
	requires spring.security.web;
	requires spring.security.config;
	requires spring.session.core;
    requires spring.aop;

    requires com.fasterxml.jackson.databind;

    requires org.mapstruct;
	requires org.openapitools.jackson.nullable;
	
	requires io.swagger.v3.oas.annotations;
	
}