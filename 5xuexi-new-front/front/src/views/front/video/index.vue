<!-- 视频学习 -->
<template>
      <div class="ele-body videoarea" v-if="coursewarelist.length>0">
        <div class="player">
          <!-- 播放器 -->
          <xgplayer :config="config" v-if="config.url" @player="onPlayer"/>
        </div>
        <div class="videolist">
            <div v-for="(item,index) in coursewarelist" :key="index">
              <div class="list_item ele-elip">章节{{index+1}}：{{item.coursewareName}}</div>
                <div v-for="(item1,index1) in item.children" :key="index1" @click.stop="nodeClick(item1.coursewareId)" class="videolist_item" :class="{ isActive: item1.coursewareId == active }">
                  <div style="font-weight: 700;width: 100%;height: 100%;position: relative;font-size: 14px;">
                    <el-progress style="top: 5px;position: absolute;" :width="22" :stroke-width="4" type="circle" :show-text="false" :percentage="item1.progress" v-if="item1.progress<100"></el-progress>
                    <el-progress style="top: 5px;position: absolute;" :width="22" :stroke-width="2" type="circle" :percentage="100" status="success" v-if="item1.progress==100"></el-progress>
                    <span class="ele-elip" style="display:inline-block;width: 75%;margin-left: 25px;">{{item1.coursewareName}}</span>
                    <span style="float: right;">{{secondsToMinSeconds(item1.duration)}}</span></div>
                 </div>
            </div>
        </div>
      </div>
      <div style="min-height: 80vh;text-align: center;margin-top: 100px"  v-else-if="loadingState == false">
        <el-empty :description="errorMsg"></el-empty>
      </div>
</template>

<script>
import Xgplayer from 'xgplayer-vue';
import {getCourseware,saveLearn} from '@/api/index'
import {getHiddenProp, getVisibilityState} from "@/api/common";
export default {
  components: { Xgplayer },
  data() {
    return {
      loadingState:true,
      errorMsg:"",
      //当前章节课时
      active:0,
      //章节课时
      coursewarelist:[],
      // 视频播放器配置
      config: {
        id: 'demoPlayer',
        lang: 'zh-cn',
        // 视频地址
        //url: 'https://s1.pstatp.com/cdn/expire-1-M/byted-player-videos/1.0.0/xgplayer-demo.mp4',
        // 封面
        poster: 'https://imgcache.qq.com/open_proj/proj_qcloud_v2/gateway/solution/general-video/css/img/scene/1.png',
        // 开启倍速播放
        playbackRate: [0.5, 1, 1.5, 2],
        // 开启画中画
        pip: false,
        autoplay: true,
        height:'100%',
        width:'100%',
        playsinline:true,
        'webkit-playsinline':true,
        'x5-video-player-type':"h5-page"
      },
      // 视频播放器实例
      player: null,
      // 课件Map
      c_map: {},
      //课时IdList
      coursewareIds: [],
      //定时器
      myTarget:null,
      //播放时长
      time:0,
      //默认速度
      backrateChange:1,
      //专业ID
      majorId:'',
      //课程ID
      courseId:''
    };
  },
  computed: {

  },
  created() {
    //监听关闭页面和刷新事件
    window.addEventListener( 'beforeunload', this.intervalSaveLearning);
    //监听页面显隐
    var visProp = getHiddenProp();
    if (visProp) {
      // 有些浏览器也需要对这个事件加前缀以便识别。
      var evtname = visProp.replace(/[H|h]idden/, '') + 'visibilitychange';
      document.addEventListener(evtname, this.visibilityFun);
    }
  },
  beforeDestroy() {
    //销毁监听事件
    window.removeEventListener("beforeunload",this.intervalSaveLearning);
    //销毁页面显隐
    var visProp = getHiddenProp();
    if (visProp) {
      // 有些浏览器也需要对这个事件加前缀以便识别。
      var evtname = visProp.replace(/[H|h]idden/, '') + 'visibilitychange';
      document.removeEventListener(evtname,this.visibilityFun);
    }
    //保存进度
    this.intervalSaveLearning();
  },
  mounted(){
    this.majorId = this.$route.params.majorId;
    this.courseId = this.$route.params.courseId;
    if(!this.majorId || !this.courseId){
      this.$router.push({path:'/index'})
      return ;
    }
    //获取章节课时
    this.coursewarelist = [];
    this.coursewareIds=[];
    this.c_map = {};
    getCourseware(this.majorId,this.courseId).then((data) => {
      if(data && data.length>0){
        var zjList=[];
        var ksMap = {};
        for(var n=0;n<data.length;n++){
          var _item  = data[n];
          this.c_map[_item.coursewareId] = _item;
          if(_item.parentId ===0){
            zjList.push(_item);
          }else{
            var list = ksMap[_item.parentId];
            if(list){
              list.push(_item);
            }else{
              list = [];
              list.push(_item);
            }
            ksMap[_item.parentId] = list;
          }
        }
        this.coursewarelist = zjList.sort((a, b) => {
          return a.sortNumber - b.sortNumber;
        });

        var lastPlayId = null;
        var firstId = null;
        for (var i = 0; i < this.coursewarelist.length; i++) {
          var zjItem = this.coursewarelist[i];
          var _list = ksMap[zjItem.coursewareId];
          if(_list && _list.length>0){
            _list.sort((a, b) => {
              return a.sortNumber - b.sortNumber;
            });
            zjItem.children = _list;
            if(_list && _list.length>0){
              for(var j=0;j<_list.length;j++){
                var item = _list[j];
                if (item.lastPlay) {
                  lastPlayId = item.coursewareId;
                }
                if (item.fileUrl && firstId == null) {
                  //取第一个有视频的
                  firstId = item.coursewareId;
                }
                if (item.fileUrl) {
                  this.coursewareIds.push(item.coursewareId);
                }
              }
            }
          }
        }

        //设置第一个播放的视频
        if(this.$route.query.coursewareId){
          this.nodeClick(this.$route.query.coursewareId);
        }else if(lastPlayId){
          this.nodeClick(lastPlayId);
        }else{
          this.nodeClick(firstId);
        }
      }else{
        this.errorMsg = '没有章节课时';
      }
      this.loadingState = false;
    }).catch((e) => {
      this.loadingState = false;
      this.errorMsg = e.message;
    });
  },
  methods: {
    visibilityFun(){
      if(document[getVisibilityState()] == 'hidden'){
        if(this.player){
          this.player.pause()
        }
      }
    },
    /* 播放器渲染完成 */
    onPlayer(e) {
      this.player = e;
      this.player.on('play', () => {
         this.myTarget = setInterval(()=>{
             var c_time = 1*this.backrateChange;
             var c = this.c_map[this.active];
             if(c){
               c.currentPlayTime = this.player.currentTime;
               if(c.progress<100){
                 c.totalPalyTime = c.totalPalyTime+c_time;
                 //1、只要跳转就算完成
                 if(this.player.currentTime+10 >= c.duration){
                   c.progress = 100;
                 }else{
                   var p = Math.floor(this.player.currentTime/c.duration*100);
                   if(c.progress<p){
                     c.progress = p;
                   }
                 }
               }
               this.$forceUpdate();
             }
             this.time += c_time;
             if(this.time >= 300){
               this.saveLearning(300);
               this.time = 0;
             }
           }, 1000);
      });
      this.player.on('pause', () => {
        this.intervalSaveLearning();
      });
      this.player.on('ended', () => {
        //当前视频播放时间点设置为0
        var currentCourseware = this.c_map[this.active];
        if(currentCourseware){
          currentCourseware.currentPlayTime = 0;
        }
        //播放下一个视频
        for(var i=0;i<this.coursewareIds.length;i++){
          var  coursewareId = this.coursewareIds[i];
          if(coursewareId == this.active){
            if(this.coursewareIds[i+1]){
              this.nodeClick(this.coursewareIds[i+1]);
            }
            break;
          }
        }
      });
      this.player.on('playbackrateChange', (item) => {
        this.backrateChange = item.to;
      });

    },
    nodeClick(coursewareId){
      var item = this.c_map[coursewareId];
      if(this.active){
        this.intervalSaveLearning();
      }
      this.active = item.coursewareId;
      if(item.fileUrl){
        if(this.player){
          this.player.src = item.fileUrl;//切换视频地址
          this.player.config.url = item.fileUrl; //视频错误后，刷新后的视频地址
          this.player.currentTime= item.currentPlayTime||0; //设置开始播放时间
        }else{
          this.config.lastPlayTime = item.currentPlayTime||0; //设置开始播放时间
          this.config.url = item.fileUrl;//设置视频地址
        }
      }
    },
    saveLearning(time){
      var data = {};
      data.majorId =this.majorId;
      data.courseId =this.courseId;
      data.coursewareId = this.active;
      data.currentPlayTime = this.player.currentTime;
      data.time = time;
      saveLearn(data).then(() => {
      }).catch((e) => {
        this.$message.error(e.message);
      });
    },
    intervalSaveLearning(){
      if(this.myTarget){
        clearInterval(this.myTarget);
      }
      if(this.time>0){
        var l_time = this.time;
        this.time = 0;
        this.saveLearning(l_time);
      }
    },
    secondsToMinSeconds(dateTime){
      if(dateTime==null){
        return "00:00";
      }
      var theTime = parseInt(dateTime);// 秒
      var theTime1 = 0;// 分
      if(theTime > 60) {
        theTime1 = parseInt(theTime/60);
        theTime = parseInt(theTime%60);
      }
      if(theTime < 10){
        theTime = "0"+theTime;
      }
      if(theTime1 < 10){
        theTime1 = "0"+theTime1;
      }
      return theTime1+":"+theTime;
    },
  },

};
</script>

<style lang="scss" scoped>

:deep(.el-progress__text){
  font-size: small!important;
}

#demoPlayer{
  z-index: 1;
}
.videoarea{
  display: flex;
  min-height: 400px;
  .player{
    width:calc(100% - 360px);
    height: calc(100vh - 86px);
  }
  .videolist{
    width: 360px;
    height: calc(100vh - 116px);
    background: #fff;
    padding: 15px;
    overflow-y: scroll;
    .list_item{
      height: 32px;
      line-height: 32px;
      cursor: default;
      font-weight: 700;
      font-size: 16px;
    }
    .videolist_item{
      height: 32px;
      line-height: 32px;
      cursor: pointer;
      padding-left: 20px;
      :hover{
        color: var(--color-success);
        background-color: #ededed;
      }
    }
    .isActive{
     color: var(--color-primary);
    }
  }
}

/* 响应式 */
@media screen and (max-width: 992px) {
  .videoarea{
    display: initial;
    .player{
      width: auto;
      height: 400px;
    }
    .videolist{
      width: auto;
      height: calc(100vh - 486px);
    }
  }
}
/* 响应式 */
@media screen and (max-width: 492px) {
  .videoarea{
    display: initial;
    width:100%;
    padding:15px 0;
    .player{
      width: auto;
      height: 260px;
    }
    .videolist{
      width: auto;
      height: calc(100vh - 346px);
    }
  }
}
</style>
