package com.example.qiliqili.controller;

import com.example.qiliqili.pojo.CustomResponse;
import com.example.qiliqili.pojo.Danmu;
import com.example.qiliqili.service.danmu.DanmuService;
import com.example.qiliqili.service.utils.CurrentUser;
import com.example.qiliqili.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class DanmuController {
    @Autowired
    private DanmuService danmuService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CurrentUser currentUser;

    /**
     * 获取弹幕列表
     * @param vid   视频ID
     * @return  CustomResponse对象
     */
    @GetMapping("/danmu-list/{vid}")
    public CustomResponse getDanmuList(@PathVariable("vid") String vid) {
        Set<Object> idset = redisUtil.getMembers("danmu_idset:" + vid);
        List<Danmu> danmuList = danmuService.getDanmuListByIdset(idset);
        CustomResponse customResponse = new CustomResponse();
        customResponse.setData(danmuList);
        return customResponse;
    }

    /**
     * 删除弹幕
     * @param id    弹幕id
     * @return  响应对象
     */
    @PostMapping("/danmu/delete")
    public CustomResponse deleteDanmu(@RequestParam("id") Integer id) {
        Integer loginUid = currentUser.getUserId();
        return danmuService.deleteDanmu(id, loginUid, currentUser.isAdmin());
    }
}
