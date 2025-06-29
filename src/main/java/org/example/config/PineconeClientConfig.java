package org.example.config;

import io.pinecone.clients.Index;
import io.pinecone.configs.PineconeConfig;
import io.pinecone.configs.PineconeConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PineconeClientConfig {

    @Value("${pinecone.db.key}")
    private String pineconeKey;

    @Value("${pinecone.db.host}")
    private String pineconeHost;

    @Value("${pinecone.db.index}")
    private String pineconeIndex;

    @Bean
    public Index pineconeIndex() {
        // This method creates and returns a Pinecone client instance.
        // You can customize the client configuration as needed.
        PineconeConfig config =  new PineconeConfig(pineconeKey);
        config.setHost(pineconeHost);
        PineconeConnection connection = new PineconeConnection(config);
        return new Index(config,connection, pineconeIndex);
    }

}
