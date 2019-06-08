package com.comcom.server.repository;

import com.comcom.server.entity.Infos;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InfosRepository extends MongoRepository<Infos, ObjectId> {


}
