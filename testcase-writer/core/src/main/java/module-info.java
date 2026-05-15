import org.jspecify.annotations.NullMarked;

@NullMarked
module testcase.writer.core {
    exports ch.scaille.tcwriter.model.config;
    exports ch.scaille.tcwriter.persistence;
    exports ch.scaille.tcwriter.persistence.factory;
    exports ch.scaille.tcwriter.persistence.handlers.serdeser;
    exports ch.scaille.tcwriter.persistence.handlers.serdeser.mixins;
    exports ch.scaille.tcwriter.services.generators.visitors;
    exports ch.scaille.tcwriter.services.testexec;
    
    requires transitive testcase.writer.model;
    requires transitive lib.persistence;
    requires transitive lib.javabeans;
    requires transitive lib.annotations;
    requires transitive lib.generator.utils;
	requires transitive tools.jackson.databind;
    
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.datatype.guava;
    requires tools.jackson.dataformat.yaml;
    requires com.google.common;
}