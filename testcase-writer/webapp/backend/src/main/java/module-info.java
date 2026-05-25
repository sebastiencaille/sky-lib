import org.jspecify.annotations.NullMarked;

@NullMarked module testcase.writer.webapp.backend {

	exports ch.scaille.tcwriter.server.facade;
	exports ch.scaille.tcwriter.server.services;
	exports ch.scaille.tcwriter.server.mappers;
	exports ch.scaille.tcwriter.server.exceptions;
	exports ch.scaille.tcwriter.server.dto;
	exports ch.scaille.tcwriter.server.dao;

	exports ch.scaille.tcwriter.server.webapi.config;
	
	opens ch.scaille.tcwriter.server.config to spring.beans, spring.context, spring.core;
	opens ch.scaille.tcwriter.server.services to spring.core;
	opens ch.scaille.tcwriter.server.mappers to org.mapstruct;

	opens ch.scaille.tcwriter.server.webapi.config to spring.beans, spring.context, spring.core;
	
	
	requires transitive testcase.writer.core;
	requires transitive testcase.writer.javatc;
	requires transitive lib.utils;
	requires transitive lib.persistence;
	requires transitive spring.messaging;
	requires transitive spring.web;
	requires transitive tools.jackson.databind;
	requires transitive jakarta.persistence;
	requires transitive jakarta.transaction;
	requires transitive org.slf4j;
	
	requires spring.tx;
	requires spring.beans;
	requires spring.context;
	requires spring.orm;
	requires spring.session.core;
	requires spring.boot;
	requires spring.core;
	requires spring.boot.webmvc;
	requires spring.webmvc;
	requires spring.websocket;
	requires spring.security.core;
	requires spring.security.crypto;
	requires spring.security.web;
	requires spring.security.config;
	requires spring.boot.autoconfigure;

	requires jakarta.servlet;
	requires org.mapstruct;
	requires org.openapitools.jackson.nullable;
	
	requires transitive org.apache.tomcat.embed.el;
	requires jakarta.validation;

}