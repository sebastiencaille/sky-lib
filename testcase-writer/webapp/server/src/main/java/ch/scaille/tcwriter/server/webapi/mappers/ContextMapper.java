package ch.scaille.tcwriter.server.webapi.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import ch.scaille.tcwriter.generated.api.model.Context;
import ch.scaille.tcwriter.generated.api.model.ContextUpdate;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ContextMapper {

	ContextMapper MAPPER = Mappers.getMapper(ContextMapper.class);
	
	Context convert(ch.scaille.tcwriter.server.dto.Context model);

	@Mapping(target = "identity", ignore = true)
	ch.scaille.tcwriter.server.dto.Context convert(ContextUpdate api);
	
}
