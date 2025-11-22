package com.unimag.ahorroplusia.services;

import org.springframework.beans.factory.annotation.Value;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
// servicio que llama al API de Geminis
public class RecommendationAIService {

    @Value("${gemini.api-key}")
    private String apiKey;

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateText?key=";

    public String generateRecommendation(String prompt) throws IOException {

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        String json = """
        {
          "contents": [{
            "parts": [{
              "text": "%s"
            }]
          }]
        }
        """.formatted(prompt);

        RequestBody body = RequestBody.create(mediaType, json.getBytes(StandardCharsets.UTF_8));

        Request request = new Request.Builder()
                .url(API_URL + apiKey)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
