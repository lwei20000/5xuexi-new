<template>
  <div class="infinite-list-wrapper">
    <div class="top-search-group noBorderInput">
      <el-input placeholder="请输入内容" v-model="keyword" />
      <el-button type="primary" @click="queryKeyword" >搜索</el-button>
    </div>
    <div class="article-list-item" v-for="(item, index) in data" :key="index" @click="gotoMessageDetail(item)">
      <h6 style="padding-top: 15px;overflow:hidden;white-space: nowrap;text-overflow: ellipsis;-o-text-overflow:ellipsis;">
        <b>{{ item.title }}</b>
      </h6>
      <div class="article-list-wrapper">
        <div class="article-list-left">
          <div class="article-list-content">{{ item.content }}  <a v-if="item.targetUrl" href="javascript:;">点击跳转</a></div>
          <div class="article-list-user-group">
            <span>{{ item.createTime }}</span>
          </div>
        </div>
        <div class="article-list-right">
          <img  v-if="item.messagePicture" :src="item.messagePicture"/>
          <img v-else  :src="API_BASE_URL+'/system/sysStaticPicture/message'"/>
        </div>
      </div>
      <el-divider />
    </div>
    <el-empty v-if="!data.length" description="暂无数据" ></el-empty>
    <div style="padding:15px 0; text-align: center" v-else>
      <el-button size="small" :loading="loading" v-if="hasMore" @click="query">
        加载更多
      </el-button>
      <span v-else>已全部加载完</span>
    </div>
  </div>
</template>

<script>
import {_pageMessages,_DetailMessage} from "@/api/system/message";
import {API_BASE_URL} from "@/config/setting";
export default {
  data() {
    return {
      API_BASE_URL:API_BASE_URL,
      hasMore:false,
      page:0,
      limit:10,
      keyword: '',
      loading: false,
      data: []
    }
  },
  mounted() {
    this.query();
  },
  methods: {
    queryKeyword(){
      this.page=0;
      this.data=[];
      this.query()
    },
    query() {
      this.page++;
      var params = {};
      params.page = this.page;
      params.limit = this.limit;
      params.title = this.keyword
      _pageMessages(params).then((result) => {
        if(this.page*this.limit<result.count){
          this.hasMore=true;
        }else{
          this.hasMore=false;
        }
        for(var i=0;i<result.list.length;i++){
          this.data.push(result.list[i]);
        }
      }).catch((e) => {
        this.$message.error(e.message);
      });
    },
    gotoMessageDetail(item){
      _DetailMessage(item.messageId).then((data) => {
        if(data){
          //重新拉取未读数量
          this.$store.dispatch('user/setUnreadNum', this.$store.state.user.unreadNum+1);
        }
      }).catch((e) => {
        console.log(e)
      });

      if(item.targetUrl){
        if(item.targetUrl.indexOf("http") == 0){//http开头
          window.open(item.targetUrl,"_blank")
        }else{
          this.$router.push(item.targetUrl).catch(err=>{console.log(err)});
        }
      }
    }
  }
};
</script>

<style lang="scss" scoped>

.article-list-item{
  cursor: pointer;
}

.infinite-list-wrapper{
  min-height:calc(100vh - 120px);
}
.article-list-right img{
  width: 85px; height: 85px; border-radius: 6px;
}

@media screen and (max-width: 768px ) {
  .article-list-right img{
    width:65px; height: 65px; border-radius: 6px
  }
}

.top-search-group {
  max-width: 500px;
  margin: 0 auto;
  display: flex;

  :deep(.el-input input) {
    border-top-right-radius: 0;
    border-bottom-right-radius: 0;
    border-right-width: 0;
  }

  .el-button {
    border-top-left-radius: 0;
    border-bottom-left-radius: 0;
  }
}

/* 文章列表 */
.article-list-wrapper {
  display: flex;
  padding: 15px 0;
}

.article-list-left {
  flex: 1;
}

.article-list-tags {
  margin-top: 12px;
}

.article-list-content {
  word-break: break-all;
  min-height: 65px;
  width: 95%;
}

.article-list-user-group {
  margin-top: 15px;

  & > span {
    vertical-align: middle;
  }
}

.article-list-footer {
  margin-top: 15px;

  .article-list-tool {
    display: inline-block;
    line-height: 22px;
    cursor: pointer;
  }

  .el-divider--vertical {
    margin: 0 16px;
  }
}
</style>
