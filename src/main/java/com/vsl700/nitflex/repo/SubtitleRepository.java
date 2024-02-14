package com.vsl700.nitflex.repo;

import com.vsl700.nitflex.models.Subtitle;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubtitleRepository extends MongoRepository<Subtitle, String> {
    void deleteByMovieId(String id);
}
