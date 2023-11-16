package com.vsl700.nitflex.services;

import com.vsl700.nitflex.components.WebsiteCredentials;

public interface WebClientService {
    String loginAndGetCookie(String url, String usernameAttr, String passwordAttr, WebsiteCredentials websiteCredentials);

    /**
     * Makes a request to the specified url and returns its response body as a String. Convenient when parsing HTML
     * @param url
     * @param cookie
     * @return response body content as a String
     */
    String getWebsiteContents(String url, String cookie);

    /**
     * Makes a request to the specified url and returns its response body as a byte array.
     * Recommended for downloading files
     * @param url
     * @param cookie
     * @return response body content as bytes
     */
    byte[] getContentsAsByteArray(String url, String cookie);
}
