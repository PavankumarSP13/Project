package com.mea.apigateway.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.mea.apigateway.model.LoginStatus;


public interface LoginSessionRepository extends MongoRepository<LoginStatus,String> {

	@Query(" {sessionId: ?0}")
	LoginStatus getLoginStatusBySessionId(String sessionId);
}

