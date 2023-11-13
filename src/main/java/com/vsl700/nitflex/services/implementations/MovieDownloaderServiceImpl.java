package com.vsl700.nitflex.services.implementations;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import com.vsl700.nitflex.components.SharedProperties;
import com.vsl700.nitflex.services.MovieDownloaderService;
import com.vsl700.nitflex.services.WebClientService;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.InetAddress;
import java.nio.file.Paths;

@Service
public class MovieDownloaderServiceImpl implements MovieDownloaderService {
    private WebClientService webClientService;
    private SharedProperties sharedProperties;

    private static final String zamundaLoginPage = "https://zamunda.net/takelogin.php";

    public MovieDownloaderServiceImpl(WebClientService webClientService, SharedProperties sharedProperties) {
        this.webClientService = webClientService;
        this.sharedProperties = sharedProperties;
    }

    @SneakyThrows
    @Override
    public void downloadFromPageURL(String pageUrl) {
        // Login and get necessary cookie
        String cookie = webClientService.loginAndGetCookie(zamundaLoginPage);

        // Look for the .torrent file download link
        String downloadLink = null;
        String torrentFileName; // TODO: Think of a more legit way to set the torrentFileName
        do { // While we are not at the page with the .torrent file download link
            String html = webClientService.getWebsiteContents(downloadLink == null ? pageUrl : pageUrl + downloadLink, cookie);
            Document doc = Jsoup.parse(html);
            Element downloadLinkElement = doc.selectFirst("a.index.notranslate");

            assert downloadLinkElement != null;
            torrentFileName = "%s.torrent".formatted(downloadLinkElement.text());
            downloadLink = downloadLinkElement.attr("href");
        } while(!downloadLink.endsWith(".torrent"));

        // Download .torrent file content
        String fileContent = webClientService.getWebsiteContents(pageUrl + downloadLink, cookie);

        // Make a path for the new file
        String torrentFilePath = Paths.get(sharedProperties.getMoviesFolder(), torrentFileName).toString();

        // Save torrent file
        BufferedWriter writer = new BufferedWriter(new FileWriter(torrentFilePath));
        writer.write(fileContent);
        writer.close();

        // Start downloading with the new torrent file
        downloadFromTorrentFilePath(torrentFilePath);
    }

    @Override
    public void downloadFromTorrentFilePath(String torrentFilePath) {
        var client = createClient(torrentFilePath);
        client.download();

        new Thread(() -> {
            while(!client.getState().equals(Client.ClientState.DONE)
                    && !client.getState().equals(Client.ClientState.ERROR)){
                client.info(); // It's not bad to keep some logs from the download
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            client.stop();
        }).start();
    }

    @SneakyThrows
    private Client createClient(String torrentFilePath){
        File parentDir = new File(sharedProperties.getMoviesFolder());
        if(!parentDir.exists() && !parentDir.mkdir()) {
            // TODO: Add exception handling
            throw new RuntimeException("Access to %s denied!".formatted(parentDir.getAbsolutePath()));
        }

        return new Client(InetAddress.getLocalHost(), SharedTorrent.fromFile(
                new File(torrentFilePath),
                parentDir
        ));
    }
}
