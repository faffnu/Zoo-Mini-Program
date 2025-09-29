package com.example.service;

import com.example.entity.Type;
import com.example.mapper.TypeMapper;
import com.example.utils.CacheClient;
import com.example.common.enums.RedisConstants;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TypeService {

    @Resource
    private TypeMapper typeMapper;
    @Resource
    private CacheClient cacheClient;

    private static final String CACHE_KEY = RedisConstants.CACHE_TYPE_KEY;
    private static final Long LOGICAL_EXPIRE_TTL = RedisConstants.CACHE_LONG_TTL;
    /**
     * 新增
     */
    public void add(Type type) {
        typeMapper.insert(type);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        int deleted = typeMapper.deleteById(id);
        if (deleted > 0) {
            // 删除成功，需要删除缓存
            String key = CACHE_KEY + id;
            cacheClient.delete(key);
        }
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        int deleted =  typeMapper.deleteBatchIds(ids);
        if (deleted > 0) {
            // 删除成功，需要删除缓存
            List<String> keys = ids.stream()
                    .map(id -> CACHE_KEY + id)
                    .collect(Collectors.toList());
            cacheClient.delete(keys);
        }
    }

    /**
     * 修改
     */
    public void updateById(Type type) {
        int updated = typeMapper.updateById(type);
        if (updated > 0) {
            // 更新成功，需要删除缓存，保证数据一致性
            // 下次查询时会重新加载最新数据并缓存
            String key = CACHE_KEY + type.getId();
            cacheClient.delete(key);
        }
    }

    /**
     * 根据ID查询
     */
    public Type selectById(Integer id) {
        return cacheClient.queryWithLogicalExpire(
                CACHE_KEY,
                id,
                Type.class,
                typeId->typeMapper.selectById(typeId),
                LOGICAL_EXPIRE_TTL,
                TimeUnit.MINUTES
        );
    }

    /**
     * 查询所有
     */
    public List<Type> selectAll(Type type) {
        return typeMapper.selectAll(type);
    }

    /**
     * 分页查询
     */
    public PageInfo<Type> selectPage(Type type, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Type> list = this.selectAll(type);

        return PageInfo.of(list);
    }

}