package com.vsl700.nitflex.services;

public interface WebClientService {
    String loginAndGetCookie(String url);
    String getWebsiteContents(String url, String cookie);
}
