package com.vsl700.nitflex.services;

import com.vsl700.nitflex.models.User;

public interface MovieLoaderService {
    void load(String initialPath); // Movie.parentPath may be different from initialPath if folders are nested
    void load(String initialPath, User requester); // When the server adds a Movie requested by a User
}
