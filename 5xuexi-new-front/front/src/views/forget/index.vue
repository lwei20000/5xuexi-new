<template>
  <div :class="[ 'login-wrapper', ['', 'login-form-right', 'login-form-left'][direction] ]">
    <img :src="API_BASE_URL+'/system/sysStaticPicture/login-background'" style="width: 100%;position: absolute;height: 100vh;object-fit: cover;">
    <img style="height: 50px; position: absolute; top: 20px; left: 20px;" :src="API_BASE_URL+'/system/sysStaticPicture/logo'">
    <div class="login " >
      <div class="logo" style=" position: relative;" :style="problemList?.length?'display: none;':''" >
        <img class="login_bg" :src="API_BASE_URL+'/system/sysStaticPicture/login-middle-background'" >
      </div>
    <el-form
      ref="form"
      size="large"
      :model="form"
      :rules="rules"
      class="login-form  ele-bg-white noBorderInput"
      @keyup.enter.native="doSubmit"
      :style="problemList?.length?'width:680px':''"
    >
      <h4>忘记密码</h4>
      <el-row :gutter="15">
        <el-col v-bind="styleResponsive ? { sm: (problemList?.length>0?12:24)} : { span: problemList?.length>0?12:24 }">
          <el-form-item prop="type">
            <el-select
              class="ele-block"
              v-model="form.type"
              placeholder="请选择找回方式"
            >
              <el-option label="根据邮箱找回" value="1" />
              <!--          <el-option label="根据手机号找回" value="2" />-->
              <el-option label="根据安全问题找回" value="3" />
            </el-select>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              show-password
              v-model.trim="form.password"
              placeholder="请输入新的登录密码"
              prefix-icon="el-icon-lock"
              auto-complete="new-password"
              autocomplete="new-password"
            />
          </el-form-item>
          <el-form-item prop="password2">
            <el-input
              show-password
              v-model.trim="form.password2"
              placeholder="请再次输入登录密码"
              prefix-icon="el-icon-key"
              auto-complete="new-password"
              autocomplete="new-password"
            />
          </el-form-item>

          <el-form-item prop="phone" v-if="form.type=='2'">
            <el-input
              clearable
              v-model.trim="form.phone"
              placeholder="请输入绑定手机号"
              prefix-icon="el-icon-mobile-phone"
            />
          </el-form-item>
          <el-form-item prop="email"  v-if="form.type=='1'">
            <el-input
              clearable
              v-model.trim="form.email"
              placeholder="请输入绑定邮箱"
              prefix-icon="el-icon-mobile-phone"
            />
          </el-form-item>
          <el-form-item prop="code" v-if="form.type=='1' || form.type=='2'">
            <div class="login-input-group">
              <el-input
                clearable
                v-model.trim="numCode"
                placeholder="请输入验证码"
                prefix-icon="el-icon-_vercode"
              />
              <el-button
                size="large"
                :disabled="!!countdownTimer"
                style="margin-left: 10px"
                @click="showImgCodeCheck"
              >
                <span v-if="!countdownTimer">发送验证码</span>
                <span v-else>已发送 {{ countdownTime }} s</span>
              </el-button>
            </div>
          </el-form-item>

          <el-form-item prop="username"  v-if="form.type=='3'">
            <div class="login-input-group">
              <el-input
                clearable
                :disabled="disabledUsername"
                v-model.trim="form.username"
                placeholder="请输入用户名"
                prefix-icon="el-icon-mobile-phone"
              />
              <el-button
                size="large"
                style="margin-left: 10px"
                :disabled="disabledUsername"
                @click="showProblemCheck"
                :loading="loadingProblem">
                <span>确定</span>
              </el-button>
            </div>
          </el-form-item>
        </el-col>
        <el-col  v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
              <el-form-item  v-for="(item,index) in problemList" :key="index" :prop="`answer${index}`">
                <div class="login-input-group">
                  <label>{{'问题'+ (index+1)+'：'}}</label>
                  <span>{{item.problem}}</span>
                </div>
                <div class="login-input-group">
                  <label>{{'答案'+ (index+1)+'：'}}</label>
                  <el-input
                    clearable
                    v-model.trim="form.answers[index]"
                    placeholder="请输入安全问题答案"
                  />
                </div>
              </el-form-item>
        </el-col>
      </el-row>

      <div class="el-form-item">
        <el-link
          type="primary"
          :underline="false"
          class="ele-pull-right"
          @click="$router.replace('/login')"
        >
          返回登录
        </el-link>
      </div>
      <div class="el-form-item">
        <el-button
          size="large"
          type="primary"
          class="login-btn"
          :loading="loading"
          @click="doSubmit"
        >
          修改密码
        </el-button>
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
    <!-- 图形验证码弹窗 -->
    <ele-modal :lock-scroll="false" width="320px" title="发送验证码" :visible.sync="showImgCode">
      <div class="login-input-group" style="margin-bottom: 15px">
        <el-input
          size="large"
          v-model="imgCode"
          placeholder="输入图形验证码"
          @keyup.enter.native="sendCode"
        />
        <img
          alt=""
          :src="captcha"
          class="login-captcha"
          @click="changeImgCode"
        />
      </div>
      <el-button
        size="large"
        type="primary"
        class="login-btn"
        :loading="codeLoading"
        @click="sendCode"
      >
        立即发送
      </el-button>
    </ele-modal>
  </div>
</template>

<script>
import {getCaptcha, modifPassword, problemPassword, sendCaptcha} from "@/api/login";
import {getUserNotQa} from "@/api/user/profile";
import {API_BASE_URL} from "@/config/setting";

  export default {
    // eslint-disable-next-line vue/multi-word-component-names
    name: 'Forget',
    data() {
      return {
        API_BASE_URL:API_BASE_URL,
        captcha:'',
        // 登录框方向, 0居中, 1居右, 2居左
        direction: 0,
        // 加载状态
        loading: false,
        loadingProblem:false,
        disabledUsername:false,
        // 表单数据
        form: {
          type:'1',
          phone: '',
          email:'',
          username:'',
          password: '',
          password2: '',
          code: '',
          uuid: '',
          answers:[]
        },
        problemList:[],
        // 表单验证规则
        rules: {
          password: [
            {
              required: true,
              message: '请输入新的登录密码',
              trigger: 'change'
            }
          ],
          password2: [
            {
              validator: (_rule, value, callback) => {
                if (!value) {
                  return callback(new Error('请再次输入登录密码'));
                }
                if (value !== this.form.password) {
                  return callback(new Error('两次密码输入不一致'));
                }
                callback();
              },
              trigger: 'change'
            }
          ],
        },
        // 用于刷新验证码
        v: new Date().getTime(),
        // 是否显示图形验证码弹窗
        showImgCode: false,
        // 图形验证码
        imgCode: '',
        // 验证码
        numCode: '',
        // 发送验证码按钮loading
        codeLoading: false,
        // 验证码倒计时时间
        countdownTime: 30,
        // 验证码倒计时定时器
        countdownTimer: null
      };
    },
    computed: {
      // 是否开启响应式布局
      styleResponsive() {
        return this.$store.state.theme.styleResponsive;
      }
    },
    methods: {
      /* 提交 */
      doSubmit() {
        this.$refs.form.validate((valid) => {
          if (!valid) {
            return false;
          }

          if (this.form.password2 !== this.form.password) {
            this.$message.error('两次密码输入不一致');
            return;
          }

          if(this.form.type=='1' && !this.form.email){
            this.$message.error('请输入邮箱');
            return;
          }

          if(this.form.type=='2' && !this.form.phone){
            this.$message.error('请输入手机号');
            return;
          }

          if(this.form.type=='3'){
            if(!this.form.username){
              this.$message.error('请输入用户名');
              return;
            }
            if(!this.form.answers || this.form.answers.length !=3){
              this.$message.error('请填写安全问题答案');
              return;
            }
          }

          if(this.form.type=='1' || this.form.type=='2'){
            if (!this.numCode) {
              this.$message.error('请输入验证码');
              return;
            }
            this.form.code = this.numCode;
          }

          var updatePassword = this.form.type=='3'? problemPassword:modifPassword
          this.loading = true;
          updatePassword(this.form).then(() => {
            this.$message.success('密码修改成功');
            this.$router.replace('/login');
          }).catch((e) => {
            this.$message.error(e.message);
            this.loading = false;
          });
        });
      },
      /* 更换图形验证码 */
      changeImgCode() {
        // 这里演示的验证码是后端返回base64格式的形式, 如果后端地址直接是图片请参考忘记密码页面
        getCaptcha().then((data) => {
            this.captcha = data.base64;
            // // 实际项目后端一般会返回验证码的key而不是直接返回验证码的内容, 登录用key去验证, 可以根据自己后端接口修改
            this.form.code = '';
            this.form.uuid = data.uuid;
          }).catch((e) => {
            this.$message.error(e.message);
          });
      },
      showProblemCheck(){
        if (!this.form.username) {
          this.$message.error('请输入用户名');
          return;
        }
        this.disabledUsername = true;
        this.loadingProblem = true;
        //获取安全问题
        getUserNotQa({loginName:this.form.username}).then((data) =>{
          this.problemList = data.data;
          if(!this.problemList || this.problemList.length == 0){
            this.disabledUsername = false;
            this.$message.error('未设置安全问题');
          }
          this.loadingProblem = false;
        }).catch((e) => {
          this.loadingProblem = false;
          this.$message.error(e.message);
        });
      },
      /* 显示发送短信验证码弹窗 */
      showImgCodeCheck() {
        if (this.form.type=='2' && !this.form.phone) {
          this.$message.error('请输入手机号码');
          return;
        }
        if (this.form.type=='1' && !this.form.email) {
          this.$message.error('请输入邮箱');
          return;
        }
        this.imgCode = '';
        this.changeImgCode();
        this.showImgCode = true;
      },
      /* 发送验证码 */
      sendCode() {
        if (!this.imgCode) {
          this.$message.error('请输入图形验证码');
          return;
        }
        this.codeLoading = true;
        this.form.code = this.imgCode;
        sendCaptcha(this.form).then(() => {
          this.$message.success('验证码发送成功, 请注意查收!');
          this.showImgCode = false;
          this.codeLoading = false;
          this.startCountdownTimer();
        }).catch((e) => {
            this.changeImgCode();
            this.codeLoading = false;
            this.$message.error(e.message);
        });
      },
      /* 开始对按钮进行倒计时 */
      startCountdownTimer() {
        this.countdownTime = 60;
        this.countdownTimer = setInterval(() => {
          if (this.countdownTime <= 1) {
            clearInterval(this.countdownTimer);
            this.countdownTimer = null;
          }
          this.countdownTime--;
        }, 1000);
      }
    },
    beforeDestroy() {
      if (this.countdownTimer) {
        clearInterval(this.countdownTimer);
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
  height: 540px;
  margin: 0 auto;
  display: flex;
  width: 890px;
  padding-top: calc((100vh - 543px)/2);
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
    overflow-y: scroll;
    width: 360px;
    max-width: 100%;
    padding: 20px 30px;
    position: relative;
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

    .login-captcha {
      width: 102px;
      height: 38px;
      margin-left: 10px;
      border-radius: 4px;
      border: 1px solid #dcdfe6;
      text-align: center;
      cursor: pointer;

      &:hover {
        opacity: 0.75;
      }
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
    text-align: center;
    padding-top: 20px;
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
    margin: -270px auto auto auto;
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
    .login-form-right .login-form,
    .login-form-left .login-form {
      left: 50%;
      right: auto;
      margin-right: auto;
      transform: translateX(-50%);
    }
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
  }
@media screen and (max-width: 420px) {
  .login {
    width: 96%;
  }
}
</style>
