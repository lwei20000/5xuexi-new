<!-- 顶栏右侧区域 -->
<template>
  <div class="ele-admin-header-tool">

    <!-- 全屏切换 -->
    <div class="ele-admin-header-tool-item hidden-xs-only" @click="toggleFullscreen">
      <i v-if="fullscreen" class="el-icon-_screen-restore"></i>
      <i v-else class="el-icon-_screen-full"></i>
    </div>
    <!-- 语言切换 -->
    <i18n-icon :iconStyle="{'font-size':'18px'}" v-if="false"/>

    <!-- 消息通知 -->
    <header-notice v-if="loginUser" />
    <!-- 帮助 -->
    <div class="ele-admin-header-tool-item" v-if="loginUser" @click="gohelp()">
      <i class="el-icon-help"><span style="font-size: 14px; padding-left: 2px;position: relative;top: -2px;">帮助</span></i>
    </div>
    <!-- 租户切换 -->
    <header-tenant v-if="loginUser" />

    <!-- 用户信息 -->
    <div class="ele-admin-header-tool-item" >
      <el-dropdown @command="onUserDropClick" trigger="click" v-if="loginUser">
        <div class="ele-admin-header-avatar">
          <el-avatar :src="loginUser.avatar?loginUser.avatar:avatarImg" />
          <span class="hidden-xs-only">{{ loginUser.realname }}</span>
          <i class="el-icon-arrow-down" style="line-height: 55px"></i>
        </div>
        <template v-slot:dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile" icon="el-icon-user">
              {{ $t('layout.header.profile') }}
            </el-dropdown-item>
            <el-dropdown-item command="password" icon="el-icon-key">
              {{ $t('layout.header.password') }}
            </el-dropdown-item>
            <el-dropdown-item
              command="logout"
              icon="el-icon-switch-button"
              divided
            >
              {{ $t('layout.header.logout') }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <div v-else>
        <el-button size="mini" type="primary" @click="$router.push('/login')">登录</el-button>
      </div>
    </div>

    <!-- 主题设置 -->
    <div class="ele-admin-header-tool-item" @click="openSetting">
      <i class="el-icon-_more"></i>
    </div>
    <!-- 修改密码弹窗 -->
    <password-modal v-if="loginUser" :visible.sync="passwordVisible" />
    <!-- 主题设置抽屉 -->
    <setting-drawer :visible.sync="settingVisible" />
  </div>
</template>

<script>
  import HeaderNotice from './header-notice.vue';
  import HeaderTenant from './tenant-select.vue';
  import PasswordModal from './password-modal.vue';
  import SettingDrawer from './setting-drawer.vue';
  import I18nIcon from './i18n-icon.vue';
  import { logout } from '@/utils/page-tab-util';
  import {_logout} from "@/api/layout";
  import {API_BASE_URL} from "@/config/setting";
  export default {
    components: { HeaderNotice,HeaderTenant, PasswordModal, SettingDrawer, I18nIcon },
    props: {
      // 是否是全屏
      fullscreen: Boolean
    },
    data() {
      return {
        // 是否显示修改密码弹窗
        passwordVisible: false,
        // 是否显示主题设置抽屉
        settingVisible: false,
        //默认头像
        avatarImg:API_BASE_URL+'/system/sysStaticPicture/avatar',
      };
    },
    computed: {
      // 当前用户信息
      loginUser() {
        return this.$store.state.user.info;
      }
    },
    methods: {
      /* 用户信息下拉点击事件 */
      onUserDropClick(command) {
        if (command === 'password') {
          this.passwordVisible = true;
        } else if (command === 'profile') {
          if (this.$route.fullPath !== '/user/profile') {
            this.$router.push('/user/profile');
          }
        } else if (command === 'logout') {
          // 退出登录
          this.$confirm(this.$t('layout.logout.message'), this.$t('layout.logout.title'), { type: 'warning',lockScroll:false })
            .then(() => {
              _logout().then((msg) => {
                  this.$message.success(msg);
                  logout();
              })
            })
            .catch(() => {});
        }
      },
      /* 全屏切换 */
      toggleFullscreen() {
        this.$emit('fullscreen');
      },
      /* 打开设置抽屉 */
      openSetting() {
        this.settingVisible = true;
      },
      gohelp(){
        this.$router.push('/system/doc').catch(err=>{console.log(err)});;
      }
    }
  };
</script>
