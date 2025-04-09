package com.example.qiliqili;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.qiliqili.mapper.UserMapper;
import com.example.qiliqili.pojo.ESUser;
import com.example.qiliqili.pojo.User;
import com.example.qiliqili.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.List;

@SpringBootTest
class QiliqiliApplicationTests {
    private static final Logger log = LoggerFactory.getLogger(QiliqiliApplicationTests.class);

    @Autowired
    UserMapper userMapper;
    @Autowired
    private ElasticsearchClient client;
    @Autowired
    RedisUtil redisUtil;
    @Test
    void contextLoads(){
//        String sql = "SELECT 1";
//        Integer result = jdbcTemplate.queryForObject(sql, Integer.class);
//        redisUtil.addMember("qiliqili", "qiliqili");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",1);

        List<User> users = userMapper.selectList(queryWrapper);
        System.out.println(users);
//        ESUser esUser = new ESUser(1, "ds");
//        client.index(i -> i.index("user1").id(esUser.getUid().toString()).document(esUser));
//        log.info(result.toString());
    }

}
