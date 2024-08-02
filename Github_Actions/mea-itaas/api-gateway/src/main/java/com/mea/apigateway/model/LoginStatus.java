package com.mea.apigateway.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="login_status")
public class LoginStatus {
	
    @Id
    private String id;
    @Field("login_time")
    private Date loginTime;
    @Field("session_id")
    private String sessionId;
    @Field("full_name")
    private String fullName;
    @Field("first_name")
    private String firstName;
    @Field("last_name")
    private String lastName;
    @Field("user_id")
    private String userId;
    private String token;
    private String avatarLink;
    
    @Field("last_used_time")
    private Date lastUsedTime;
    
    @Field("is_session_expired")
    private boolean isSessionExpired;

    private String type;

    private Boolean active;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    public String getFullName() {
        return fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

	public Date getLastUsedTime() {
		return lastUsedTime;
	}

	public void setLastUsedTime(Date lastUsedTime) {
		this.lastUsedTime = lastUsedTime;
	}

	public boolean isSessionExpired() {
		return isSessionExpired;
	}

	public void setSessionExpired(boolean isSessionExpired) {
		this.isSessionExpired = isSessionExpired;
	}
    
}
