package com.vsl700.nitflex.services.implementations;

import com.vsl700.nitflex.components.WebsiteCredentials;
import com.vsl700.nitflex.services.MovieSeekerService;
import com.vsl700.nitflex.services.WebClientService;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class MovieSeekerServiceImpl implements MovieSeekerService {
    private WebClientService webClientService;
    private WebsiteCredentials.Zamunda zamundaCredentials;

    private static final String zamundaURL = "https://zamunda.net";
    private static final String zamundaCatalogURL = "https://zamunda.net/bananas";
    private static final String zamundaLoginPage = "https://zamunda.net/takelogin.php";

    public MovieSeekerServiceImpl(WebClientService webClientService, WebsiteCredentials.Zamunda zamundaCredentials) {
        this.webClientService = webClientService;
        this.zamundaCredentials = zamundaCredentials;
    }

    @SneakyThrows
    @Override
    public URI findMovieURL() {
        String cookie = webClientService.loginAndGetCookie(zamundaLoginPage,
                "username",
                "password",
                zamundaCredentials);
        if(!cookie.contains("uid")) // TODO: Add custom exception
            throw new RuntimeException("Login failed!");

        String html = webClientService.getWebsiteContents(zamundaCatalogURL, cookie);
        Document doc = Jsoup.parse(html);
        var tableContentTableRows = doc.select("#div1 > table > tbody > tr").stream().skip(1).map(TableRow::new);

        var filtered = tableContentTableRows
                .filter(t -> t.typeOk) // Torrent type filter
                // TODO Add more filters for the other restrictions
                .toList();

        TableRow chosenMovieTableRow = filtered.stream()
                .toList().get(new Random().nextInt(filtered.size()));

        return new URI(zamundaURL + chosenMovieTableRow.link);
    }

    private static class TableRow {
        public String name;
        public String link;
        public float size;
        public boolean typeOk;

        public TableRow(Element element){
            name = Objects.requireNonNull(element.selectFirst("td:nth-child(2) > a > b"))
                    .text();

            link = Objects.requireNonNull(element.selectFirst("td:nth-child(2) > a")).attr("href");

            size = Float.parseFloat(
                    Objects.requireNonNull(element.selectFirst("td:nth-child(6)"))
                            .text()
            );

            String typeImgSrc = Objects.requireNonNull(element.selectFirst("td:nth-child(1) > img"))
                    .attr("src");

            typeOk = typeImgSrc.equals("https://zamunda.net/pic/img/pic/cat_movies_sd.gif")
                    || typeImgSrc.equals("https://zamunda.net/pic/img/pic/cat_movs_hdtv.gif")
                    || typeImgSrc.equals("https://zamunda.net/pic/pic/cat_movies_dvdr.gif")
                    || typeImgSrc.equals("https://zamunda.net/pic/pic/cat_movies_xvidrus.gif")
                    || typeImgSrc.equals("https://zamunda.net/pic/pic/cat_3d.gif")
                    || typeImgSrc.equals("https://zamunda.net/pic/pic/cat_movies_science.gif")
                    || typeImgSrc.equals("https://zamunda.net/pic/img/pic/cat_anime_anime.gif")
                    || typeImgSrc.equals("https://zamunda.net/pic/pic/cat_bluray.gif");
        }
    }
}
