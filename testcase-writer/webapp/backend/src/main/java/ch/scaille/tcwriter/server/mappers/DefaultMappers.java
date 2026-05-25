package ch.scaille.tcwriter.server.mappers;

import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;

import java.util.Optional;

@Mapper(componentModel = "jakarta")
public interface DefaultMappers {

    @Nullable
    default <T> T fromOptional(Optional<T> value) {
        return value.orElse(null);
    }

    default <T> Optional<T> toOptional(@Nullable T value) {
        return Optional.ofNullable(value);
    }

}
