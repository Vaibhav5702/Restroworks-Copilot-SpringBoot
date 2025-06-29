package org.example.service;
import com.cohere.api.Cohere;
import com.cohere.api.resources.v2.requests.V2EmbedRequest;
import com.cohere.api.types.EmbedByTypeResponse;
import com.cohere.api.types.EmbedInputType;
import com.cohere.api.types.EmbeddingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Embeddings {

    @Autowired
    private Cohere cohere;

    public List<List<Double>> generateEmbeddings(List<String> text) throws Exception {
        // This method generates an embedding using the Cohere API.
        // You can customize the parameters as needed.

        V2EmbedRequest req = V2EmbedRequest.builder()
                .model("embed-english-v3.0")
                .inputType(EmbedInputType.CLASSIFICATION)
                .texts(text)
                .embeddingTypes(List.of(EmbeddingType.FLOAT))
                .build();

        EmbedByTypeResponse res = cohere.v2().embed(req);
        return res.getEmbeddings().getFloat().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Embedding generation failed"));
    }
}
