package ch.scaille.tcwriter.examples.websearch.selectors;

import ch.scaille.tcwriter.annotations.TCApi;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@TCApi(description = "how to execute the search", humanReadable = "", isSelector = true)
public record EngineSearchSelector(String queryUrl, String mainPageXPath) {

    @TCApi(description = "DuckDuckGo search", humanReadable = "%s on DuckDuckGo", isSelector = true)
    public static EngineSearchSelector duckDuckGo(String query) {
        return new EngineSearchSelector("https://www.duckduckgo.com/search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8), "//div[@id= 'react-layout']");
    }

    @TCApi(description = "Bing search", humanReadable = "%s on Bing", isSelector = true)
    public static EngineSearchSelector bing(String query) {
        return new EngineSearchSelector("https://www.bing.com/search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8), "//div[@id='b_content']");
    }

    @TCApi(description = "Other search engine, search query will be appended to the url", humanReadable = "on %s", isSelector = true)
    public static EngineSearchSelector other(String url) {
        return new EngineSearchSelector(url, "//");
    }
}
