package com.example.qiliqili.service.comment;



import com.example.qiliqili.pojo.Comment;
import com.example.qiliqili.pojo.CommentTree;
import com.example.qiliqili.pojo.CustomResponse;

import java.util.List;

public interface CommentService {
    List<CommentTree> getCommentTreeByVid(Integer vid, Long offset, Integer type);

    CommentTree sendComment(Integer vid, Integer uid, Integer rootId, Integer parentId, Integer toUserId, String content);

    CustomResponse deleteComment(Integer id, Integer uid, boolean isAdmin);

    List<Comment> getChildCommentsByRootId(Integer rootId, Integer vid, Long start, Long stop);

    List<Comment> getRootCommentsByVid(Integer vid, Long offset, Integer type);

    CommentTree getMoreCommentsById(Integer id);

    /*
    评论点赞点踩相关
     */
    void updateLikeAndDisLike(Integer id, boolean addLike);

    void updateComment(Integer id, String column, boolean incr, Integer count);
}
