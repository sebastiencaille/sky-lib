package ch.scaille.tcwriter.server.webapi.v0.mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import ch.scaille.tcwriter.generated.api.model.v0.Metadata;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, implementationPackage = "<PACKAGE_NAME>.generated")
public interface MetadataMapper {

	MetadataMapper MAPPER = Mappers.getMapper(MetadataMapper.class);

	Metadata convert(ch.scaille.tcwriter.model.Metadata model);

	default LocalDateTime convert(OffsetDateTime from) {
		if (from == null) {
			return null;
		}
		return from.toLocalDateTime();
	}

	default OffsetDateTime convert(LocalDateTime from) {
		if (from == null) {
			return null;
		}
		return from.atZone(ZoneId.systemDefault()).toOffsetDateTime();
	}

}
