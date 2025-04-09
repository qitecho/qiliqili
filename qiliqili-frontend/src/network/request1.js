import axios from 'axios';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';
const Jwt_Prefix = 'Bearer ';
import {GET_TOKEN} from '@/utils/auth.js'
import store from '@/store'
import { ElMessage } from 'element-plus';
// 创建axios实例
const http= axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API ?? '/', // api的base_url
  timeout: 60000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})


// request拦截器
http.interceptors.request.use((config) => {
  config.headers['X-Client-Type'] = 'Frontend'
  config.headers['Authorization'] = Jwt_Prefix + GET_TOKEN()

  return config
}, error => {
  return Promise.reject(error)
})

// response拦截器
http.interceptors.response.use(
    (config) => {
      const code = config.data.code;
      if (code && code !== 200)
        ElMessage.error(config.data.message || '未知错误, 请打开控制台查看');
      return config;
    },
    (err) => {
      console.log(err);
      if (err.response.headers.message === 'not login') {
        // 修改当前的登录状态
        store.commit("initData");
        // 关闭websocket
        if (store.state.ws) {
          store.state.ws.close();
          store.commit('setWebSocket', null);
        }
        // 清除本地token缓存
        localStorage.removeItem("teri_token");
        ElMessage.error("请登录后查看");
        store.state.isLoading = false;
      } else {
        ElMessage.error("特丽丽被玩坏了(¯﹃¯)");
        store.state.isLoading = false;
      }
    },
);

export default http