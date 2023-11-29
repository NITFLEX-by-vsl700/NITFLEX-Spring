package com.vsl700.nitflex.services.implementations;

import com.vsl700.nitflex.components.InitialMoviesLoader;
import com.vsl700.nitflex.components.WebsiteCredentials;
import com.vsl700.nitflex.services.MovieSeekerService;
import com.vsl700.nitflex.services.WebClientService;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Objects;
import java.util.Random;

public class MovieSeekerServiceImpl implements MovieSeekerService {
    private WebClientService webClientService;
    private WebsiteCredentials.Zamunda zamundaCredentials;

    private static final Logger LOG = LoggerFactory.getLogger(MovieSeekerServiceImpl.class);

    private static final String zamundaURL = "https://zamunda.net";
    private static final String zamundaCatalogURL = "https://zamunda.net/bananas";
    private static final String zamundaLoginPage = "https://zamunda.net/takelogin.php";

    public MovieSeekerServiceImpl(WebClientService webClientService, WebsiteCredentials.Zamunda zamundaCredentials) {
        this.webClientService = webClientService;
        this.zamundaCredentials = zamundaCredentials;
    }

    @SneakyThrows
    @Override
    public URL findMovieURL() {
        // Login and get necessary cookie
        LOG.info("Logging in as '%s'...".formatted(zamundaCredentials.getUsername()));
        String cookie = webClientService.loginAndGetCookie(zamundaLoginPage,
                "username",
                "password",
                zamundaCredentials);
        if(!cookie.contains("uid")) // TODO: Add custom exception
            throw new RuntimeException("Login failed!");

        LOG.info("Login successful!");

        // Load the Zamunda Top10 torrents table
        LOG.info("Looking for a movie to download...");
        String html = webClientService.getWebsiteContents(zamundaCatalogURL, cookie);
        Document doc = Jsoup.parse(html);
        var tableContentTableRows = doc.select("#div1 > table > tbody > tr").stream().skip(1).map(TableRow::new);

        // Filter out the table elements that don't meet the system's requirements
        var filtered = tableContentTableRows
                .filter(t -> t.typeOk) // Torrent type filter
                // TODO Add more filters for the other restrictions
                .toList();

        // Pick a random torrent from the filtered table
        TableRow chosenMovieTableRow = filtered.stream()
                .toList().get(new Random().nextInt(filtered.size()));
        URL url = new URL(zamundaURL + chosenMovieTableRow.link);
        LOG.info("And we have a winner! ( %s )".formatted(url));

        // Return the torrent's review page link
        return url;
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
