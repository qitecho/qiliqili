package com.example.qiliqili.controller;

import com.example.qiliqili.utils.MinioUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    MinioUtils minioUtils;

    @GetMapping("/test")
    public String testConnection() throws Exception {
//        minioUtils.completeMultipartUpload("4ad2b5184e8472d68c717174704a2a1f");
        String sql = "SELECT 1";

        Integer result = jdbcTemplate.queryForObject(sql, Integer.class);
        return "Connection successful, result: " + result;
    }
    @GetMapping("/auth")
    public String testAuth() {

        return "测试授权";
    }

}