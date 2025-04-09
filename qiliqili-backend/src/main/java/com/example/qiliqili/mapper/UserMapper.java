package com.example.qiliqili.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.example.qiliqili.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
