package com.mea.apigateway;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableDiscoveryClient
@SpringBootApplication
@EnableAsync
public class ApiGatewayApplication {
	

	@Value("${threadPoolExecutor.corePoolSize}")
	private int corePoolSize;
	@Value("${threadPoolExecutor.maxPoolSize}")
	private int maxPoolSize;
	@Value("${threadPoolExecutor.queueCapacity}")
	private int queueCapacity;
	
	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}
	
	@Bean(name = "threadPoolExecutor")
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueCapacity);
		executor.initialize();
		return executor;
	}
}
