package org.example.uploadDocs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import org.example.service.Embeddings;
import org.example.service.FileParser;
import org.example.service.ThreadPoolService;
import org.example.service.UploadVector;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/docs")
public class DocsController {
    // This class can be used to handle HTTP requests related to document uploads.
    // You can define methods here to handle specific endpoints, such as uploading documents,
    // retrieving document metadata, etc. For example:


    private final FileParser fileParser;
    private final ThreadPoolService threadPoolService;


    public DocsController(FileParser fileParser,
                          ThreadPoolService threadPoolService) {
        this.fileParser = fileParser;
        this.threadPoolService = threadPoolService;
    }

     @PostMapping("/upload")
     @PreAuthorize("hasAnyRole('SUPER ADMIN', 'ADMIN')")
     public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
         // Logic to handle file upload
         try {
             List<String> jsonRows = fileParser.parseCSV(file);
             try {
                 threadPoolService.processRows(jsonRows);
                 return ResponseEntity.ok("File uploaded and processed successfully.");
             }
             catch (Exception e) {
                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
             }
         } catch (IOException e) {
             return ResponseEntity.internalServerError().body("Error parsing file: " + e.getMessage());
         }

     }
}
