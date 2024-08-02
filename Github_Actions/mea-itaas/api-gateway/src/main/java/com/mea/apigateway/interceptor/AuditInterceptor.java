package com.mea.apigateway.interceptor;

import com.mea.apigateway.model.AppProperty;
import com.mea.apigateway.model.Audit;
import com.mea.apigateway.model.LoginStatus;
import com.mea.apigateway.repository.AppProperyRepository;
import com.mea.apigateway.repository.LoginSessionRepository;
import com.mea.apigateway.util.AsyncExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuditInterceptor implements GlobalFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditInterceptor.class);

    @Autowired
    private AsyncExecutor asyncExecutor;

    @Autowired
    private LoginSessionRepository loginStatusRepository;

    @Autowired
    private AppProperyRepository appPropertyRepository;

    private String sessionExpireAt = "15";
    private Long SESSION_EXPIRY_TIME;
    private final Map<String, LoginStatus> expiryMap = new ConcurrentHashMap<>();

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        new Thread(() -> {
            try {
                Thread.sleep(25000);
            } catch (InterruptedException e) {
                LOGGER.error("Initialization thread interrupted: {}", e.getMessage());
            }
            initializeAuditInterceptor();
        }).start();
    }

    private void initializeAuditInterceptor() {
        try {
            List<AppProperty> appProperties = appPropertyRepository.findAll();
            if (appProperties != null && !appProperties.isEmpty()) {
                AppProperty property = appProperties.get(0);
                sessionExpireAt = property.getSessionTimeout();
            }
            SESSION_EXPIRY_TIME = Duration.ofMinutes(Long.parseLong(sessionExpireAt)).toMillis();
        } catch (Exception e) {
            LOGGER.error("Error initializing AuditInterceptor: {}", e.getMessage());
        }
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // Handle preflight requests
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        LOGGER.info("Api triggered: {}", request.getPath());

        String jsessionId = request.getHeaders().getFirst("jsessionid");

        if (jsessionId == null) {
            // Check if the request does not require authentication
            if (isUnauthenticatedRequest(request)) {
                return chain.filter(exchange).doFinally(signal -> persistAudit(request));
            } else {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        } else {
            // Perform session validation
            if (isSessionValid(jsessionId)) {
                return chain.filter(exchange).doFinally(signal -> persistAudit(request));
            } else {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        }
    }

    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange).doFinally(signal -> {
            if (exchange.getResponse().getStatusCode().is2xxSuccessful()) {
                // Logic to set expiry map after successful request
                setExpiryMap(exchange.getRequest());
            }
        });
    }

    private void setExpiryMap(ServerHttpRequest request) {
        String uri = request.getURI().getPath();
        // Check if the request URI indicates a successful login
        if (uri.contains("login")) {
            List<LoginStatus> loggedInSession = asyncExecutor.getLoggedInSessions();
            if (loggedInSession != null) {
                for (LoginStatus loginStatus : loggedInSession) {
                    expiryMap.put(loginStatus.getSessionId(), loginStatus);
                }
            }
        }
    }


    private boolean isUnauthenticatedRequest(ServerHttpRequest request) {
        String uri = request.getURI().getPath();
        // Check if the request URI does not require authentication
        if (uri.contains("login") || uri.contains("logout") ||
                uri.contains("updatedocusignstatus") ||
                uri.contains("avatar") || uri.contains("export") ||
                uri.contains("studentPic") || uri.contains("download") ||
                uri.contains("pdfs") || uri.contains("docusignreport") ||
                (uri.contains("forms") && uri.contains("envelopes"))) {
            persistAudit(request);
            return true;
        } else {
            return false;
        }
    }

    public void persistAudit(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        LOGGER.info("Request Headers: {}", headers);
        String apiTag = headers.getFirst("api-tag");
        String userId = headers.getFirst("userId");

        if (!"login".equalsIgnoreCase(apiTag) && !StringUtils.hasText(userId)) {
            LOGGER.error("User-Id cannot be null or empty in the header.");
        } else {
            Audit audit = populateAuditParams(request);
            LOGGER.debug("Audit data: {}", audit);
            asyncExecutor.invokeAuditService(audit);
        }
    }

    /**
     * Populate Audit Object from request Object.
     *
     * @param request
     * @return Audit
     * @throws Exception
     */
    private Audit populateAuditParams(ServerHttpRequest request) {
        Audit audit = new Audit();
        String requestURL = request.getURI().toString();
        LOGGER.debug("Requested URL: {}", requestURL);
        audit.setUrl(requestURL);
        audit.setQueryString(request.getURI().getQuery());
        audit.setUserAgent(request.getHeaders().getFirst(HttpHeaders.USER_AGENT));
        audit.setApiTag(request.getHeaders().getFirst("api-tag"));

        DateFormat df = new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss");
        String today = df.format(Calendar.getInstance().getTime());

        try {
            audit.setApiInvokedTime(df.parse(today));
        } catch (Exception e) {
            LOGGER.error("Error parsing date: {}", e.getMessage());
        }

        audit.setUserId(request.getHeaders().getFirst("userId"));
        audit.setSessionToken(request.getHeaders().getFirst("token"));
        audit.setTagDisplayName(request.getHeaders().getFirst("display-name"));
        audit.setIpAddress(getClientIpAddress(request));
        audit.setGroup("Email-Blast");
        return audit;
    }

    private String getClientIpAddress(ServerHttpRequest request) {
        return request.getRemoteAddress().getAddress().getHostAddress();
    }

    private boolean isSessionValid(String jsessionId) {
        if (expiryMap != null) {
            // Check if the jsessionId exists in the expiryMap
            if (expiryMap.containsKey(jsessionId)) {
                LoginStatus loginStatus = expiryMap.get(jsessionId);
                Date dateUsed = loginStatus.getLastUsedTime() == null ? loginStatus.getLoginTime() : loginStatus.getLastUsedTime();
                // Check if the session is expired based on the configured session expiry time
                if ((System.currentTimeMillis() - dateUsed.getTime()) > SESSION_EXPIRY_TIME) {
                    asyncExecutor.updateSessionLoginStatus(loginStatus); // Call logout API
                    return false;
                } else {
                    // Update the last used time of the session and update the expiryMap
                    loginStatus.setLastUsedTime(new Date());
                    expiryMap.put(jsessionId, loginStatus);
                    return true;
                }
            } else {
                // If the jsessionId is not found in the expiryMap, retrieve the session from the database
                LoginStatus loginStatus = loginStatusRepository.getLoginStatusBySessionId(jsessionId);
                if (loginStatus != null) {
                    Date dateUsed = loginStatus.getLastUsedTime() == null ? loginStatus.getLoginTime() : loginStatus.getLastUsedTime();
                    // Check if the session is expired based on the configured session expiry time
                    if ((System.currentTimeMillis() - dateUsed.getTime()) > SESSION_EXPIRY_TIME) {
                        asyncExecutor.updateSessionLoginStatus(loginStatus); // Call logout API
                        return false;
                    } else {
                        // Update the last used time of the session and update the expiryMap
                        loginStatus.setLastUsedTime(new Date());
                        expiryMap.put(jsessionId, loginStatus);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
