package com.vsl700.nitflex.repo;

import com.vsl700.nitflex.models.Movie;
import com.vsl700.nitflex.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends MongoRepository<Movie, String> {
    List<Movie> findAllByType(Movie.MovieType type);
    Optional<Movie> findByPath(String path);
    List<Movie> findAllByRequester(User user);
}
