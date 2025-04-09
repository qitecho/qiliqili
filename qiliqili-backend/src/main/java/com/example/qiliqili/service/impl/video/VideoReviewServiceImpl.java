package com.example.qiliqili.service.impl.video;
import com.example.qiliqili.mapper.VideoMapper;
import com.example.qiliqili.pojo.CustomResponse;
import com.example.qiliqili.service.category.CategoryService;
import com.example.qiliqili.service.user.UserService;
import com.example.qiliqili.service.utils.CurrentUser;
import com.example.qiliqili.service.video.VideoReviewService;
import com.example.qiliqili.service.video.VideoService;
import com.example.qiliqili.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

@Service
public class VideoReviewServiceImpl implements VideoReviewService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private CurrentUser currentUser;

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor taskExecutor;

    /**
     * 查询对应状态的视频数量
     * @param status 状态 0审核中 1通过审核 2打回整改（指投稿信息不符） 3视频违规删除（视频内容违规）
     * @return 包含视频数量的CustomResponse对象
     */
    @Override
    public CustomResponse getTotalByStatus(Integer status) {
        CustomResponse customResponse = new CustomResponse();
        if (!currentUser.isAdmin()) {
            customResponse.setCode(403);
            customResponse.setMessage("您不是管理员，无权访问");
            return customResponse;
        }
        Long total = redisUtil.scard("video_status:" + status);
        customResponse.setData(total);
        return customResponse;
    }

    /**
     * 获取分页对应状态的视频
     * @return CustomResponse对象，包含符合条件的视频列表
     */
    @Override
    public CustomResponse getVideosByPage(Integer status, Integer page, Integer quantity) {
        CustomResponse customResponse = new CustomResponse();
        if (!currentUser.isAdmin()) {
            customResponse.setCode(403);
            customResponse.setMessage("您不是管理员，无权访问");
            return customResponse;
        }
        // 从 redis 获取待审核的视频id集合，为了提升效率就不遍历数据库了，前提得保证 Redis 没崩，数据一致性采用定时同步或者中间件来保证
        Set<Object> set = redisUtil.getMembers("video_status:" + status);
        if (set != null && set.size() != 0) {
            // 如果集合不为空，则在数据库主键查询，并且返回没有被删除的视频
            List<Map<String, Object>> mapList = videoService.getVideosWithDataByIds(set, page, quantity);
            customResponse.setData(mapList);
        }
        return customResponse;
    }
}
