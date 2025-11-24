package com.unimag.ahorroplusia.services;

import org.springframework.beans.factory.annotation.Value;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Service
public class RecommendationAIService {

    @Value("${gemini.api-key}")
    private String apiKey;

    // USA GEMINI-2.0-FLASH que ya probaste con curl
    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateRecommendation(String prompt) throws IOException {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        String json = """
        {
          "contents": [{
            "parts": [{
              "text": "%s"
            }]
          }]
        }
        """.formatted(prompt.replace("\"", "\\\"").replace("\n", "\\n"));

        RequestBody body = RequestBody.create(mediaType, json.getBytes(StandardCharsets.UTF_8));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-goog-api-key", apiKey)  // IMPORTANTE: usa X-goog-api-key en header
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";

            if (!response.isSuccessful()) {
                throw new IOException("Error en API de Gemini: " + response.code() + " - " + responseBody);
            }

            // Parsear el JSON
            JsonNode root = objectMapper.readTree(responseBody);

            // Extraer el texto generado
            return root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        }
    }
}