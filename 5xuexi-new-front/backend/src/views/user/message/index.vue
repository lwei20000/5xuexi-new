<template>
  <div :class="['ele-body', { 'demo-message-responsive': styleResponsive }]">
    <el-card shadow="never" body-style="padding: 0;">
      <div class="ele-cell ele-cell-align-top ele-user-message">
        <el-menu
          :mode="mode"
          :default-active="active"
          class="ele-scrollbar-hide"
        >
          <el-menu-item index="notice">
            <router-link to="/user/message?type=notice">
              <span>通知</span>
            </router-link>
          </el-menu-item>
          <el-menu-item index="letter">
            <router-link to="/user/message?type=letter">
              <span>消息</span>
            </router-link>
          </el-menu-item>
          <el-menu-item index="todo">
            <router-link to="/user/message?type=todo">
              <span>待办</span>
            </router-link>
          </el-menu-item>
          <el-menu-item index="system_notice">
            <router-link to="/user/message?type=system_notice">
              <span>平台通知</span>
            </router-link>
          </el-menu-item>
          <el-menu-item index="system_letter">
            <router-link to="/user/message?type=system_letter">
              <span>平台消息</span>
            </router-link>
          </el-menu-item>
        </el-menu>
        <div class="ele-cell-content">
          <transition name="slide-right" mode="out-in">
            <message-notice v-if="active === 'notice'"/>
            <message-letter v-else-if="active === 'letter'"/>
            <message-todo   v-else-if="active === 'todo'" />
            <message-system-notice v-else-if="active === 'system_notice'"/>
            <message-system-letter v-else-if="active === 'system_letter'"/>
          </transition>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import MessageNotice from './components/message-notice.vue';
import MessageLetter from './components/message-letter.vue';
import MessageSystemNotice from './components/message-system-notice.vue';
import MessageSystemLetter from './components/message-system-letter.vue';
import MessageTodo from './components/message-todo.vue';

export default {
    name: 'UserMessage',
    components: { MessageNotice, MessageLetter, MessageTodo , MessageSystemNotice , MessageSystemLetter},
    data() {
      return {
        // 导航选中
        active: null
      };
    },
    computed: {
      // 小屏幕水平导航
      mode() {
        return this.styleResponsive && this.$store.state.theme.screenWidth < 768
          ? 'horizontal'
          : 'vertical';
      },
      // 是否开启响应式布局
      styleResponsive() {
        return this.$store.state.theme.styleResponsive;
      }
    },
    created() {

    },
    methods: {
    },
    watch: {
      $route: {
        handler(route) {
          if (route.path === '/user/message') {
            this.active = route?.query?.type || 'notice';
          }
        },
        immediate: true
      }
    },
  };
</script>

<style lang="scss" scoped>
  .ele-user-message {
    :deep(.el-menu) {
      width: 151px;
      flex-shrink: 0;
    }

    .ele-cell-content {
      padding: 15px;
      box-sizing: border-box;
    }

    .ele-badge-static {
      margin-right: 10px;
    }
  }

  @media screen and (min-width: 768px) {
    .demo-message-responsive .ele-user-message :deep(.el-menu) {
      .el-menu-item {
        text-align: right;
      }

      .el-menu-item:first-child {
        margin-top: 15px;
      }
    }
  }

  @media screen and (max-width: 768px) {
    .demo-message-responsive .ele-user-message {
      display: block;

      :deep(.el-menu) {
        width: auto;
        text-align: center;
        white-space: nowrap;
        overflow: auto;

        .el-menu-item {
          height: 45px;
          line-height: 45px;
          display: inline-block;
          float: none;
        }
      }

      .ele-badge-static {
        margin-left: 3px;
      }
    }
  }
</style>
