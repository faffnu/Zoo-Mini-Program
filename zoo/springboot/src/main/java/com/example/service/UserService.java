package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    //是否存在该用户
    public User haveUser(String openid) {
        User dbUser = userMapper.selectByOpenid(openid);
        if (ObjectUtil.isNull(dbUser)) {
            return null;
        }
        return dbUser;
    }

    /**
     * 新增
     */
    public void add(User user) {
        userMapper.insert(user);
    }

    /**
     * 禁用
     */
    public void isBanById(Integer id) {
        userMapper.isBanById(id);
    }


    /**
     * 批量禁用
     */
    public void banBatch(List<Integer> ids) {
        for (Integer id : ids) {
            userMapper.banById(id);
        }
    }

    /**
     * 批量解禁
     */
    public void unBanBatch(List<Integer> ids) {
        for (Integer id : ids) {
            userMapper.unBanById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(User user) {
        userMapper.updateById(user);
    }

    /**
     * 根据ID查询
     */
    public User selectById(Integer id) {
        return userMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<User> selectAll(User user) {
        return userMapper.selectAll(user);
    }

    /**
     * 分页查询
     */
    public PageInfo<User> selectPage(User user, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<User> list = this.selectAll(user);

        for (User user1 : list) {
            if (user1.getIdCard() != null) {
                if (user1.getIdCard().length() > 8) {
                    int length = user1.getIdCard().length();
                    String prefix = user1.getIdCard().substring(0, 4); // 前4位
                    String suffix = user1.getIdCard().substring(length - 4); // 后4位
                    StringBuilder maskedPart = new StringBuilder();
                    for (int i = 0; i < length - 8; i++) {
                        maskedPart.append("*"); // 中间部分用*填充
                    }
                    String idCard = prefix + maskedPart.toString() + suffix;
                    user1.setIdCard(idCard);
                }
            }
        }
        return PageInfo.of(list);
    }

}