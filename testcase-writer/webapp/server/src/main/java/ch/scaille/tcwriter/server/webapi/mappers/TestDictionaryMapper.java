package ch.scaille.tcwriter.server.webapi.mappers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.google.common.collect.Multimap;

import ch.scaille.tcwriter.generated.api.model.Metadata;
import ch.scaille.tcwriter.generated.api.model.TestAction;
import ch.scaille.tcwriter.generated.api.model.TestActor;
import ch.scaille.tcwriter.generated.api.model.TestApiParameter;
import ch.scaille.tcwriter.generated.api.model.TestDictionary;
import ch.scaille.tcwriter.generated.api.model.TestObjectDescription;
import ch.scaille.tcwriter.generated.api.model.TestParameterFactory;
import ch.scaille.tcwriter.generated.api.model.TestRole;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TestDictionaryMapper {

	TestDictionaryMapper MAPPER = Mappers.getMapper(TestDictionaryMapper.class);

	Metadata convert(ch.scaille.tcwriter.model.Metadata model);

	TestDictionary convert(ch.scaille.tcwriter.model.dictionary.TestDictionary model);

	default List<TestRole> convertRoleList(Map<String, ch.scaille.tcwriter.model.dictionary.TestRole> model) {
		return model.values().stream().map(this::convert).collect(Collectors.toList());
	}

	TestRole convert(ch.scaille.tcwriter.model.dictionary.TestRole model);
	
	TestAction convert(ch.scaille.tcwriter.model.dictionary.TestAction model);
	
	TestApiParameter convert(ch.scaille.tcwriter.model.dictionary.TestApiParameter model);

	default String convertToId(ch.scaille.tcwriter.model.dictionary.TestRole model) {
		return model.getId(); 
	}
	
	default List<TestActor> convertActorsList(Map<String, ch.scaille.tcwriter.model.dictionary.TestActor> model) {
		return model.values().stream().map(this::convert).collect(Collectors.toList());
	}

	TestActor convert(ch.scaille.tcwriter.model.dictionary.TestActor model);

	default List<TestParameterFactory> convertTestParameterFactoryList(
			Multimap<String, ch.scaille.tcwriter.model.dictionary.TestParameterFactory> model) {
		return model.values().stream().map(this::convert).collect(Collectors.toList());
	}
	
	TestParameterFactory convert(ch.scaille.tcwriter.model.dictionary.TestParameterFactory model);

	default List<TestObjectDescription> convertTestObjectDescriptionList(
			Map<String, ch.scaille.tcwriter.model.TestObjectDescription> model) {
		return model.values().stream().map(this::convert).collect(Collectors.toList());
	}

	TestObjectDescription convert(ch.scaille.tcwriter.model.TestObjectDescription model);

}
