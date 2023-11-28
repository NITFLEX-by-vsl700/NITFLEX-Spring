package com.vsl700.nitflex.services;

import java.net.URI;

/**
 * Looks for a new Movie to be downloaded
 */
public interface MovieSeekerService {
    /**
     * Looks for a new Movie to be downloaded and returns the URL to its page
     * @return the link to a new movie's page
     */
    URI findMovieURL();
}
