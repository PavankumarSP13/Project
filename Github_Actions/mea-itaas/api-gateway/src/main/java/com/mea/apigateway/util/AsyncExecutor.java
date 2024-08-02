package com.mea.apigateway.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.mea.apigateway.model.Audit;
import com.mea.apigateway.model.LoginStatus;
import com.mea.apigateway.repository.LoginSessionRepository;

@Component
public class AsyncExecutor{

	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncExecutor.class);
	
	@Value("${auditServiceURL}")
	private String auditServiceURL;
	
	@Value("${logoutURL}")
	private String logoutURL;
	
	@Value("${loggedInSessionURL}")
	private String loggedInSessionURL;
	
	
	@Autowired
    private LoginSessionRepository loginStatusRepository;

	@Async("threadPoolExecutor")
	public void invokeAuditService(Audit audit){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		LOGGER.debug("invoking audit service == {}", auditServiceURL);
		
		HttpEntity<Audit> request = new HttpEntity<Audit>(audit, headers);
		RestTemplate restTemplate = new RestTemplate();		
		try {
			ResponseEntity<Audit> response = restTemplate.exchange(auditServiceURL, HttpMethod.POST, request, Audit.class);
			HttpStatus statusCode = (HttpStatus) response.getStatusCode();
			if(statusCode.value() == 200){
				LOGGER.debug("Successfully saved audit == {}", audit);
			}else{
				LOGGER.error("Audit Service error == {}", response);
			}
		} catch (Exception e) {
			LOGGER.error("Exception occured while invoking audit-service == {}" , e.getMessage());
		}
		
	}
	
	public List<LoginStatus> getLoggedInSessions() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		LOGGER.debug("invoking audit service == {}", loggedInSessionURL);

		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(loggedInSessionURL);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<List<LoginStatus>> response = new RestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,  
					new ParameterizedTypeReference<List<LoginStatus>>() {
            });
			if(response.getStatusCode() == HttpStatus.OK){
				return response.getBody();
			}
		} catch (Exception ex) {
			LOGGER.error("Exception occured while invoking getLoggedInSessions == {}", ex);
		}
		return null;
	}	
	
	@Async("threadPoolExecutor")
	public void updateSessionLoginStatus(LoginStatus loginStatus){
		try {
			invokeGet(logoutURL+"?sessionId="+ loginStatus.getSessionId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void invokeGet(String url) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();
		LOGGER.info("Response code is {}" , responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		LOGGER.debug("Response: {}",response.toString());

	}
}