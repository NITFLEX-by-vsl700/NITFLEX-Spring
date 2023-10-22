package com.vsl700.nitflex.services;

public interface MovieLoaderService {
    void load(String initialPath); // Movie.parentPath may be different from initialPath if folders are nested
}
