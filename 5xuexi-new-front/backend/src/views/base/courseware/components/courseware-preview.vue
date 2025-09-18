<!-- 预览课件 -->
<template>
  <ele-modal :lock-scroll="false"
    width="720px"
    :visible="visible"
     append-to-body
    :close-on-click-modal="true"
    custom-class="ele-dialog-form"
    title="预览课件"
    @update:visible="updateVisible"
  >
    <div v-if="fileType">
      <!-- 播放器 -->
          <xgplayer :config="config" @player="onPlayer"/>
    </div>
    <div v-else>
          <iframe style="min-height: 400px;width: 100%" :src="config.url"></iframe>
    </div>
  </ele-modal>
</template>

<script>
import Xgplayer from 'xgplayer-vue';

  export default {
    components: { Xgplayer },
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object,
    },
    data() {
      return {
        fileType:true,
        // 视频播放器配置
        config: {
          id: 'demoPlayer',
          lang: 'zh-cn',
          // 宽度 100%
          fluid: true,
          // 视频地址
          // url: 'https://s1.pstatp.com/cdn/expire-1-M/byted-player-videos/1.0.0/xgplayer-demo.mp4',
          // 封面
          //poster: 'https://imgcache.qq.com/open_proj/proj_qcloud_v2/gateway/solution/general-video/css/img/scene/1.png',
          // 开启倍速播放
          playbackRate: [0.5, 1, 1.5, 2],
          // 开启画中画
          pip: false,
          //获取视频首帧，在移动端无效，与autoplay配置项不可同时设置为true
          videoInit: true
        },
        // 视频播放器实例
        player: null,
        // 视频播放器是否实例化完成
        ready: false
      };
    },
    computed: {

    },
    methods: {
      /* 播放器渲染完成 */
      onPlayer(e) {
        this.player = e;
        this.player.on('play', () => {
          this.ready = true;
        });
      },
      /* 更新visible */
      updateVisible(value) {
        if(this.fileType){
          if (!this.player.paused) {
            this.player.pause();
          }
        }
        this.$emit('update:visible', value);
      },
    },
    watch: {
      visible(visible) {
        if (visible) {
          if (this.data) {
            if(this.data.fileType =='pdf'){
              this.fileType = false
            }else{
              this.fileType = true
            }
            if(this.player){
              this.player.src = this.data.fileUrl;//切换视频地址
              this.player.config.url = this.data.fileUrl; //视频错误后，刷新后的视频地址
            }else{
              this.config.url = this.data.fileUrl;//设置视频地址
            }
          }
        }
      }
    }
  };
</script>
