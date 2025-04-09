import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@/assets': path.resolve(__dirname, 'src/assets'),
      '@/components': path.resolve(__dirname, 'src/components'),
      '@/network': path.resolve(__dirname, 'src/network'),
      '@/views': path.resolve(__dirname, 'src/views'),
      '@/plugins': path.resolve(__dirname, 'src/plugins'),
      '@/utils': path.resolve(__dirname, 'src/utils'),
      '@/store': path.resolve(__dirname, 'src/store'),
      '@/apis': path.resolve(__dirname, 'src/apis'),
    }
  },
  // 开发服务器配置 (替代 devServer)
  server: {
    port: 8787, // 自定义端口
    open: true, // 自动打开浏览器
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端地址
        changeOrigin: true, // 改变源地址
        ws: true, // 开启 WebSocket 代理
        rewrite: (path) => path.replace(/^\/api/, ''), // 路径重写，去掉 /api 前缀
      },
    },
  },

  // 关闭全屏错误覆盖 (替代 devServer.client.overlay)
  client: {
    overlay: {
      errors: false, // 禁用错误全屏提示
      warnings: true, // 保留警告提示（可选）
    },
  },

  // 是否转译依赖 (替代 transpileDependencies)
  build: {
    transpile: true, // Vite 默认不转译 node_modules 中的依赖，若需要转译特定依赖，可在此处列出
  },
})
