package ch.scaille.tcwriter.server.webapi.v0.mappers;

import ch.scaille.tcwriter.server.mappers.DefaultMappers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import ch.scaille.tcwriter.generated.api.model.v0.Context;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, implementationPackage = "<PACKAGE_NAME>.generated",
        uses = DefaultMappers.class)
public interface ContextMapper {

    ContextMapper MAPPER = Mappers.getMapper(ContextMapper.class);

    @Mappings(
            @Mapping(source = "dictionaryName", target = "dictionary")
    )
    Context convert(ch.scaille.tcwriter.server.dto.Context model);

    @Mappings(
            @Mapping(source = "dictionary", target = "dictionaryName")
    )
    ch.scaille.tcwriter.server.dto.Context convert(Context model);

}
