package com.vsl700.nitflex.services.implementations;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import com.vsl700.nitflex.components.InitialMoviesLoader;
import com.vsl700.nitflex.components.SharedProperties;
import com.vsl700.nitflex.components.WebsiteCredentials;
import com.vsl700.nitflex.exceptions.InvalidDownloadPageException;
import com.vsl700.nitflex.exceptions.InvalidTorrentException;
import com.vsl700.nitflex.exceptions.MovieTorrentingException;
import com.vsl700.nitflex.exceptions.WebClientLoginException;
import com.vsl700.nitflex.services.MovieDownloaderService;
import com.vsl700.nitflex.services.WebClientService;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class MovieDownloaderServiceImpl implements MovieDownloaderService {
    private WebClientService webClientService;
    private SharedProperties sharedProperties;
    private WebsiteCredentials.Zamunda zamundaCredentials;

    private static final Logger LOG = LoggerFactory.getLogger(MovieDownloaderServiceImpl.class);

    private static final String zamundaLoginPage = "https://zamunda.net/takelogin.php";

    public MovieDownloaderServiceImpl(WebClientService webClientService, SharedProperties sharedProperties, WebsiteCredentials.Zamunda zamundaCredentials) {
        this.webClientService = webClientService;
        this.sharedProperties = sharedProperties;
        this.zamundaCredentials = zamundaCredentials;
    }

    @SneakyThrows
    @Override
    public String downloadFromPageURL(URL page) { // Zamunda.NET implementation
        LOG.info("Downloading from URL: '%s'".formatted(page));

        // Login and get necessary cookie
        LOG.info("Logging in as '%s'...".formatted(zamundaCredentials.getUsername()));
        String cookie = webClientService.loginAndGetCookie(zamundaLoginPage,
                "username",
                "password",
                zamundaCredentials);
        if(!cookie.contains("uid"))
            throw new WebClientLoginException();

        LOG.info("Login successful!");

        // Look for the .torrent file download link
        LOG.info("Looking for the .TORRENT file download link...");
        String pageUrl = page.toString();
        String scheme = page.getProtocol();
        String host = page.getHost();
        String downloadLinkPath = null;
        String torrentFileName;
        do { // While we are not at the page with the .torrent file download link
            String html = webClientService.getWebsiteContents(downloadLinkPath == null ? pageUrl : scheme + "://" + host + downloadLinkPath, cookie, Charset.forName("windows-1251"));
            Document doc = Jsoup.parse(html);

            // If this is the first iteration, check torrent type (whether it's a movie) & movie size
            if(downloadLinkPath == null){
                // Size check
                String sizeStr = Objects.requireNonNull(doc.selectFirst("body > div.content-position > div > table > tbody > tr:nth-child(1) > td > table > tbody > tr > td > table > tbody > tr:nth-child(2) > td > table > tbody > tr:nth-child(12) > td:nth-child(2)"))
                        .text();
                String[] sizeStrParts = sizeStr.split(" ");
                float size = sizeStrParts[1].equals("MB") ? Float.parseFloat(sizeStrParts[0]) / 1024 : Float.parseFloat(sizeStrParts[0]);
                if(sharedProperties.getMovieSizeLimit() != -1 && size > sharedProperties.getMovieSizeLimit())
                    throw new InvalidTorrentException("Requested movie exceeds the size limit!");

                // Type check
                String typeStr = Objects.requireNonNull(doc.selectFirst("body > div.content-position > div > table > tbody > tr:nth-child(1) > td > table > tbody > tr > td > table > tbody > tr:nth-child(2) > td > table > tbody > tr:nth-child(8) > td:nth-child(2)"))
                        .text();
                switch (typeStr){
                    case "Филми/SD":
                    case "Филми/HD":
                    case "Анимации/Аниме":
                    case "Сериали":
                    case "Сериали/HD":
                    case "Сериали/Русия":
                    case "Филми/DVD-R":
                    case "Филми/Русия":
                    case "Филми/БГ":
                    case "Филми/Документални":
                    case "Blu-ray":
                    case "Филми/3D": break;
                    default: throw new InvalidTorrentException("Invalid torrent type: '%s'".formatted(typeStr));
                }
            }

            // Find the download link
            Element downloadLinkElement = doc.selectFirst("a.index.notranslate");
            if(downloadLinkElement == null)
                throw new InvalidDownloadPageException();
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
        return downloadFromTorrentFilePath(torrentFilePath.toFile());
    }

    @Override
    public String downloadFromTorrentFilePath(File torrentFile) {
        var client = createClient(torrentFile);
        client.download();

        client.waitForCompletion();
        client.stop();

        String resultMovieFolder = Path.of(sharedProperties.getMoviesFolder(), client.getTorrent().getName()).toString();

        // If the torrent was a 'single-file' and the downloaded video file is in the parent folder, we should move it
        // from the parent folder and put it in its own folder (following the MovieLoaderService's policy)
        if(!client.getTorrent().isMultifile()){
            String fileName = client.getTorrent().getFilenames()
                    .stream().findFirst().orElseThrow();

            if(Path.of(fileName).getNameCount() > 1)
                return resultMovieFolder;

            // If the video file is not in its own folder
            String movieFolderName = fileName.substring(0, fileName.lastIndexOf('.'));

            File parentDir = new File(sharedProperties.getMoviesFolder());

            File movieFolder = new File(parentDir, movieFolderName);
            if (!movieFolder.mkdir())
                throw new MovieTorrentingException("Folder %s could not be created!".formatted(movieFolder.getAbsolutePath()));

            File targetFile = new File(parentDir, fileName);
            try {
                Files.move(Path.of(targetFile.getAbsolutePath()), Path.of(movieFolder.getAbsolutePath(), fileName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            resultMovieFolder = movieFolder.getAbsolutePath();
        }

        return resultMovieFolder;
    }

    @SneakyThrows
    private Client createClient(File torrentFile){
        File parentDir = new File(sharedProperties.getMoviesFolder());
        if(!parentDir.exists() && !parentDir.mkdir()) {
            throw new MovieTorrentingException("Access to %s denied!".formatted(parentDir.getAbsolutePath()));
        }

        return new Client(InetAddress.getLocalHost(), SharedTorrent.fromFile(
                torrentFile,
                parentDir
        ));
    }
}
