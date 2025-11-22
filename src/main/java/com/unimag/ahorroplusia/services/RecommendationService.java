package com.unimag.ahorroplusia.services;

import com.unimag.ahorroplusia.dto.RecommendationDTO;


public interface RecommendationService {

    RecommendationDTO generateAndSaveRecommendation(Long userId) throws Exception;

}
