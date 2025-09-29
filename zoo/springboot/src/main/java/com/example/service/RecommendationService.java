package com.example.service;

import com.example.mapper.AnimalMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {

    @Resource
    private AnimalMapper animalMapper;

    public List<Integer> getPopularAnimals(int limit) {
        return animalMapper.selectPopularAnimalIds(limit);
    }


}
