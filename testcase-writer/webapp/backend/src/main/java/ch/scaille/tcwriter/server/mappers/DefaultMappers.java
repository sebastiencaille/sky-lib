package ch.scaille.tcwriter.server.mappers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta")
public interface DefaultMappers {

    @Nullable
    default <T> T fromOptional(Optional<T> value) {
        return value.orElse(null);
    }

    default <T> Optional<T> toOptional(@Nullable T value) {
        return Optional.ofNullable(value);
    }
    
    default <T> List<T> setToList(Set<T> set)  {
    	return new ArrayList<>(set);
    }

    default <T> Set<T> listtToSet(List<T> set)  {
    	return new HashSet<>(set);
    }

}
