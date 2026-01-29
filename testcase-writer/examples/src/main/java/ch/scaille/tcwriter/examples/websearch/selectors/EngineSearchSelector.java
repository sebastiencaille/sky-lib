package ch.scaille.tcwriter.examples.websearch.selectors;

import ch.scaille.tcwriter.annotations.TCApi;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.URLEncoder;

@TCApi(description = "how to execute the search", humanReadable = "", isSelector = true)
@Getter
@RequiredArgsConstructor
public class EngineSearchSelector {

    private final String queryUrl;
    private final String mainPageXPath;

    @TCApi(description = "DuckDuckGo search", humanReadable = "on DuckDuckGo", isSelector = true)
    public static EngineSearchSelector duckDuckGo(String query) {
        return new EngineSearchSelector("https://www.duckduckgo.com/search?q=" + URLEncoder.encode(query),  "//div[@id= 'react-layout']");
    }

    @TCApi(description = "Bing search", humanReadable = "on Bing", isSelector = true)
    public static EngineSearchSelector bing(String query) {
        return new EngineSearchSelector("https://www.bing.com/search?q=" + URLEncoder.encode(query), "//div[@id='b_content']");
    }

    @TCApi(description = "Other search engine, search query will be appended to the url", humanReadable = "on %s", isSelector = true)
    public static EngineSearchSelector other(String url) {
        return new EngineSearchSelector(url, "//");
    }
}
