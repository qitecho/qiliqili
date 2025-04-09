import http from "@/network/request1.js";
// 获取文章详细
export const getArticleDetail = (id) => {
    return http.request({
        url: `/test/${id}`,
        method: "get"
    });
}