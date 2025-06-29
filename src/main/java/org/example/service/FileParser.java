package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileParser {
    // This class can be used to handle file parsing logic.
    // You can define methods here to parse different file formats,
    // such as CSV, JSON, XML, etc. For example:

    public List<String> parseCSV(MultipartFile file) throws IOException, RuntimeException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> jsonRows = new ArrayList<>();
        try(Reader reader = new InputStreamReader(file.getInputStream())) {
            // Parse the CSV file
            CSVParser csvParser = CSVFormat.DEFAULT
                    .withHeader()
                    .withSkipHeaderRecord(true)
                    .parse(reader);
            // Process the CSV records as needed
            csvParser.forEach(record -> {
                Map<String, String> rowMap = new HashMap<>();
                for (String header : csvParser.getHeaderNames()) {
                    rowMap.put(header, record.get(header));
                }
                try {
                    jsonRows.add(objectMapper.writeValueAsString(rowMap));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
            return jsonRows;
        } catch (Exception e) {
            throw e;
        }
    }

    public void parseJSON(String jsonContent) {
        // Logic to parse JSON content
        // You can use libraries like Jackson or Gson for this purpose
    }

    // Add more parsing methods as needed for different file formats
}
