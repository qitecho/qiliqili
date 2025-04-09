// 获取token
export const GET_TOKEN = () => {
    // const str = localStorage.getItem(TOKEN_KEY) || sessionStorage.getItem(TOKEN_KEY)
    // if (!str) return null
    // // 解析json
    // const authObject = JSON.parse(str)
    // // 判断token是否过期
    // if (new Date(authObject.expire) <= new Date()) {
    //     REMOVE_TOKEN()
    //     ElMessage.warning('登录状态已过期，请重新登录')
    //     return null
    // }

    return localStorage.getItem("teri_token")
}