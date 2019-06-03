package com.comcom.server.repository;

import com.comcom.server.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, ObjectId> {

        public User findFirstByUsername(String username);

}
