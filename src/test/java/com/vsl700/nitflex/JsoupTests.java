package com.vsl700.nitflex;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
public class JsoupTests {

    @Test
    public void findElementById_Test() throws IOException {
        Document doc = Jsoup.connect("https://zamunda.net/banan?id=747184&hit=1&t=movie").get();
        Element downloadLink = doc.getElementById("leftadedd");

        assertThat(downloadLink).isNotNull();
    }

    @Test
    public void findElementByCSSSelector_File_Test() throws IOException {
        Document doc = Jsoup.parse(new File("D:\\Downloads\\Oppenheimer _ Опенхаймер (2023) - Zamunda.NET (banan).html"));
        Element downloadLink = doc.selectFirst("a.index.notranslate");

        assertThat(downloadLink).isNotNull();

        System.out.println(downloadLink.text());
    }

    @Test
    public void findElementByCSSSelector_URL_Test() throws IOException { // This test WILL fail because of 'getZamundaContents'
        String html = getZamundaContents("https://zamunda.net/banan?id=747184&hit=1&t=movie");

        Document doc = Jsoup.parse(html);
        Element downloadLink = doc.selectFirst("a.index.notranslate");

        assertThat(downloadLink).isNotNull();

        System.out.println(downloadLink.text());
    }

    private String getZamundaContents(String theUrl) { // !!! There is no cookie values provided !!!
        StringBuilder content = new StringBuilder();
        // Use try and catch to avoid the exceptions
        try {
            URL url = new URL(theUrl); // creating a url object
            URLConnection urlConnection = url.openConnection(); // creating a urlconnection object
            urlConnection.setRequestProperty("Cookie", "uid={uid}; pass={pass}");

            // wrapping the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            // reading from the urlconnection using the bufferedreader
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    @Test
    public void findElementByCSSSelector2_URL_Test() throws IOException {
        Document doc = Jsoup.connect("https://en.wikipedia.org/").get();
        System.out.println(doc.title());
        Elements newsHeadlines = doc.select(".mw-footer-container");
        for (Element headline : newsHeadlines) {
            System.out.printf("%s\n\t%s%n", headline.attr("title"), headline.absUrl("href"));
        }
    }

}
