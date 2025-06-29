package org.example.chatbot;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.protobuf.Value;
import io.pinecone.clients.Index;
import io.pinecone.unsigned_indices_model.QueryResponseWithUnsignedIndices;
import org.example.service.Embeddings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {
    // This class can be used to handle HTTP requests related to the chatbot functionality.
    // You can define methods here to handle specific endpoints, such as sending messages,
    // retrieving responses, etc. For example:

    private final Embeddings embeddings;
    private final Client genaiClient;
    private final Index index;

    public ChatbotController(Embeddings embeddings, Client genaiClient, Index index) {
        this.embeddings = embeddings;
        this.genaiClient = genaiClient;
        this.index = index;
    }

     @PostMapping("/ask")
     public ResponseEntity<String> chat(@RequestBody ChatBody chat) {
         try {
             List<List<Double>> queryEmbedding = embeddings.generateEmbeddings(Collections.singletonList(chat.getQuestion()));
             List<Float> queryEmbeddingFloat = queryEmbedding.get(0).stream().map(Double::floatValue).toList();
             QueryResponseWithUnsignedIndices response = index.query(25, queryEmbeddingFloat,null,null,null
                     ,"test",null,false,true );
             StringBuilder promptBuilder = new StringBuilder();
             promptBuilder.append("""
                     You are now Restroworks POS systems AI chatbot. User has asked a question about the system,\
                      present at the end of this prompt and below are some info about the system.\
                      Answer their query in a friendly and helpful manner.\
                      Answer should be in max 3-4 sentences and it should be based only on the info provided below:,
                     
                     """);
             response.getMatchesList().forEach(result -> {
                    promptBuilder.append("{ Module: ").append(result.getMetadata().getFieldsOrDefault("Module", Value.newBuilder().build()).getStringValue()).append("\n")
                            .append("Topic: ").append(result.getMetadata().getFieldsOrDefault("Topic", Value.newBuilder().build()).getStringValue()).append("\n")
                            .append("Level: ").append(result.getMetadata().getFieldsOrDefault("Level", Value.newBuilder().build()).getStringValue()).append("\n")
                            .append("Description: ").append(result.getMetadata().getFieldsOrDefault("Description", Value.newBuilder().build()).getStringValue()).append(" }\n\n");
             });
             promptBuilder.append("Below is the conversation history with the user:\n\n");
                chat.getHistory().ifPresent(hist -> {
                    hist.forEach(msg -> promptBuilder.append(msg).append("\n"));
                });
             promptBuilder.append("""
                     Consider the history only if the question is related to the previous conversation.\
                      If the question is not related to the previous conversation,\
                       ignore the history and answer based on the info provided above.
                     
                     """);
             promptBuilder.append("User Question: ").append(chat.getQuestion()).append("\n\n");
             promptBuilder.append("""
                     If you feel the user question is just a\
                      general message and not a query, answer accordingly,\
                      it may or may not contain anything related to POS.
                     
                     """);
             System.out.println("Prompt: " + promptBuilder.toString());
             GenerateContentResponse answer =
                     genaiClient.models.generateContent(
                             "gemini-2.0-flash",
                             promptBuilder.toString(),
                             null);
             return ResponseEntity.ok(answer.text());

         } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating embedding: " + e.getMessage());
         }
     }
}
