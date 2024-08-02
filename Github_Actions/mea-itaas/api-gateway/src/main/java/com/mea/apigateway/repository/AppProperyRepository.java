package com.mea.apigateway.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mea.apigateway.model.AppProperty;

public interface AppProperyRepository extends MongoRepository<AppProperty,String>{

}
