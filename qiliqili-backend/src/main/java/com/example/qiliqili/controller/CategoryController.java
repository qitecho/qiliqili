package com.example.qiliqili.controller;


import com.example.qiliqili.pojo.CustomResponse;
import com.example.qiliqili.service.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 获取全部分区接口
     * @return CustomResponse对象
     */
    @GetMapping("/category/getall")
    public CustomResponse getAll() {
        return categoryService.getAll();
    }
}
