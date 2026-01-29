package ch.scaille.tcwriter.examples.websearch.dto;

import ch.scaille.tcwriter.annotations.TCApi;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

@TCApi(description = "how to execute the search", humanReadable = "", isSelector = true)
@Getter
@RequiredArgsConstructor
public class MatcherDto {

    private final Predicate<String> matcher;

    @TCApi(description = "First link that starts with...", humanReadable = "the first link that starts with %s")
    public static MatcherDto startsWith(String start) {
        return new MatcherDto(link -> link.startsWith(start));
    }

    @TCApi(description = "First link that contains...", humanReadable = "the first link that contains %s")
    public static MatcherDto contains(String contains) {
        return new MatcherDto(link -> link.contains(contains));
    }
}
