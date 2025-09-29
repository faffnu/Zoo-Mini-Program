package com.example.service;

import com.example.entity.Animal;
import com.example.mapper.AnimalMapper;
import com.example.utils.CacheClient;
import com.example.common.enums.RedisConstants;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.example.common.enums.RedisConstants.*;

@Service
@RequiredArgsConstructor
public class AnimalService {

    @Resource
    private AnimalMapper animalMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private CacheClient cacheClient;

    private static final String CACHE_KEY = RedisConstants.CACHE_ANIMAL_KEY;
    private static final Long LOGICAL_EXPIRE_TTL = RedisConstants.CACHE_LONG_TTL;

    /**
     * 新增
     */
    public boolean add(Animal animal) {
        int saved = animalMapper.insert(animal);
        return saved > 0;
    }

    /**
     * 删除
     */
    public boolean deleteById(Integer id) {
        int deleted = animalMapper.deleteById(id);
        if (deleted > 0) {
            // 删除成功，需要删除缓存
            String key = CACHE_KEY + id;
            cacheClient.delete(key);
        }
        return deleted > 0;
    }


    /**
     * 批量删除
     */
    public boolean deleteBatch(List<Integer> ids) {
        int deleted =  animalMapper.deleteBatchIds(ids);
        if (deleted > 0) {
            // 删除成功，需要删除缓存
            List<String> keys = ids.stream()
                    .map(id -> CACHE_KEY + id)
                    .collect(Collectors.toList());
            cacheClient.delete(keys);
        }
        return deleted > 0;
    }

    /**
     * 修改
     */
    public boolean update(Animal animal) {
        int updated = animalMapper.updateById(animal);
        if (updated > 0) {
            // 更新成功，需要删除缓存，保证数据一致性
            // 下次查询时会重新加载最新数据并缓存
            String key = CACHE_KEY + animal.getId();
            cacheClient.delete(key);
        }
        return updated > 0;
    }


    /**
     * 根据ID查询
     */
    public Animal selectById(Integer id) {
        return cacheClient.queryWithLogicalExpire(
                CACHE_KEY,
                id,
                Animal.class,
                animalId->animalMapper.selectById(animalId),
                LOGICAL_EXPIRE_TTL,
                TimeUnit.MINUTES
        );
    }


    /**
     * 查询所有
     */
    public List<Animal> selectAll(Animal animal) {
        return animalMapper.selectAll(animal);
    }





    /**
     * 分页查询
     */
    public PageInfo<Animal> selectPage(
            Animal animal,
            Double x,
            Double y,
            Integer maxDistance,
            Integer pageNum,
            Integer pageSize) {

        // 1. 如果不传坐标，直接返回所有动物（分页）
        if (x == null || y == null) {
            return PageHelper.startPage(pageNum, pageSize)
                    .doSelectPageInfo(() -> animalMapper.selectAll(animal));
        }

        // 2. 查询所有符合条件的附近动物（不分页）
        Distance distance = new Distance(maxDistance != null ? maxDistance : 100000);
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo()
                .search(
                        ANIMAL_GEO_KEY,
                        GeoReference.fromCoordinate(x, y),
                        distance,
                        RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs()
                                .includeDistance()
                                .sortAscending()
                );

        // 3. 处理空结果
        if (results == null) {
            return new PageInfo<>(Collections.emptyList());
        }

        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> geoResults = results.getContent();

        // 4. 解析出所有id和距离
        List<Long> allIds = new ArrayList<>(geoResults.size());
        Map<String, Distance> allDistanceMap = new HashMap<>(geoResults.size());

        for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : geoResults) {
            String animalIdStr = result.getContent().getName();
            allIds.add(Long.valueOf(animalIdStr));
            allDistanceMap.put(animalIdStr, result.getDistance());
        }

        // 5. 如果没有数据，返回空分页
        if (allIds.isEmpty()) {
            return new PageInfo<>(Collections.emptyList());
        }

        // 6. 根据所有ID和筛选条件查询动物（不分页）
        List<Animal> allAnimals = animalMapper.selectBatchIdsWithCondition(allIds, animal);

        // 7. 设置距离并保持原始顺序
        for (Animal animalItem : allAnimals) {
            String idStr = animalItem.getId().toString();
            animalItem.setDistance(allDistanceMap.get(idStr).getValue());
        }

        // 8. 手动分页
        int from = (pageNum - 1) * pageSize;
        int to = Math.min(from + pageSize, allAnimals.size());

        if (from >= allAnimals.size()) {
            return new PageInfo<>(Collections.emptyList());
        }

        List<Animal> pageAnimals = allAnimals.subList(from, to);

        // 9. 手动构建PageInfo对象
        PageInfo<Animal> pageInfo = new PageInfo<>();
        pageInfo.setList(pageAnimals);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(allAnimals.size()); // 总记录数是筛选后的总数
        pageInfo.setPages((int) Math.ceil((double) allAnimals.size() / pageSize));

        return pageInfo;
    }


}