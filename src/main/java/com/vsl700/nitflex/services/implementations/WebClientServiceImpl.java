package com.vsl700.nitflex.services.implementations;

import com.vsl700.nitflex.components.WebsiteCredentials;
import com.vsl700.nitflex.services.WebClientService;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.atomic.AtomicReference;

public class WebClientServiceImpl implements WebClientService {

    private WebsiteCredentials.Zamunda zamundaCredentials;

    public WebClientServiceImpl(WebsiteCredentials.Zamunda zamundaCredentials){
        this.zamundaCredentials = zamundaCredentials;
    }

    @Override
    public String loginAndGetCookie(String url) {
        WebClient client = WebClient.builder()
                .baseUrl(url)
                .build();

        AtomicReference<String> result = new AtomicReference<>();
        client.post()
                .attribute("username", zamundaCredentials.getUsername())
                .attribute("password", zamundaCredentials.getPassword())
                .exchangeToMono(response -> {
                    result.set(String.join(";", response.headers().header("Cookie")));
                    return response.bodyToMono(Void.class);
                }).block();

        return result.get();
    }

    @Override
    public String getWebsiteContents(String url, String cookie) {
        WebClient client = WebClient.builder()
                .baseUrl(url)
                .defaultHeader("Cookie", cookie)
                .build();

        return client.get()
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
