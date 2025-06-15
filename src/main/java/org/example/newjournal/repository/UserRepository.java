package org.example.newjournal.repository;

import org.bson.types.ObjectId;
import org.example.newjournal.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {
    public User findByUserName(String userName);
}
