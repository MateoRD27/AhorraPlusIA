package com.unimag.ahorroplusia.controller;

import com.unimag.ahorroplusia.services.RecommendationAIService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class RecommendationAIController {

    private final RecommendationAIService recommendationAIService;

    @PostMapping("/recommend")
    public String getRecommendation(@RequestBody String prompt) throws Exception {
        return recommendationAIService.generateRecommendation(prompt);
    }
}
