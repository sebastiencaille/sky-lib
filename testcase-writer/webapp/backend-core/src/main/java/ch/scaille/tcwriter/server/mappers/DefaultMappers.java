package ch.scaille.tcwriter.server.mappers;

import org.mapstruct.Mapper;

import java.util.Optional;

@Mapper
public interface DefaultMappers {

    default <T> T fromOptional(Optional<T> value) {
        return value.orElse(null);
    }

    default <T> Optional<T> toOptional(T value) {
        return Optional.ofNullable(value);
    }

}
