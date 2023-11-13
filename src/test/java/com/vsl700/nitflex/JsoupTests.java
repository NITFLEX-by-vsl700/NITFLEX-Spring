package com.vsl700.nitflex;

import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import reactor.netty.http.client.HttpClient;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static io.netty.handler.codec.http.HttpHeaders.Values.KEEP_ALIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class JsoupTests {

    @Test
    @Disabled
    public void findElementById_Test() throws IOException {
        Document doc = Jsoup.connect("https://zamunda.net/banan?id=747184&hit=1&t=movie").get();
        Element downloadLink = doc.getElementById("leftadedd");

        assertThat(downloadLink).isNotNull();
    }

    @Test
    @Disabled
    public void findElementByCSSSelector_File_Test() throws IOException {
        Document doc = Jsoup.parse(new File("D:\\Downloads\\Oppenheimer _ Опенхаймер (2023) - Zamunda.NET (banan).html"));
        Element downloadLink = doc.selectFirst("a.index.notranslate");

        assertThat(downloadLink).isNotNull();

        System.out.println(downloadLink.text());
    }

    @Test
    @Disabled
    public void findElementByCSSSelector_URL_Test() throws IOException { // This test WILL fail because of 'getZamundaContents'
        String html = getZamundaContentsViaWebClient("https://zamunda.net/banan?id=747184&hit=1&t=movie");

        Document doc = Jsoup.parse(html);
        Element downloadLink = doc.selectFirst("a.index.notranslate");

        assertThat(downloadLink).isNotNull();

        System.out.println(downloadLink.text());
        System.out.println(downloadLink.attr("href"));
    }

    private String getZamundaContents(String theUrl) { // !!! There is no cookie values provided !!!
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("Cookie", "uid={uid}; pass={pass}");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }

            bufferedReader.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return content.toString();
    }

    private String getZamundaContentsViaWebClient(String url){
        WebClient client = WebClient.builder()
                //.clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
                .baseUrl(url)
                .defaultHeader("Cookie", "uid={uid}; pass={pass}")
                //.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE)
                //.defaultUriVariables(Collections.singletonMap("url", "https://zamunda.net"))
                .build();

        AtomicReference<String> result = new AtomicReference<>();
//        String res = client.get().accept(MediaType.TEXT_HTML)
//                .retrieve()
//                .onStatus(status -> {System.out.println(status.value()); return !status.is2xxSuccessful();},
//                        ClientResponse::createException)
//
//                .bodyToMono(String.class)
//                .block();

        String res = client.get()
                .exchangeToMono(response -> {
                    System.out.println(response.statusCode().value());
                    System.out.println(response.headers().asHttpHeaders());
                    return response.bodyToMono(String.class);
                }).block();

        result.set(res);

        return result.get();
    }

    @Test
    @Disabled
    public void findElementByCSSSelector2_URL_Test() throws IOException {
        Document doc = Jsoup.connect("https://en.wikipedia.org/").get();
        System.out.println(doc.title());
        Elements newsHeadlines = doc.select(".mw-footer-container");
        for (Element headline : newsHeadlines) {
            System.out.printf("%s\n\t%s%n", headline.attr("title"), headline.absUrl("href"));
        }
    }

}
