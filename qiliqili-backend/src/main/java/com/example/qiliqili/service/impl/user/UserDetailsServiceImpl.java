package com.example.qiliqili.service.impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.example.qiliqili.mapper.UserMapper;
import com.example.qiliqili.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.ne("state", 2);
        User user = userMapper.selectOne(queryWrapper);
        log.info(user.toString());
        if (user == null) {
            return null;
        }

        return new UserDetailsImpl(user);
    }
}
