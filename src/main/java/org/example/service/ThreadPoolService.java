package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import io.pinecone.unsigned_indices_model.VectorWithUnsignedIndices;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ThreadPoolService {

    private final ExecutorService executorService;
    private final Embeddings embeddings;
    private final UploadVector uploadVector;
    private final int batchSize = 15; // Example batch size, can be configured

    public ThreadPoolService(ExecutorService executorService,
                            Embeddings embeddings,
                            UploadVector uploadVector) {
        this.executorService = executorService;
        this.embeddings = embeddings;
        this.uploadVector = uploadVector;
    }

    public void processRows(List<String> jsonRows){
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < jsonRows.size(); i += batchSize) {
            int end = Math.min(i + batchSize, jsonRows.size());
            List<String> batch = jsonRows.subList(i, end);
//            boolean isLastBatch = (end == jsonRows.size()); // to check exception handling
            CompletableFuture<Void> future = CompletableFuture.supplyAsync(()->{
                try {
//                    if(isLastBatch){
//                        throw new RuntimeException();
//                    }
                    processBatch(batch);
                } catch (Exception e) {
                    throw new RuntimeException("Error processing batch: " + e.getMessage(), e);
                }
                return null;
            }, executorService);
            futures.add(future);
        }
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
        catch (Exception e) {
            throw new RuntimeException("Error processing batches: " + e.getMessage(), e);
        }
    }

    private void processBatch(List<String> batch) {
        try {
            List<List<Double>> embeddingsList = embeddings.generateEmbeddings(batch);
            List<VectorWithUnsignedIndices> vectors = new ArrayList<>();
            for (int i = 0; i < batch.size(); i++) {
                List<Float> embedding = embeddingsList.get(i).stream().map(Double::floatValue).toList();
                String id = UUID.randomUUID().toString();
                Map<String, Object> objectMap = new ObjectMapper().readValue(batch.get(i), Map.class);
                Struct metadata = Struct.newBuilder()
                        .putFields("Module", Value.newBuilder().setStringValue(objectMap.getOrDefault("Module","").toString()).build() )
                        .putFields("Topic", Value.newBuilder().setStringValue(objectMap.getOrDefault("Topic","").toString()).build() )
                        .putFields("Level", Value.newBuilder().setStringValue(objectMap.getOrDefault("Level","").toString()).build() )
                        .putFields("Description", Value.newBuilder().setStringValue(objectMap.getOrDefault("Description","").toString()).build() )
                        .build();
                VectorWithUnsignedIndices vector = new VectorWithUnsignedIndices(id, embedding, metadata,null);
                vectors.add(vector);
            }
            uploadVector.uploadVectorBatchesToPineCone(vectors);
        } catch (Exception e) {
            throw new RuntimeException("Error processing batch: " + e.getMessage(), e);
        }
    }
}
