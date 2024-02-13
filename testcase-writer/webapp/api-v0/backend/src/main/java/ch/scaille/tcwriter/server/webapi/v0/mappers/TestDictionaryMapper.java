package ch.scaille.tcwriter.server.webapi.v0.mappers;

import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.google.common.collect.Multimap;

import ch.scaille.tcwriter.generated.api.model.v0.TestAction;
import ch.scaille.tcwriter.generated.api.model.v0.TestActor;
import ch.scaille.tcwriter.generated.api.model.v0.TestApiParameter;
import ch.scaille.tcwriter.generated.api.model.v0.TestDictionary;
import ch.scaille.tcwriter.generated.api.model.v0.TestObjectDescription;
import ch.scaille.tcwriter.generated.api.model.v0.TestParameterFactory;
import ch.scaille.tcwriter.generated.api.model.v0.TestRole;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, implementationPackage = "<PACKAGE_NAME>.generated")
public interface TestDictionaryMapper extends MetadataMapper {

	TestDictionaryMapper MAPPER = Mappers.getMapper(TestDictionaryMapper.class);

	TestDictionary convert(ch.scaille.tcwriter.model.dictionary.TestDictionary model);

	default List<TestRole> convertRoleList(Map<String, ch.scaille.tcwriter.model.dictionary.TestRole> model) {
		return model.values().stream().map(this::convert).toList();
	}

	TestRole convert(ch.scaille.tcwriter.model.dictionary.TestRole model);

	TestAction convert(ch.scaille.tcwriter.model.dictionary.TestAction model);

	TestApiParameter convert(ch.scaille.tcwriter.model.dictionary.TestApiParameter model);

	default String convertToId(ch.scaille.tcwriter.model.dictionary.TestRole model) {
		return model.getId();
	}

	default List<TestActor> convertActorsList(Map<String, ch.scaille.tcwriter.model.dictionary.TestActor> model) {
		return model.values().stream().map(this::convert).toList();
	}

	TestActor convert(ch.scaille.tcwriter.model.dictionary.TestActor model);

	default List<TestParameterFactory> convertTestParameterFactoryList(
			Multimap<String, ch.scaille.tcwriter.model.dictionary.TestParameterFactory> model) {
		return model.values().stream().map(this::convert).toList();
	}

	TestParameterFactory convert(ch.scaille.tcwriter.model.dictionary.TestParameterFactory model);

	TestObjectDescription convert(ch.scaille.tcwriter.model.TestObjectDescription model);

}
