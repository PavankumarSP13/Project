package com.mea.apigateway.model;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
//import org.jboss.logging.Field;

import java.util.Date;

/**
 * @author parakh
 *
 */
public class Audit {

	private String url;
	private String ipAddress;
	private Date apiInvokedTime;
	private String userAgent;
	private String userId;
	private String group;
	private String payload;
	private String apiTag;
	private String tagDisplayName;
	private String sessionToken;
	private String queryString;

	/**
	 * Gets apiTag.
	 * @return Value of apiTag.
	 */
	public String getApiTag() {
		return apiTag;
	}

	/**
	 * Sets new ipAddress.
	 * @param ipAddress New value of ipAddress.
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * Sets new apiTag.
	 * @param apiTag New value of apiTag.
	 */
	public void setApiTag(String apiTag) {
		this.apiTag = apiTag;
	}


	/**
	 * Gets ipAddress.
	 * @return Value of ipAddress.
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * Gets userId.
	 * @return Value of userId.
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Sets new userAgent.
	 * @param userAgent New value of userAgent.
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	/**
	 * Gets group.
	 * @return Value of group.
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * Sets new url.
	 * @param url New value of url.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets url.
	 * @return Value of url.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets new group.
	 * @param group New value of group.
	 */
	public void setGroup(String group) {
		this.group = group;
	}


	/**
	 * Sets new apiInvokedTime.
	 * @param apiInvokedTime New value of apiInvokedTime.
	 */
	public void setApiInvokedTime(Date apiInvokedTime) {
		this.apiInvokedTime = apiInvokedTime;
	}

	/**
	 * Sets new userId.
	 * @param userId New value of userId.
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * Sets new payload.
	 * @param payload New value of payload.
	 */
	public void setPayload(String payload) {
		this.payload = payload;
	}

	/**
	 * Gets apiInvokedTime.
	 * @return Value of apiInvokedTime.
	 */
	public Date getApiInvokedTime() {
		return apiInvokedTime;
	}

	/**
	 * Gets payload.
	 * @return Value of payload.
	 */
	public String getPayload() {
		return payload;
	}

	/**
	 * Gets userAgent.
	 * @return Value of userAgent.
	 */
	public String getUserAgent() {
		return userAgent;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	/**
	 * Gets sessionToken.
	 * @return Value of sessionToken.
	 */
	public String getSessionToken() {
		return sessionToken;
	}

	/**
	 * Sets new sessionToken.
	 * @param sessionToken New value of sessionToken.
	 */
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	/**
	 * Gets queryString.
	 * @return Value of queryString.
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * Sets new queryString.
	 * @param queryString New value of queryString.
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	/**
	 * Gets tagDisplayName.
	 * @return Value of tagDisplayName.
	 */
	public String getTagDisplayName() {
		return tagDisplayName;
	}

	/**
	 * Sets new tagDisplayName.
	 * @param tagDisplayName New value of tagDisplayName.
	 */
	public void setTagDisplayName(String tagDisplayName) {
		this.tagDisplayName = tagDisplayName;
	}
}
