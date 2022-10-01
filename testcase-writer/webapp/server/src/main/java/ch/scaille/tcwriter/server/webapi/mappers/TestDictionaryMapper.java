package ch.scaille.tcwriter.server.webapi.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import ch.scaille.tcwriter.generated.api.model.Metadata;
import ch.scaille.tcwriter.generated.api.model.TestDictionary;
import ch.scaille.tcwriter.generated.api.model.TestRole;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TestDictionaryMapper {

	TestDictionaryMapper MAPPER = Mappers.getMapper(TestDictionaryMapper.class);

	Metadata convert(ch.scaille.tcwriter.model.testapi.Metadata model);
	
	TestDictionary convert(ch.scaille.tcwriter.model.testapi.TestDictionary model);

	TestRole convert(ch.scaille.tcwriter.model.testapi.TestRole model);

}
