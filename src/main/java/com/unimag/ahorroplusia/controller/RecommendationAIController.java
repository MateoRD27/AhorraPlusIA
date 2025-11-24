package com.unimag.ahorroplusia.controller;

import com.unimag.ahorroplusia.services.RecommendationAIService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class RecommendationAIController {

    private final RecommendationAIService recommendationAIService;

    @PostMapping("/recommend")
    public String getRecommendation(@RequestBody Map<String, String> request) throws Exception {
        String prompt = request.get("prompt");
        return recommendationAIService.generateRecommendation(prompt);
    }
}