package com.vsl700.nitflex.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vsl700.nitflex.models.User;

public interface UserRepository extends MongoRepository<User, String> {
    
}
