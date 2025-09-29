package com.example;

import com.example.entity.Animal;
import com.example.service.AnimalService;
import com.example.service.UserVoucherService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.common.enums.RedisConstants.ANIMAL_GEO_KEY;

@Slf4j
@SpringBootTest
public class animalGeoTest {
    @Resource
    private AnimalService animalService;

    @Resource
    private UserVoucherService userVoucherService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //动物redisgeo
    @Test
    void loadAnimals() {
        List<Animal> animals = animalService.selectAll(new Animal());
        List<RedisGeoCommands.GeoLocation<String>> locations = animals.stream()
                .filter(a -> a.getX() != null && a.getY() != null)
                .map(a -> new RedisGeoCommands.GeoLocation<>(
                        a.getId().toString(),
                        new Point(a.getX(), a.getY())
                ))
                .collect(Collectors.toList());
        if (!locations.isEmpty()) {
            stringRedisTemplate.opsForGeo().add(ANIMAL_GEO_KEY, locations);
        }
    }

    @Test
    public void testCheckExpiredVouchers() {
        log.info("测试执行过期优惠券检查...");
        userVoucherService.checkAndUpdateExpiredVouchers();
    }


}
