package com.example.controller;

import com.example.common.Result;
import com.example.entity.Animal;
import com.example.service.AnimalService;
import com.example.service.RecommendationService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/recommend")
public class RecommendationController {
    @Resource
    private RecommendationService recommendationService;

    @Resource
    private AnimalService animalService;

    // 热门推荐接口
    @GetMapping("/popular")
    public Result getPopularRecommendations() {
        List<Animal> animals = new ArrayList<>();
        List<Integer> popularAnimals = recommendationService.getPopularAnimals(10);
        for (int i = 0; i < popularAnimals.size(); i++) {
            Animal animal = animalService.selectById(popularAnimals.get(i));
            animals.add(animal);
        }
        return Result.success(animals);
    }
}