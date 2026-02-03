package ch.scaille.tcwriter.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Setter
public class Metadata {

    private String transientId = "";

    @Getter
    private String description = "";

    @Getter
    private LocalDateTime creationDate;

    @Getter
    private Set<String> tags = new HashSet<>();

    public Metadata() {
    }

    public Metadata(String transientId, String description, Set<String> tags) {
        this.transientId = transientId;
        this.description = description;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    @JsonIgnore
    public String getTransientId() {
        return transientId;
    }

    @Override
    public String toString() {
        return transientId + " " + tags;
    }

    public boolean matches(Metadata other) {
        return other != null && other.tags.stream().anyMatch(tags::contains);
    }
}
