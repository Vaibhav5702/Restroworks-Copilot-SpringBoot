package org.example.service;

import com.google.protobuf.Struct;
import io.pinecone.clients.Index;
import io.pinecone.unsigned_indices_model.VectorWithUnsignedIndices;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UploadVector {

    private final Index index;

    public UploadVector(Index index) {
        this.index = index;
    }

    public void uploadVectorToPineCone(List<List<Double>> embeddings, String id, Struct metadata) throws RuntimeException {
        // This method uploads the generated embeddings to Pinecone.
        // You can customize the upload logic as needed.
        try {
            index.upsert(id, embeddings.get(0).stream().map(Double::floatValue).toList()
                    , null,null, metadata,"test");
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload vector to Pinecone: " + e.getMessage(), e);
        }

    }

    public void uploadVectorBatchesToPineCone(List<VectorWithUnsignedIndices> vectors) throws RuntimeException {
        // This method uploads the generated embeddings to Pinecone.
        // You can customize the upload logic as needed.
        try {
            index.upsert(vectors,"test");
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload vector to Pinecone: " + e.getMessage(), e);
        }

    }
}
