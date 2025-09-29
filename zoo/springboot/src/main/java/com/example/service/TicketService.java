package com.example.service;

import com.example.entity.Ticket;
import com.example.mapper.TicketMapper;
import com.example.utils.CacheClient;
import com.example.common.enums.RedisConstants;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TicketService {

    @Resource
    private TicketMapper ticketMapper;
    @Resource
    private CacheClient cacheClient;

    private static final String CACHE_KEY = RedisConstants.CACHE_TICKET_KEY;
    private static final Long LOGICAL_EXPIRE_TTL = RedisConstants.CACHE_LONG_TTL;
    /**
     * 新增
     */
    public void add(Ticket ticket) {
        ticketMapper.insert(ticket);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        int deleted = ticketMapper.deleteById(id);
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
        int deleted =  ticketMapper.deleteBatchIds(ids);
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
    public void updateById(Ticket ticket) {
        int updated = ticketMapper.updateById(ticket);
        if (updated > 0) {
            // 更新成功，需要删除缓存，保证数据一致性
            // 下次查询时会重新加载最新数据并缓存
            String key = CACHE_KEY + ticket.getId();
            cacheClient.delete(key);
        }
    }

    /**
     * 根据ID查询
     */
    public Ticket selectById(Integer id) {
        return cacheClient.queryWithLogicalExpire(
                CACHE_KEY,
                id,
                Ticket.class,
                ticketId->ticketMapper.selectById(ticketId),
                LOGICAL_EXPIRE_TTL,
                TimeUnit.MINUTES
        );
    }

    /**
     * 查询所有
     */
    public List<Ticket> selectAll(Ticket ticket) {
        return ticketMapper.selectAll(ticket);
    }

    /**
     * 分页查询
     */
    public PageInfo<Ticket> selectPage(Ticket ticket, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Ticket> list = this.selectAll(ticket);

        return PageInfo.of(list);
    }

}