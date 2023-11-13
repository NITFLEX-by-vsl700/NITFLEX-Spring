package com.vsl700.nitflex.services;

import com.vsl700.nitflex.components.WebsiteCredentials;

public interface WebClientService {
    String loginAndGetCookie(String url, WebsiteCredentials websiteCredentials);
    String getWebsiteContents(String url, String cookie);
}
