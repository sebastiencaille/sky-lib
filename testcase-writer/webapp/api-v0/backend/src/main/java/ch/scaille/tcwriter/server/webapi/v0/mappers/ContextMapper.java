package ch.scaille.tcwriter.server.webapi.v0.mappers;

import java.util.Optional;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import ch.scaille.tcwriter.generated.api.model.v0.Context;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, implementationPackage = "<PACKAGE_NAME>.generated")
public interface ContextMapper {

	ContextMapper MAPPER = Mappers.getMapper(ContextMapper.class);

	Context convert(ch.scaille.tcwriter.server.dto.Context model);

	ch.scaille.tcwriter.server.dto.Context convert(Context model);

	default <T> T fromOptional(Optional<T> optional) {
		return optional.orElse(null);
	}

	default <T> Optional<T> toOptional(T value) {
		return Optional.ofNullable(value);
	}

}
