import http from "@/network/request1.js";

// 用户添加评论
export const sendComment = (formData) => {
    return http.request({
        url: '/comment/add',
        method: "post",
        data:formData,
        headers: {
        'Content-Type': 'multipart/form-data', // 发送formData 需要指定类型
    }
    });
}


export const getCommentTree = (params) => {
    return http.request({
        url: '/comment/get',
        method: "get",
        params: params
    });
}

export const getMoreReply = (params) => {
    return http.request({
        url: '/comment/reply/get-more',
        method: "get",
        params: params
    });
}