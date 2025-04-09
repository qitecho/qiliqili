import http from "@/network/request1.js";

export const submitLogin = (data) => {
    return http.request({
        url: '/user/account/login',
        method: "post",
        data:data,

    });
}