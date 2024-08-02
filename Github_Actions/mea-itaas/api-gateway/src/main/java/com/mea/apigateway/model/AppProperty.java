package com.mea.apigateway.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="app_properties")
public class AppProperty {

	@Field("session_timeout")
	private String sessionTimeout;

	public String getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(String sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	
	
}
