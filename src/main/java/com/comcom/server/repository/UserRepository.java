package com.comcom.server.repository;

import com.comcom.server.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserRepository extends MongoRepository<User, ObjectId> {

        public User findFirstByUsername(String username);

        public User findFirstByToken(String token);

        public User findFirstByEmail(String email);

        @Query(value = "{ 'notificationDetails.detailslist.deviceId' : ?0 }")
        List<User> findbyDeviceId(String deviceId);

        @Query(value = "{}",fields ="{'notificationDetails.detailslist.deviceId' : 1 }" )
        List<User> findAllDeviceId();

}
