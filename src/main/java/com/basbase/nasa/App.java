package com.basbase.nasa;

import com.basbase.nasa.client.HttpClient;
import com.basbase.nasa.model.Pair;
import com.basbase.nasa.model.Uri;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import static com.basbase.nasa.client.HttpClientFactoryUtil.createHttpClient;
import static java.util.concurrent.CompletableFuture.supplyAsync;

public class App {
    private static final String API_KEY = "hKfg7MJKtyIf7kiPZ6fHrlkw7yKh3BRZQdLgHxBR";
    private static final String URL = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=15&api_key="
            + API_KEY;

    public static void main(String[] args) {
        System.out.println("Started NASA's pictures extractor, it takes some time...");
        System.out.println();

        var httpClient = createHttpClient();
        var urls = getUrls(httpClient);

        var start = System.nanoTime();
        var pair = urls.parallelStream()
                .map(url -> supplyAsync(() -> getLength(httpClient, url)).join())
                .max(Comparator.comparing(Pair::length))
                .orElseThrow();

        var stop = System.nanoTime();
        System.out.println(pair);
        System.out.printf("Execution time of Async request: %s sec%n", elapsed(start, stop));
        System.out.println();

        start = System.nanoTime();
        pair = urls.stream()
                .map(url -> getLength(httpClient, url))
                .max(Comparator.comparing(Pair::length))
                .orElseThrow();
        stop = System.nanoTime();

        System.out.println(pair);
        System.out.printf("Execution time of Sync request: %s sec%n", elapsed(start, stop));
    }

    private static double elapsed(long start, long stop) {
        return (stop - start) / 1_000_000_000.0;
    }

    private static List<String> getUrls(HttpClient client) {
        return client.get(Uri.createFrom(URL), App::extractUrls).body();
    }

    private static Pair getLength(HttpClient client, String url) {
        return Pair.builder()
                .length(Long.parseLong(client.head(Uri.createFrom(url))
                        .headers()
                        .get("Content-Length")))
                .url(url)
                .build();
    }

    @SneakyThrows
    private static List<String> extractUrls(BufferedReader in) {
        return Pattern.compile("\"img_src\":\"(http.*?)\",\"earth_date")
                .matcher(in.readLine())
                .results()
                .map(matchResult -> matchResult.group(1))
                .toList();
    }
}
