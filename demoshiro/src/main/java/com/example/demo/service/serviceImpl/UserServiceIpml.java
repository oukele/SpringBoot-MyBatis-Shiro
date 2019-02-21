package com.example.demo.service.serviceImpl;

import com.example.demo.dao.UserMapper;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * UserMapper接口的业务逻辑类
 *
 * @author OYL
 * @create 2019-02-21 9:02
 */
@Service
public class UserServiceIpml implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByName(String name) {
        return userMapper.findByName(name);
    }
}
