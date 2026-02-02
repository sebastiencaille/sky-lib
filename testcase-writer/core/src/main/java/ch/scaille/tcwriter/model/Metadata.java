package ch.scaille.tcwriter.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Metadata {

    @JsonIgnore
    private String transientId = null;

    private String description = "";

    private LocalDateTime creationDate;

    private Set<String> tags = new HashSet<>();

    public Metadata() {
    }

    @JsonCreator
    public Metadata(String transientId, String description, LocalDateTime creationDate, Set<String> tags) {
        this.transientId = transientId;
        this.description = description;
        this.creationDate = creationDate;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    @Override
    public String toString() {
        return transientId + " " + tags;
    }

    public boolean matches(Metadata other) {
        return other != null && other.tags.stream().anyMatch(tags::contains);
    }

    public boolean matches(String other) {
        return other != null && tags.contains(other);
    }
}
