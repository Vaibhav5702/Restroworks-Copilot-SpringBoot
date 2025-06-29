package org.example.config;

import com.cohere.api.Cohere;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CohereConfig {
    // This class can be used to configure Cohere API settings.
    // You can define beans or methods here to set up the Cohere client,
    // handle API keys, and manage other configurations related to Cohere.
    @Value("${cohere.api.key}")
    private String apiKey;
    // Example method to configure Cohere client (this is just a placeholder)
     @Bean
     public Cohere cohereClient() {
         return Cohere.builder()
                 .token(apiKey)
                 .build();
     }
}
