<!-- 顶栏消息通知 -->
<template>
  <el-popover
    :width="330"
    trigger="click"
    v-model="visible"
    class="ele-notice-group"
    transition="el-zoom-in-top"
    popper-class="ele-notice-pop"
  >
    <template v-slot:reference>
      <div class="ele-admin-header-tool-item">
        <div class="ele-notice-group">
          <el-badge :value="unreadNum" :hidden="!unreadNum">
            <i class="el-icon-bell" style="line-height: 55px"></i>
          </el-badge>
        </div>
      </div>
    </template>
    <el-tabs tab-position="left" v-model="active">
      <el-tab-pane name="notice" :label="noticeTitle">
        <div class="ele-notice-list ele-scrollbar-mini" >
          <div
            v-for="(item, index) in notice.list"
            :key="index"
            class="ele-notice-item"
            @click="gotoNoticeDetail(item)"
          >
            <div class="ele-cell ele-notice-item-wrapper">
              <i class="ele-notice-item-icon"></i>
              <div class="ele-cell-content">
                <div class="ele-elip" style="width: 180px;">{{ item.title }}</div>
                <div class="ele-text-secondary ele-elip">{{ $util.toDateString(item.createTime) }}</div>
              </div>
            </div>
            <el-divider />
          </div>
          <ele-empty v-if="!notice.count" text="已查看所有通知" />
        </div>
        <div class="ele-cell ele-notice-actions">
          <span @click="goto('notice')" class="ele-cell-content">
            查看更多
          </span>
        </div>

      </el-tab-pane>
      <el-tab-pane name="letter" :label="letterTitle">
        <div class="ele-notice-list ele-scrollbar-mini">
          <div
            v-for="(item, index) in letter.list"
            :key="index"
            class="ele-notice-item"
            @click="gotoMessageDetail(item)"
          >
            <div class="ele-cell ele-notice-item-wrapper">
              <i class="ele-letter-item-icon"></i>
              <div class="ele-cell-content">
                <div class="ele-elip" style="width: 180px;">{{ item.title }}</div>
                <div class="ele-text-secondary ele-elip">{{ $util.toDateString(item.createTime) }}</div>
              </div>
            </div>
            <el-divider />
          </div>
          <ele-empty v-if="!letter.count" text="已查看所有消息" />
        </div>
        <div class="ele-cell ele-notice-actions">
          <span @click="goto('letter')"  class="ele-cell-content">
            查看更多
          </span>
        </div>

      </el-tab-pane>
      <el-tab-pane :label="todoTitle" name="todo">
        <div class="ele-notice-list ele-scrollbar-mini">
          <div
            v-for="(item, index) in todo"
            :key="index"
            class="ele-notice-item"
          >
            <div class="ele-notice-item-wrapper">
              <div class="ele-cell ele-cell-align-top">
                <div class="ele-cell-content ele-elip">{{ item.title }}</div>
                <el-tag size="mini" :type="['info', 'danger', ''][item.status]">
                  {{ ['未开始', '即将到期', '进行中'][item.status] }}
                </el-tag>
              </div>
              <div class="ele-text-secondary ele-elip">
                {{ item.description }}
              </div>
            </div>
            <el-divider />
          </div>
          <ele-empty v-if="!todo.length" text="已完成所有任务" />
        </div>
        <div class="ele-cell ele-notice-actions">
          <span @click="goto('todo')"  class="ele-cell-content">
            查看更多
          </span>
        </div>
      </el-tab-pane>

      <el-tab-pane name="system_notice" :label="systemNoticeTitle">
        <div class="ele-notice-list ele-scrollbar-mini" >
          <div
            v-for="(item, index) in system_notice.list"
            :key="index"
            class="ele-notice-item"
            @click="gotoSystemNoticeDetail(item)"
          >
            <div class="ele-cell ele-notice-item-wrapper">
              <i class="ele-notice-item-icon"></i>
              <div class="ele-cell-content">
                <div class="ele-elip" style="width: 180px;">{{ item.title }}</div>
                <div class="ele-text-secondary ele-elip">{{ $util.toDateString(item.createTime) }}</div>
              </div>
            </div>
            <el-divider />
          </div>
          <ele-empty v-if="!system_notice.count" text="已查看所有平台通知" />
        </div>
        <div class="ele-cell ele-notice-actions">
          <span @click="goto('system_notice')" class="ele-cell-content">
            查看更多
          </span>
        </div>

      </el-tab-pane>
      <el-tab-pane name="system_letter" :label="systemLetterTitle">
        <div class="ele-notice-list ele-scrollbar-mini">
          <div
            v-for="(item, index) in system_letter.list"
            :key="index"
            class="ele-notice-item"
            @click="gotoSystemMessageDetail(item)"
          >
            <div class="ele-cell ele-notice-item-wrapper">
              <i class="ele-letter-item-icon"></i>
              <div class="ele-cell-content">
                <div class="ele-elip" style="width: 180px;">{{ item.title }}</div>
                <div class="ele-text-secondary ele-elip">{{ $util.toDateString(item.createTime) }}</div>
              </div>
            </div>
            <el-divider />
          </div>
          <ele-empty v-if="!system_letter.count" text="已查看所有平台消息" />
        </div>
        <div class="ele-cell ele-notice-actions">
          <span @click="goto('system_letter')"  class="ele-cell-content">
            查看更多
          </span>
        </div>

      </el-tab-pane>

    </el-tabs>
  </el-popover>
</template>

<script>
import {getUnreadNotice} from '@/api/layout';
import {mapGetters} from 'vuex';
import {getHiddenProp, getVisibilityState} from '@/api/common';
import {_DetailMessage} from "@/api/system/message";
import {_DetailSystemMessage} from "@/api/system/system-message";

export default {
    data() {
      return {
        // 是否显示
        visible: false,
        // 选项卡选中
        active: 'notice',
        // 通知数据
        notice: {},
        // 消息数据
        letter: [],
        // 平台通知数据
        system_notice: {},
        // 平台消息数据
        system_letter: [],
        // 待办数据
        todo: [],
        // 轮询消息数据
        myTarget:null
      };
    },
    computed: {
      ...mapGetters(['user']),
      // 通知标题
      noticeTitle() {
        return (this.notice.count ? `(${this.notice.count})` : '') + '通知';
      },
      // 消息标题
      letterTitle() {
        return (this.letter.count ? `(${this.letter.count})` : '') + '消息';
      },
      // 待办标题
      todoTitle() {
        return (this.todo.length ? `(${this.todo.length})` : '') + '待办' ;
      },
      // 通知标题
      systemNoticeTitle() {
        return (this.system_notice.count ? `(${this.system_notice.count})` : '') + '平台通知';
      },
      // 消息标题
      systemLetterTitle() {
        return  (this.system_letter.count ? `(${this.system_letter.count})` : '') + '平台消息';
      },
      // 未读数量
      unreadNum() {
        return this.notice?.count + this.letter?.count + this.system_notice?.count+ this.system_letter?.count;
      }
    },
    created() {
      this.query();
      this.myTarget = setInterval(()=>{
        this.query();
      }, 60000);

      //监听关闭页面和刷新事件
      window.addEventListener( 'beforeunload', this.clearInter);

      var visProp = getHiddenProp();
      if (visProp) {
        // 有些浏览器也需要对这个事件加前缀以便识别。
        var evtname = visProp.replace(/[H|h]idden/, '') + 'visibilitychange';
        document.addEventListener(evtname, this.visibilityFun);
      }
    },
    beforeDestroy() {
      //销毁监听事件
      window.removeEventListener("beforeunload",this.clearInter);
      var visProp = getHiddenProp();
      if (visProp) {
        // 有些浏览器也需要对这个事件加前缀以便识别。
        var evtname = visProp.replace(/[H|h]idden/, '') + 'visibilitychange';
        document.removeEventListener(evtname,this.visibilityFun);
      }
      this.clearInter()
    },
    methods: {
      clearInter(){
        if(this.myTarget){
          clearInterval(this.myTarget);
        }
      },
      visibilityFun(){
        this.clearInter();
        if(document[getVisibilityState()] !='hidden'){
          this.query();
          this.myTarget = setInterval(()=>{
            this.query();
          }, 60000);
        }
      },
      gotoNoticeDetail(item){
        this.$router.push({path:'/announcement/detail',query:{id:item.announcementId}}).catch(err=>{console.log(err)});
        this.visible=false;
      },
      gotoSystemNoticeDetail(item){
        this.$router.push({path:'/system-announcement/detail',query:{id:item.systemAnnouncementId}}).catch(err=>{console.log(err)});
        this.visible=false;
      },
      goto(type){
        this.$router.push({path:'/user/message',query:{type:type}}).catch(err=>{console.log(err)});
        this.visible=false;
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
          window.open(item.targetUrl,"_blank")
        }
      },
      gotoSystemMessageDetail(item){
        _DetailSystemMessage(item.systemMessageId).then((data) => {
          if(data){
            //重新拉取未读数量
            this.$store.dispatch('user/setUnreadNum', this.$store.state.user.unreadNum+1);
          }
        }).catch((e) => {
          console.log(e)
        });
        if(item.targetUrl){
          window.open(item.targetUrl,"_blank")
        }
      },
      /* 查询数据 */
      query() {
        getUnreadNotice()
          .then((result) => {
              this.notice = JSON.parse(result.notice);
              this.letter = JSON.parse(result.message);
              this.system_notice = JSON.parse(result.system_notice);
              this.system_letter = JSON.parse(result.system_message);
              this.todo = [];
          }).catch(() => {
              this.clearInter();
        });
      }
    },
    watch:{
      "$store.state.user.unreadNum":{
        handler(){
          this.query();
        }
      }
    }
  };
</script>

<style lang="scss">
  .ele-notice-group {
    display: block;

    .el-badge {
      line-height: 1;
      display: block;
    }
  }

  /* 消息通知pop */
  .ele-notice-pop {
    padding: 0 !important;

    /* tab */
    .el-tabs__nav-scroll {
      text-align: center;
    }

    .el-tabs__nav {
      float: none;
      display: inline-block;
    }

    .el-tabs__item {
      height: 44px;
      line-height: 44px;
      padding: 0 10px !important;
      font-size: 12px;
    }

    /* 空视图 */
    .ele-empty {
      padding: 60px 0;
    }
  }

  /* 列表 */
  .ele-notice-list {
    padding-top: 8px;
    min-height: 220px;
    overflow: scroll;
  }

  .ele-notice-item {
    .ele-notice-item-wrapper {
      padding: 5px 15px;
      transition: background-color 0.2s;
      cursor: pointer;

      &:hover {
        background-color: hsla(0, 0%, 60%, 0.05);
      }
    }

    .ele-text-secondary {
      margin-top: 5px;
      font-size: 12px;
    }

    .ele-cell-desc {
      margin-top: 3px !important;
      font-size: 12px !important;
    }
  }

  .ele-letter-item-icon{
    width: 10px;
    height: 10px;
    line-height: 10px !important;
    color: #fff;
    font-size: 16px;
    background-color: #48DC46;
    border-radius: 50%;
    text-align: center;

    &.el-icon-s-check {
      background-color: #f5686f;
    }

    &.el-icon-video-camera {
      background-color: #7cd734;
    }

    &.el-icon-s-claim {
      background-color: #faad14;
    }

    &.el-icon-message-solid {
      background-color: #2bcacd;
    }
  }

  .ele-notice-item-icon {
    width: 10px;
    height: 10px;
    line-height: 10px !important;
    color: #fff;
    font-size: 16px;
    background-color: #60b2fc;
    border-radius: 50%;
    text-align: center;

    &.el-icon-s-check {
      background-color: #f5686f;
    }

    &.el-icon-video-camera {
      background-color: #7cd734;
    }

    &.el-icon-s-claim {
      background-color: #faad14;
    }

    &.el-icon-message-solid {
      background-color: #2bcacd;
    }
  }

  /* 操作按钮 */
  .ele-notice-actions > .ele-cell-content {
    line-height: 42px;
    text-align: center;
    cursor: pointer;
    color: inherit;

    &:hover {
      background-color: hsla(0, 0%, 60%, 0.05);
    }
  }
  .el-badge__content.is-fixed{
    top: 18px!important;
  }
</style>
