<template>
  <div ref="artplayerContainer" style="width: 100%; height: 500px;"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import Artplayer from 'artplayer';
import artplayerPluginDanmuku from 'artplayer-plugin-danmuku';
// 定义 props
const props = defineProps({
  videoUrl: {
    type: String,
    required: true,
  },
  poster: {
    type: String,
    default: '',
  },
  danmaku: {
    type: Array,
    default: () => [],
  },
});

// DOM 容器引用
const artplayerContainer = ref(null);
// ArtPlayer 实例
let art = ref(null);

// 初始化 ArtPlayer
onMounted(() => {
  art = new Artplayer({
    container: artplayerContainer.value, // 绑定 DOM 容器
    url: props.videoUrl, // 视频地址
    poster: props.poster, // 封面图片
    autoplay: false, // 是否自动播放
    volume: 0.7, // 默认音量
    isLive: false, // 是否为直播
    muted: false, // 是否静音
    loop: true, // 是否循环播放
    screenshot: true, // 是否允许截图
    setting: true, // 是否显示设置面板
    flip: true,
    playbackRate: true,
    aspectRatio: true,
    subtitleOffset: true, // 是否支持字幕偏移

    hotkey: true, // 是否启用快捷键
    pip: true, // 是否支持画中画
    fullscreen: true, // 是否支持全屏
    fullscreenWeb: true, // 是否支持网页全屏

    mutex: true,//假如多个视频播放。只能播放一个
    miniProgressBar: true,//迷你进度条
    plugins: [
      artplayerPluginDanmuku({
        danmuku: props.danmaku,
        //     [
        //   {
        //     text: '欢迎使用 !',
        //     time: 1, // 弹幕出现的时间（秒）
        //     color: '#ff0000', // 弹幕颜色
        //     mode: 0, // 0: 滚动, 1: 顶部, 2: 底部
        //   },
        //   {
        //     text: '集成成功',
        //     time: 3,
        //     color: '#00ff00',
        //     mode: 1,
        //   },
        // ],

        // 这是用户在输入框输入弹幕文本，然后点击发送按钮后触发的函数
        // 你可以对弹幕做合法校验，或者做存库处理
        // 当返回true后才表示把弹幕加入到弹幕队列
        async beforeEmit(danmu) {
          console.log("hhh");
          console.log(danmu);
          const isDirty = (/fuck/i).test(danmu.text);
          if (isDirty) return false;

          return true;
        },

        // 这里是所有弹幕的过滤器，包含来自服务端的和来自用户输入的
        // 你可以对弹幕做合法校验
        // 当返回true后才表示把弹幕加入到弹幕队列
        filter(danmu) {
          return danmu.text.length <= 200;
        },

        // 这是弹幕即将显示的时触发的函数
        // 你可以对弹幕做合法校验
        // 当返回true后才表示可以马上发送到播放器里
        async beforeVisible(danmu) {
          // const state = await saveDanmu(danmu);
          // console.log(state);
          return true;
        },
      }),
    ],
    controls: [

      {
        name: 'custom-button',
        position: 'right',
        html: '<span>自定义</span>',
        tooltip: '自定义按钮',
        click: () => {
          console.log('自定义按钮被点击');
        },
      },
      {
        name: 'button1',
        index: 10,
        position: 'right',
        html: 'Subtitle',
        selector: [
          {
            default: true,
            html: '<span style="color:blue">subtitle 01</span>',
          },
          {
            html: '<span style="color:red">subtitle 02</span>',
          },
        ],
        onSelect: function (item, $dom) {
          console.info(item, $dom);
          return item.html;
        },
      },
      {
        html: 'quality',
        position: 'right',
        tooltip: '1080P',
        selector: [
          {
            default: true,
            html: '1080P',
            url: '/assets/sample/video.mp4?id=1080',
          },
          {
            html: '720P',
            url: '/assets/sample/video.mp4?id=720',
          },
          {
            html: '360P',
            url: '/assets/sample/video.mp4?id=360',
          },
        ],
        onSelect: function (item, $dom,event) {
          console.info(item, $dom, event);
          art.switchQuality(item.url, item.html);
          return item.html;
        },
      },

    ],

    contextmenu: [
      {
        name: 'your-menu',
        html: 'Your Menu',
        click: function (...args) {
          console.info(args);
          art.contextmenu.show = false;
        },
      },
    ],
  });

  // 监听事件
  art.on('ready', () => {
    console.log('ArtPlayer 已准备就绪');
  });

  art.on('danmuku', (danmu) => {
    console.log('弹幕显示:', danmu);
  });
  art.on('flip', (flip) => {
    console.log('翻转:',flip);
  });


});//onMounted

// 保存到数据库
function saveDanmu(danmu) {
  return new Promise(resolve => {
    setTimeout(() => {
      console.log("弹幕保存到数据库")
      resolve([
        {
          text: '使用 Promise 异步返回',
          time: 1
        },
      ]);
    }, 1000);
  })
}
// 清理 ArtPlayer 实例
onUnmounted(() => {
  if (art) {
    art.destroy();
  }
});

const currentTime = ref(0);
// 暴露方法给父组件
defineExpose({
  getArtPlugins: () => art && art.plugins,
  currentTime: () => art && art.currentTime,
  play: () => art && art.play(),
  pause: () => art && art.pause(),
  // seek: (time) => art && art.seek(time),
  sendDanmuku: (danmuJson) => {
    if (art && danmuJson) {
      console.log("暴露方法，发送弹幕")

      console.log(danmuJson.text)
      art.plugins.artplayerPluginDanmuku.emit({

        text: danmuJson.text,
        // time: art.video.currentTime, // 当前播放时间
        color: danmuJson.color || '#ffffff',
        border: danmuJson.border || true,
        mode: danmuJson.mode || 0, // 0: 滚动, 1: 顶部, 2: 底部
      });
    }
  },
});
</script>

<style>
/* ArtPlayer 自带样式会自动应用，无需额外配置 */
.art-video-player{
  width: 900px;
  border-radius: 10px;
  left: 10px;
}
</style>