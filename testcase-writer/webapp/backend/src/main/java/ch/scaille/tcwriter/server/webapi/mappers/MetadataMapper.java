package ch.scaille.tcwriter.server.webapi.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import ch.scaille.tcwriter.generated.api.model.Metadata;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, implementationPackage = "<PACKAGE_NAME>.generated")
public interface MetadataMapper {

	MetadataMapper MAPPER = Mappers.getMapper(MetadataMapper.class);

	Metadata convert(ch.scaille.tcwriter.model.Metadata model);

}
