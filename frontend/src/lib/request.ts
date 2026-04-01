import axios from 'axios';
import Cookies from 'js-cookie';

const api = axios.create({
  baseURL: 'http://localhost:8080', // 暂连本地后端或服务器，根据需要调整
  timeout: 10000,
});

// 请求拦截器：自动携带 Token
api.interceptors.request.use(
  (config) => {
    const token = Cookies.get('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 响应拦截器：处理全局错误
api.interceptors.response.use(
  (response) => {
    const res = response.data;
    // 如果 code 不是 200，当做错误处理
    if (res.code && res.code !== 200) {
      // 可以在这里引入 toast 提示
      console.error(res.message);
      return Promise.reject(new Error(res.message || 'Error'));
    }
    return res;
  },
  (error) => {
    if (error.response?.status === 401) {
      Cookies.remove('token');
      Cookies.remove('username');
      Cookies.remove('role');
      if (typeof window !== 'undefined') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;