package io.spring.infrastructure.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import okhttp3.*;

public class SendEventService {

  private static final String PATH_TO_WILCO_ID = "../.wilco";
  private static final String BASE_URL =
      Objects.requireNonNullElse(System.getenv("ENGINE_BASE_URL"), "https://engine.wilco.gg");
  private static String wilcoId;

  private final OkHttpClient client;
  private final ObjectMapper objectMapper;

  public SendEventService() {
    this.client = new OkHttpClient();
    this.objectMapper = new ObjectMapper();
    this.wilcoId = System.getenv("WILCO_ID");

    if (wilcoId == null && doesFileExist(PATH_TO_WILCO_ID)) {
      wilcoId = readFile(PATH_TO_WILCO_ID);
    }
  }

  public String sendEvent(String event, Map<String, Object> metadata) {
    MediaType mediaType = MediaType.parse("application/json");

    JsonNode metadataNode = objectMapper.valueToTree(metadata);

    ObjectNode data = objectMapper.createObjectNode();
    data.put("event", event);
    data.set("metadata", metadataNode);

    RequestBody requestBody = RequestBody.create(mediaType, data.toString());

    Request request =
        new Request.Builder()
            .url(BASE_URL + "/users/" + wilcoId + "/event")
            .post(requestBody)
            .addHeader("Content-type", "application/json")
            .build();

    try {
      Response response = client.newCall(request).execute();
      return response.body().string();
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Failed to send event " + event + " to Wilco engine");
      return null;
    }
  }

  private static boolean doesFileExist(String filePath) {
    return Objects.requireNonNull(new java.io.File(filePath).exists());
  }

  private static String readFile(String filePath) {
    StringBuilder content = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        content.append(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return content.toString();
  }
}
