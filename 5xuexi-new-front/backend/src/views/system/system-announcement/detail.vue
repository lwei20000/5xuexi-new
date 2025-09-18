<!-- 公告详情 -->
<template>
  <div class="ele-body "  v-loading="loading" >
    <el-card shadow="never" style="min-height: calc(100vh - 126px);" :key="systemAnnouncementId">
      <div v-if="isShow">
        <h2 style="text-align: center;font-weight: bold;">{{title}}</h2>
        <h5 class="intro-time" v-if="time">发布时间：{{time}}</h5>
        <el-divider></el-divider>
        <div class="wdms_html intro-content"  v-html="content"></div>
        <div v-if="attachment.length>0">
          <span style="font-size: 18px;font-weight: bold;">附件：</span>
          <div style="margin-left: 46px;font-size: 16px">
            <a href="javascript:;" style="display: block;margin-top: 10px;" @click="download(item)"
               v-for="(item,index) in attachment" :key="index">{{item.name}}</a>
          </div>
        </div>
      </div>
      <el-empty v-else description="未找到数据或者没有权限" ></el-empty>
    </el-card>
  </div>
</template>
<script>
import {_DetailSystemAnnouncement} from "@/api/system/system-announcement";
import {downloadUrl} from "@/api/common";
import Prism from "../../../../public/tinymce/skins/prism/prism.js"; //引入插件
export default {
  name: 'SysSystemAnnouncementDetail',
  data() {
    return {
      loading:false,
      isShow:false,
      content:'',
      title:'',
      time:'',
      attachment:[],
      systemAnnouncementId:''
    }
  },
  mounted() {
  },
  methods: {
    download(item){
      downloadUrl(item.url,item.name);
    },
    getDetailSystemAnnouncement(){
      this.loading = true;
      _DetailSystemAnnouncement(this.systemAnnouncementId).then((data) => {
        this.loading = false;
        if(data){
          this.isShow = true;
          this.content = data.content;
          this.title =  data.title;
          this.time = data.createTime;
          if(data.systemAnnouncementAttachment){
            this.attachment = JSON.parse(data.systemAnnouncementAttachment);
          }else{
            this.attachment = [];
          }
          //重新拉取未读数量
          this.$store.dispatch('user/setUnreadNum', this.$store.state.user.unreadNum+1);
        }else{
          this.isShow = false;
        }
      }).catch((e) => {
        this.loading = false;
        this.$message.error(e.message);
      });
    }
  },
  watch: {
    $route: {
      handler(route) {
        if (route.path === '/system-announcement/detail') {
          this.systemAnnouncementId = route?.query?.id;
          this.getDetailSystemAnnouncement();
        }
      },
      immediate: true
    },
    content(){
      this.$nextTick(() => {
        Prism.highlightAll()
      })
    }
  },
}
</script>
<style lang="scss" scoped>
.intro-time {
  text-align: center;
  line-height: 30px;
  margin-top: 15px;
  color: var(--color-info);
  margin-bottom: 20px;
  font-size: 14px;
}
.intro-content {
  margin-top: 26px;
  margin-bottom: 25px;
  font-size: 16px;
  line-height: 26px;
}
</style>
