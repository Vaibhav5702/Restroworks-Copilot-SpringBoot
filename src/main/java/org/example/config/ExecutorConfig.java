package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Value("${executor.threads.count}")
    private int executorThreadsCount;

    @Bean
    public ExecutorService executorService() {
        // This method creates and returns an ExecutorService instance with a fixed thread pool.
        // The number of threads is configured using the 'executor.threads.count' property.
        return Executors.newFixedThreadPool(executorThreadsCount);
    }
}
