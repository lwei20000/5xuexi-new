<template>
  <div :class="[  'login-wrapper', ['', 'login-form-right', 'login-form-left'][direction]]">
    <img :src="API_BASE_URL+'/system/sysStaticPicture/login-background'" style="width: 100%;position: absolute;height: 100vh;object-fit: cover;">
    <img style="height: 50px; position: absolute; top: 20px; left: 20px;" :src="API_BASE_URL+'/system/sysStaticPicture/logo'">
    <div class="login">
      <div class="logo" style=" position: relative;">
        <img class="login_bg" :src="API_BASE_URL+'/system/sysStaticPicture/login-middle-background'" >
      </div>
      <el-form
        ref="form"
        size="large"
        :model="form"
        :rules="rules"
        class="login-form ele-bg-white noBorderInput"
        @keyup.enter.native="submit"
      >
        <h4>{{ $t('login.title') }}</h4>
        <el-form-item v-if="errNum<3" style="height: 8px">

        </el-form-item>
        <el-form-item prop="username">
          <el-input id="username"
            clearable
            v-model.trim="form.username"
            prefix-icon="el-icon-user"
            :placeholder="$t('login.username')"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            show-password
            v-model.trim="form.password"
            prefix-icon="el-icon-lock"
            :placeholder="$t('login.password')"
            auto-complete="new-password"
          />
        </el-form-item>
        <el-form-item v-if="errNum<3" style="height: 8px">

        </el-form-item>
        <el-form-item prop="code" v-if="errNum>=3">
          <div class="login-input-group">
            <el-input
              clearable
              v-model.trim="form.code"
              prefix-icon="el-icon-_vercode"
              :placeholder="$t('login.code')"
            />
            <img
              alt=""
              v-if="captcha"
              :src="captcha"
              class="login-captcha"
              @click="changeCaptcha"
            />
          </div>
        </el-form-item>
        <div class="el-form-item">
          <el-checkbox v-model="form.remember">
            {{ $t('login.remember') }}
          </el-checkbox>
          <el-link
            type="primary"
            :underline="false"
            class="ele-pull-right"
            @click="$router.replace('/forget')"
          >
            {{ $t('login.forget')}}
          </el-link>
        </div>
        <div class="el-form-item">
          <el-button
            size="large"
            type="primary"
            class="login-btn"
            :loading="loading"
            @click="submit"
          >
            {{ loading ? $t('login.loading') : $t('login.login') }}
          </el-button>
        </div>
        <div class="ele-text-center" style="margin-bottom: 10px" v-if="false">
          <i class="login-oauth-icon el-icon-_qq" style="background: #3492ed"></i>
          <i class="login-oauth-icon el-icon-_wechat" style="background: #4daf29"></i>
          <i class="login-oauth-icon el-icon-_weibo" style="background: #cf1900"></i>
        </div>
      </el-form>
    </div>
    <div class="login-copyright" v-if="false">
      {{ $t('layout.footer.copyright') }}
    </div>
    <!-- 多语言切换 -->
    <div style="position: absolute; right: 30px; top: 20px" v-if="false">
      <i18n-icon
        :icon-style="{ fontSize: '22px', cursor: 'pointer' }"
      />
    </div>
  </div>
</template>

<script>
  import I18nIcon from '@/layout/components/i18n-icon.vue';
  import {login, getCaptcha,getErrNum} from '@/api/login';
  import {API_BASE_URL} from "@/config/setting";
  export default {
    // eslint-disable-next-line vue/multi-word-component-names
    name: 'Login',
    components: { I18nIcon },
    data() {
      return {
        API_BASE_URL:API_BASE_URL,
        //查询错误次数定时器
        timer: null,
        // 登录框方向, 0居中, 1居右, 2居左
        direction: 0,
        // 加载状态
        loading: false,
        // 表单数据
        form: {
          username: '',
          password: '',
          remember: true,
          code:'',
          uuid:''
        },
        // 验证码base64数据
        captcha: '',
        // 验证码内容, 实际项目去掉
        text: '',
        errNum:0,
        clientHeight:0
      };
    },
    computed: {
      // 表单验证规则
      rules() {
        return {
          username: [
            {
              required: true,
              message: this.$t('login.username'),
              type: 'string',
              trigger: 'change'
            }
          ],
          password: [
            {
              required: true,
              message: this.$t('login.password'),
              type: 'string',
              trigger: 'change'
            }
          ],
          code: [
            {
              required: true,
              message: this.$t('login.code'),
              type: 'string',
              trigger: 'change'
            }
          ],

        };
      }
    },
    mounted() {
      //input输入框弹起软键盘的解决方案。
      this.clientHeight = document.body.clientHeight;
    },
    methods: {
      /* 提交 */
      submit() {
        this.$refs.form.validate((valid) => {
          if (!valid) {
            return false;
          }
          this.loading = true;
          login(this.form)
            .then((data) => {
              this.loading = false;
              if (data.code === 0) {
                this.$message.success(data.message);

                //登录成功后，如果安全键盘回弹后留下空白。需要调出普通键盘覆盖一下
                if(this.clientHeight != document.body.clientHeight) {
                  document.getElementById("username").focus();
                }

                this.$router.replace(this.$route?.query?.from ?? '/').catch(() => {});
              }else{
                this.$message.error(data.message);
                this.errNum = data.data;
                if(this.errNum >=3){
                  this.changeCaptcha();
                }
              }
            }).catch((e) => {
            this.loading = false;
            this.$message.error(e.message);
          });

        });
      },
      /* 更换图形验证码 */
      changeCaptcha() {
        // 这里演示的验证码是后端返回base64格式的形式, 如果后端地址直接是图片请参考忘记密码页面
        getCaptcha()
          .then((data) => {
            this.captcha = data.base64;
            // 自动回填验证码, 实际项目去掉这个
            this.form.code = '';
            this.form.uuid = data.uuid;
          })
          .catch((e) => {
            this.$message.error(e.message);
          });
      },
    },
    watch: {
      'form.username'(newVal) {
        if (this.timer) {
          clearTimeout(this.timer)
        }
        if(newVal){
          this.timer = setTimeout(() => {
            getErrNum(newVal).then((data) => {
              this.errNum = data;
              if(this.errNum >=3){
                this.changeCaptcha();
              }
            })
          }, 500)
        }
      }
    }
  };
</script>

<style lang="scss" scoped>
  /* 背景 */
  .login-wrapper {
    //padding: 50px 20px;
    box-sizing: border-box;
    //background-image: url('@/assets/bg-login.jpg');
    //background-repeat: no-repeat;
    //background-size: cover;
    min-height: 100vh;

    &:before {
      content: '';
      background-color: rgba(0, 0, 0, 0.2);
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
    }
  }
  .login{
    height: 440px;
    margin: 0 auto;
    display: flex;
    width: 890px;
    padding-top: calc((100vh - 443px)/2);
    //position: fixed;
    //top: 50%;
    //left: 50%;
    //transform: translateX(-50%);
    //margin-top: -220px;
  .login_bg{
    align-items: center;
    justify-content: center;
    border-radius: 10px 0 0 10px;
    width: 470px;
    height: 100%;
    //background-image: url('@/assets/login-site-bg.png');
    //background-size: cover;
    img{
      border-radius: 5px;
    }
  }
  /* 卡片 */
  .login-form {
    width: 360px;
    max-width: 100%;
    padding: 50px 30px;
    position: relative;
    overflow: hidden;
    // box-shadow: 0 3px 6px rgba(0, 0, 0, 0.15);
    border-radius: 0 10px 10px 0;
    z-index: 2;

    h4 {
      text-align: left;
      margin: 0 0 25px 0;
      padding:0 20px 16px;
      text-align: center;
      font-weight: bold;
    }
    & > .el-form-item {
      margin-bottom: 25px;
    }
  }
}
  .login-form-right .login-form {
    margin: 0 15% 0 auto;
  }

  .login-form-left .login-form {
    margin: 0 auto 0 15%;
  }

  /* 验证码 */
  .login-input-group {
    display: flex;
    align-items: center;

    :deep(.el-input) {
      flex: 1;
    }
  }

  .login-captcha {
    height: 38px;
    width: 102px;
    margin-left: 10px;
    border-radius: 4px;
    border: 1px solid #dcdfe6;
    text-align: center;
    cursor: pointer;

    &:hover {
      opacity: 0.75;
    }
  }

  .login-btn {
    display: block;
    width: 100%;
  }

  /* 第三方登录图标 */
  .login-oauth-icon {
    color: #fff;
    padding: 5px;
    margin: 0 10px;
    font-size: 18px;
    border-radius: 50%;
    cursor: pointer;
  }

  /* 底部版权 */
  .login-copyright {
    color: #eee;
    padding-top: 20px;
    text-align: center;
    position: relative;
    z-index: 1;
  }

  /* 响应式 */
  @media screen and (min-height: 450px) {
    .login-form-right .login-form,
    .login-form-left .login-form {
      left: auto;
      right: 15%;
      transform: translateX(0);
      margin: -220px auto auto auto;
    }

    .login-form-left .login-form {
      right: auto;
      left: 15%;
    }

    .login-copyright {
      position: absolute;
      bottom: 20px;
      right: 0;
      left: 0;
    }
  }

  @media screen and (max-width: 768px) {
    .login{
      width: 420px;
      padding-top:80px;
      box-shadow: none;
      .logo{
        display: none;
      }
      .login-form{
        box-shadow: 0 3px 6px rgba(0, 0, 0, 0.15);
        border-radius: 10px;
      }
    }
    .login-form-right .login-form,
    .login-form-left .login-form {
      left: 50%;
      right: auto;
      transform: translateX(-50%);
      margin-right: auto;
    }
  }
@media screen and (max-width: 420px) {
  .login {
    width: 96%;
  }
}
</style>
