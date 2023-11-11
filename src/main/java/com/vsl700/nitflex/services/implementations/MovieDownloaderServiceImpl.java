package com.vsl700.nitflex.services.implementations;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import com.vsl700.nitflex.components.SharedProperties;
import com.vsl700.nitflex.services.MovieDownloaderService;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;

@Service
public class MovieDownloaderServiceImpl implements MovieDownloaderService {

    private SharedProperties sharedProperties;

    public MovieDownloaderServiceImpl(SharedProperties sharedProperties) {
        this.sharedProperties = sharedProperties;
    }

    @SneakyThrows
    @Override
    public void downloadFromPageURL(String pageUrl) {
        Document doc = Jsoup.parse(new URL(pageUrl), 10000);
        doc.select(".index.notranslate");
    }

    @Override
    public void download(String torrentFilePath) {
        var client = createClient(torrentFilePath);
        client.download();

        new Thread(() -> {
            while(!client.getState().equals(Client.ClientState.DONE) && !client.getState().equals(Client.ClientState.ERROR)){
                client.info();
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
