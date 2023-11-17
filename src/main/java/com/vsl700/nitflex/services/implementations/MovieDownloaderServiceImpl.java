package com.vsl700.nitflex.services.implementations;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import com.vsl700.nitflex.components.InitialMoviesLoader;
import com.vsl700.nitflex.components.SharedProperties;
import com.vsl700.nitflex.components.WebsiteCredentials;
import com.vsl700.nitflex.services.MovieDownloaderService;
import com.vsl700.nitflex.services.WebClientService;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class MovieDownloaderServiceImpl implements MovieDownloaderService {
    private WebClientService webClientService;
    private SharedProperties sharedProperties;
    private WebsiteCredentials.Zamunda zamundaCredentials;

    private static final Logger LOG = LoggerFactory.getLogger(InitialMoviesLoader.class);

    private static final String zamundaLoginPage = "https://zamunda.net/takelogin.php";

    public MovieDownloaderServiceImpl(WebClientService webClientService, SharedProperties sharedProperties, WebsiteCredentials.Zamunda zamundaCredentials) {
        this.webClientService = webClientService;
        this.sharedProperties = sharedProperties;
        this.zamundaCredentials = zamundaCredentials;
    }

    @SneakyThrows
    @Override
    public void downloadFromPageURL(String pageUrl) { // Zamunda.NET implementation
        LOG.info("Downloading from URL: '%s'".formatted(pageUrl));

        // Login and get necessary cookie
        LOG.info("Logging in as '%s'...".formatted(zamundaCredentials.getUsername()));
        String cookie = webClientService.loginAndGetCookie(zamundaLoginPage,
                "username",
                "password",
                zamundaCredentials);
        if(!cookie.contains("uid")) // TODO: Add custom exception
            throw new RuntimeException("Login failed!");

        LOG.info("Login successful!");

        // Look for the .torrent file download link
        LOG.info("Looking for the .TORRENT file download link...");
        URI tempURI = new URI(pageUrl);
        String scheme = tempURI.getScheme();
        String host = tempURI.getHost();
        String downloadLinkPath = null;
        String torrentFileName;
        do { // While we are not at the page with the .torrent file download link
            String html = webClientService.getWebsiteContents(downloadLinkPath == null ? pageUrl : scheme + "://" + host + downloadLinkPath, cookie);
            Document doc = Jsoup.parse(html);
            Element downloadLinkElement = doc.selectFirst("a.index.notranslate");

            assert downloadLinkElement != null;
            torrentFileName = "%s.torrent".formatted(downloadLinkElement.text());
            downloadLinkPath = downloadLinkElement.attr("href");
            if(!downloadLinkPath.startsWith("/"))
                downloadLinkPath = "/%s".formatted(downloadLinkPath);
        } while(!downloadLinkPath.endsWith(".torrent"));

        // Download .torrent file content
        LOG.info("Downloading .TORRENT file...");
        byte[] fileContent = webClientService.getContentsAsByteArray(scheme + "://" + host + downloadLinkPath, cookie);

        // Make a path for the new file
        Path torrentFilePath = Paths.get(sharedProperties.getMoviesFolder(), torrentFileName);

        // Save torrent file
        Files.write(torrentFilePath, fileContent);

        LOG.info(".TORRENT file downloaded!");

        // Start downloading with the new torrent file
        downloadFromTorrentFilePath(torrentFilePath.toString());
    }

    @Override
    public void downloadFromTorrentFilePath(String torrentFilePath) {
        var client = createClient(torrentFilePath);
        client.download();

        client.waitForCompletion();
        client.stop();

        // If the torrent was a 'single-file' and the downloaded video file is in the parent folder, we should move it
        // from the parent folder and put it in its own folder (following the MovieLoaderService's policy)
        if(!client.getTorrent().isMultifile()){
            String fileName = client.getTorrent().getFilenames()
                    .stream().findFirst().orElseThrow();

            if(Path.of(fileName).getNameCount() > 1)
                return;

            // If the video file is not in its own folder
            String movieFolderName = fileName.substring(0, fileName.lastIndexOf('.'));

            File parentDir = new File(sharedProperties.getMoviesFolder());

            File movieFolder = new File(parentDir, movieFolderName);
            if (!movieFolder.mkdir()) // TODO: Add custom exception
                throw new RuntimeException("Folder %s could not be created!".formatted(movieFolder.getAbsolutePath()));

            File targetFile = new File(parentDir, fileName);
            try {
                Files.move(Path.of(targetFile.getAbsolutePath()), Path.of(movieFolder.getAbsolutePath(), fileName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SneakyThrows
    private Client createClient(String torrentFilePath){
        File parentDir = new File(sharedProperties.getMoviesFolder());
        if(!parentDir.exists() && !parentDir.mkdir()) {
            // TODO: Add custom exception
            throw new RuntimeException("Access to %s denied!".formatted(parentDir.getAbsolutePath()));
        }

        return new Client(InetAddress.getLocalHost(), SharedTorrent.fromFile(
                new File(torrentFilePath),
                parentDir
        ));
    }
}
