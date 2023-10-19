package com.vsl700.nitflex.repo;

import com.vsl700.nitflex.models.Episode;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EpisodeRepository extends MongoRepository<Episode, String> {
}
