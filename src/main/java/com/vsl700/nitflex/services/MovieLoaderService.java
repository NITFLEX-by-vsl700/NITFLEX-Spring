package com.vsl700.nitflex.services;

import com.vsl700.nitflex.models.User;

import java.nio.file.Path;

public interface MovieLoaderService {
    void load(Path initialPath); // Loads a concrete Movie. Used when server loads a Movie automatically
    void load(Path initialPath, User requester); // Loads a concrete Movie. Used when a User requested the Movie
    void loadNewlyAdded(); // Scans the whole movies folder and registers (loads) all movies that are not registered
    void unloadNonExisting();  // Scans the whole movies folder and unloads all movies that are deleted from the folder
}
