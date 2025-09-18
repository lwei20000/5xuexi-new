<template>
  <div class="ele-body">
    <el-row :gutter="15" style="margin: 0;">
      <el-col class="elcol1" v-bind="styleResponsive ? { md: 8, sm: 10 } : { span: 8 }" style="padding: 0">
        <el-card shadow="never" body-style="padding: 25px;min-height: calc(100vh - 176px);">
          <div class="user-info-card">
            <div class="user-info-avatar-group" @click="openCropper(1)">
              <img class="user-info-avatar" :src="form.avatar?form.avatar:avatarImg" alt="" />
              <i class="el-icon-upload2"></i>
            </div>
            <h2 class="user-info-name">{{ loginUser.realname }}</h2>
            <pre class="user-info-desc">{{ loginUser.introduction }}</pre>
          </div>
          <div class="user-info-list">
            <div class="user-info-item">
              <i class="el-icon-user">角色：</i>
              <el-tag
                v-for="item in loginUser.roles"
                :key="item.roleId"
                size="mini"
                type="primary"
                :disable-transitions="true"
              >
                {{ item.roleName }}
              </el-tag>
            </div>
          </div>
          <div class="user-info-list">
            <div class="user-info-item">
              <i class="el-icon-office-building">机构：</i>
              <el-tag
                v-for="item in loginUser.orgs"
                :key="item.orgId"
                size="mini"
                type="primary"
                :disable-transitions="true"
              >
                {{ item.orgName }}
              </el-tag>
            </div>
          </div>
          <div class="user-info-list">
            <div class="user-info-item">
              <i class="el-icon-office-building">分组：</i>
              <el-tag
                v-for="item in loginUser.groups"
                :key="item.groupId"
                size="mini"
                type="primary"
                :disable-transitions="true"
              >
                {{ item.groupName }}
              </el-tag>
            </div>
          </div>
          <div class="user-info-list">
            <div class="user-info-item">
              <i class="el-icon-timer">创建时间：</i>
              <span>{{ loginUser.createTime }}</span>
            </div>
          </div>
          <div style="margin: 30px 0 20px 0">
            <el-divider class="ele-divider-dashed ele-divider-base" />
          </div>
          <h6 class="ele-text" style="margin-bottom: 8px">标签：</h6>
          <div class="user-info-tags">
            <el-tag size="mini" type="info">很有想法的</el-tag>
            <el-tag size="mini" type="info">海纳百川</el-tag>
          </div>
        </el-card>
      </el-col>
      <el-col class="elcol2" v-bind="styleResponsive ? { md: 16, sm: 14 } : { span: 16 }"  style="padding: 0;">
        <el-card shadow="never" body-style="padding: 0;min-height: calc(100vh - 126px);">
          <el-tabs v-model="active" class="user-info-tabs">
            <el-tab-pane label="基本信息" name="info">
              <el-form
                ref="form"
                :model="form"
                :rules="rules"
                label-width="150px"
                class="noBorderInput"
                style="padding: 10px 30px 0;"
                @keyup.enter.native="save"
                @submit.native.prevent
              >

                <el-row :gutter="10">
                  <el-col :xs="24" :sm="12">
                    <el-form-item label="账号（身份证号）:" prop="idCard">
                      <el-input
                        clearable
                        :maxlength="18"
                        v-model.trim="form.idCard"
                        placeholder="请输入身份证号"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="12">

                    <el-form-item label="姓名:" prop="realname">
                      <el-input
                        v-model.trim="form.realname"
                        placeholder="请输入姓名"
                        clearable
                      />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-row :gutter="10">
                  <el-col :xs="24" :sm="12">
                    <el-form-item label="性别:" prop="sex">
                      <el-select
                        v-model="form.sex"
                        placeholder="请选择性别"
                        class="ele-fluid"
                        clearable
                        disabled
                      >
                        <el-option label="男" value="男" />
                        <el-option label="女" value="女" />
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="12">
                    <el-form-item label="出生日期:" prop="birthday">
                      <el-date-picker
                        type="date"
                        class="ele-fluid"
                        v-model="form.birthday"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择出生日期"
                        :picker-options="pickerOptions"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                </el-row>


                <el-row :gutter="10">
                  <el-col :xs="24" :sm="12">
                    <el-form-item label="邮箱:" prop="email">
                      <el-input
                        v-model.trim="form.email"
                        placeholder="请输入邮箱"
                        clearable
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="12">
                    <el-form-item label="手机号:" prop="phone">
                      <el-input
                        clearable
                        :maxlength="11"
                        v-model.trim="form.phone"
                        placeholder="请输入手机号"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="10">
                  <el-col :xs="24" :sm="12">
                  </el-col>
                  <el-col :xs="24" :sm="12">
                  </el-col>
                </el-row>





                <el-card shadow="never" style="border: 1px solid #e1f3d8; background-color: #f0f9eb; padding-left: 0px; " :body-style="{ paddingLeft: '0px', paddingBottom: '1px'}">
                  <div class="hint-container" style="padding-left: 17px; padding-bottom: 10px; ">
                    <p><i class="el-icon-warning" style="color: #e6a23c; margin-right: 5px; margin-bottom: 12px" ></i>以下个人信息在下载《登记表》和《学籍表》中使用 </p>
                  </div>
                  <el-row :gutter="10">
                    <el-col :xs="24" :sm="12">
                      <el-form-item label="民族:" prop="nation">
                        <dict-data-select :params="{dictCode: 'nation'}"  v-model="form.nation" />
                      </el-form-item>
                    </el-col>
                    <el-col :xs="24" :sm="12">
                      <el-form-item label="政治面貌:" prop="politics">
                        <dict-data-select :params="{dictCode: 'politics'}"  v-model="form.politics" />
                      </el-form-item>
                    </el-col>
                  </el-row>

                  <el-row :gutter="10">
                    <el-col :xs="24" :sm="12">
                      <el-form-item label="工作单位:" prop="organization">
                        <el-input
                          clearable
                          :maxlength="500"
                          v-model="form.organization"
                          placeholder="请输入工作单位"
                        />
                      </el-form-item>
                    </el-col>
                    <el-col :xs="24" :sm="12">
                      <el-form-item label="家庭住址:" prop="address">
                        <el-input
                          clearable
                          :maxlength="500"
                          v-model="form.address"
                          placeholder="请输入家庭住址"
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row :gutter="10">
                    <el-col :xs="24" :sm="12">
                    </el-col>
                    <el-col :xs="24" :sm="12">
                    </el-col>
                  </el-row>
                  <el-row :gutter="10">
                    <el-col :xs="24" :sm="12">
                      <el-form-item label="身份证正面:" prop="idCard1">
                        <div class="user-info-phone" style="width: 240px;height: 180px" >
                          <img style="width: 100%;height: 100%"  v-if="form.idCard1" :src="form.idCard1" alt="" />
                          <div v-else style="width: 100%;height: 100%;background: #ededed"></div>
                          <i @click="openCropper(3)" style="top: 50%;left: 30%; transform: translate(-50%, -50%);cursor: pointer;" class="el-icon-upload2"></i>
                          <i @click="delImg(3)" style="top: 50%;left: 70%; transform: translate(-50%, -50%);cursor: pointer;" class="el-icon-delete"></i>
                        </div>
                      </el-form-item>
                    </el-col>
                    <el-col :xs="24" :sm="12">
                      <el-form-item label="身份证反面:" prop="idCard2">
                        <div class="user-info-phone" style="width: 240px;height: 180px">
                          <img style="width: 100%;height: 100%"   v-if="form.idCard2" :src="form.idCard2" alt="" />
                          <div v-else style="width: 100%;height: 100%;background: #ededed"></div>
                          <i @click="openCropper(4)" style="top: 50%;left: 30%; transform: translate(-50%, -50%);cursor: pointer;" class="el-icon-upload2"></i>
                          <i @click="delImg(4)" style="top: 50%;left: 70%; transform: translate(-50%, -50%);cursor: pointer;" class="el-icon-delete"></i>
                        </div>
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-form-item label="登记照:" prop="idPhoto">
                    <div class="user-info-phone" style="width: 120px;height: 160px">
                      <img v-if="form.idPhoto" style="width: 100%;height: 100%" :src="form.idPhoto" alt="" />
                      <div v-else style="width: 100%;height: 100%;background: #ededed"></div>
                      <i @click="openCropper(2)" style="top: 50%;left: 30%; transform: translate(-50%, -50%);cursor: pointer;" class="el-icon-upload2"></i>
                      <i @click="delImg(2)" style="top: 50%;left: 70%; transform: translate(-50%, -50%);cursor: pointer;" class="el-icon-delete"></i>
                    </div>
                  </el-form-item>
                </el-card>

                <el-form-item label="个人简介:">
                  <el-input
                    v-model.trim="form.introduction"
                    placeholder="请输入个人简介"
                    :rows="4"
                    type="textarea"
                  />
                </el-form-item>

                <el-form-item>
                  <el-button type="primary" :loading="loading" @click="save">
                    保存更改
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="账号绑定" name="account">
              <div class="user-account-list">
<!--                <div class="user-account-item ele-cell">-->
<!--                  <div class="ele-cell-content">-->
<!--                    <div>密保手机</div>-->
<!--                    <div class="ele-text-secondary">-->
<!--                      已绑定手机: 138****8293-->
<!--                    </div>-->
<!--                  </div>-->
<!--                  <el-link type="primary" :underline="false">去修改</el-link>-->
<!--                </div>-->
<!--                <el-divider />-->
<!--                <div class="user-account-item ele-cell">-->
<!--                  <div class="ele-cell-content">-->
<!--                    <div>密保邮箱</div>-->
<!--                    <div class="ele-text-secondary">-->
<!--                      已绑定邮箱: eleadmin@eclouds.com-->
<!--                    </div>-->
<!--                  </div>-->
<!--                  <el-link type="primary" :underline="false">去修改</el-link>-->
<!--                </div>-->
<!--                <el-divider />-->
                <div class="user-account-item ele-cell">
                  <div class="ele-cell-content">
                    <div>密保问题</div>
                      <div class="ele-text-secondary">
                        {{problemList.length==0 ? '未设置密保问题' : '已设置密保问题'}}
                      </div>
                  </div>
                  <el-link type="primary" @click="settingProblem">{{problemList.length==0 ? '去设置':'去修改'}}</el-link>
                  <el-link type="primary" v-if="problemList.length>0" class="unbind" @click="unbind">解绑</el-link>
                </div>
                <el-divider />
<!--                <div class="user-account-item ele-cell">-->
<!--                  <i class="user-account-icon el-icon-_qq"></i>-->
<!--                  <div class="ele-cell-content">-->
<!--                    <div>绑定QQ</div>-->
<!--                    <div class="ele-text-secondary">当前未绑定QQ账号</div>-->
<!--                  </div>-->
<!--                  <el-link type="primary" :underline="false">去绑定</el-link>-->
<!--                </div>-->
<!--                <el-divider />-->
<!--                <div class="user-account-item ele-cell">-->
<!--                  <i class="user-account-icon el-icon-_wechat"></i>-->
<!--                  <div class="ele-cell-content">-->
<!--                    <div>绑定微信</div>-->
<!--                    <div class="ele-text-secondary">当前未绑定绑定微信账号</div>-->
<!--                  </div>-->
<!--                  <el-link type="primary" :underline="false">去绑定</el-link>-->
<!--                </div>-->
<!--                <el-divider />-->
<!--                <div class="user-account-item ele-cell">-->
<!--                  <i class="user-account-icon el-icon-_alipay"></i>-->
<!--                  <div class="ele-cell-content">-->
<!--                    <div>绑定支付宝</div>-->
<!--                    <div class="ele-text-secondary">-->
<!--                      当前未绑定绑定支付宝账号-->
<!--                    </div>-->
<!--                  </div>-->
<!--                  <el-link type="primary" :underline="false">去绑定</el-link>-->
<!--                </div>-->
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
    <!-- 头像裁剪弹窗 -->
    <ele-cropper-dialog
      :src="cropperImg"
      :show.sync="visible"
      append-to-body
      :lock-scroll="false"
      :destroy-on-close="true"
      :close-on-click-modal="true"
      :options="{aspectRatio: ((cropperType==1||cropperType==2)?3/4:1.58/1), autoCropArea: 1, viewMode: 1, dragMode: 'crop' }"
      @crop="onCrop"
    />

    <settingsecurityquestion :visible.sync="showsecurityquestion" :problemList="problemList" ref="question"/>
  </div>
</template>

<script>
  import EleCropperDialog from 'ele-admin/es/ele-cropper-dialog';
  import DictDataSelect from '@/views/system/dictionary/components/dict-data-select.vue';
  import settingsecurityquestion from './components/setting-security-question.vue'
  import {uploadFile} from "@/api/system/file";
  import {updateUser,getUserQa,unbindQa} from "@/api/user/profile";
  import {clearDeep} from "@/api/common";
  import {API_BASE_URL} from "@/config/setting";
  import {left} from "core-js/internals/array-reduce";

  export default {
    name: 'UserProfile',
    components: { EleCropperDialog,settingsecurityquestion ,DictDataSelect},
    data() {
      return {
        cropperType:'',
        cropperImg:'',
        //默认头像
        avatarImg:API_BASE_URL+'/system/sysStaticPicture/avatar',
        pickerOptions: {
          disabledDate(time) {
            return time.getTime() > Date.now();
          }
        },
        // tab页选中
        active: 'info',
        // 表单数据
        form: {
          realname: '',
          nation:'',
          politics:'',
          organization:'',
          address:'',
          idCard:"",
          idPhoto:'',
          idCard1:'',
          idCard2:'',
          birthday: '',
          sex: '',
          email: '',
          phone: '',
          avatar: '',
          introduction:'',
        },
        // 表单验证规则
        rules: {
          realname: [
            {
              required: true,
              message: '请输入姓名',
              trigger: 'blur'
            }
          ]
        },
        // 保存按钮loading
        loading: false,
        // 是否显示裁剪弹窗
        visible: false,
        // 是否显示安全问题弹窗
        showsecurityquestion:false,
        //安全问题List
        problemList:[]
      };
    },
    computed: {
      // 登录用户信息
      loginUser() {
        return this.$store.state.user.info;
      },
      // 是否开启响应式布局
      styleResponsive() {
        return this.$store.state.theme.styleResponsive;
      }
    },
    created() {
      Object.assign(this.form, this.loginUser);

      // 从身份证号中提取性别和出生日期
      if (this.form.idCard && this.form.idCard.length === 18) {
        const genderCode = parseInt(this.form.idCard.charAt(16), 10);
        this.form.sex = genderCode % 2 === 1 ? '男' : '女';

        const birthYear = this.form.idCard.substring(6, 10);
        const birthMonth = this.form.idCard.substring(10, 12);
        const birthDay = this.form.idCard.substring(12, 14);
        this.form.birthday = `${birthYear}-${birthMonth}-${birthDay}`;
      }
    },
    mounted(){
      //获取安全问题
      getUserQa().then((data) =>{
          this.problemList = data.data;
      }).catch((e) => {
        this.$message.error(e.message);
      });
    },
    methods: {
      left,
      unbind(){
        this.$confirm('确定要解绑吗？', '提示',{ type: 'warning',lockScroll:false })
          .then(() => {
            unbindQa().then((msg =>{
              this.problemList = [];
              this.$message.success(msg);
            })).catch((e) => {
              this.$message.error(e.message);
            });
          })
      },
      settingProblem(){
        this.showsecurityquestion = true;
      },
      /* 保存更改 */
      save() {
        this.$refs.form.validate((valid) => {
          if (!valid) {
            return false;
          }
          this.updateLoginUser(this.form);
        });
      },
      /* 修改登录用户 */
      updateLoginUser(data) {
        const loading = this.$loading({ lock: true });
        this.$store.dispatch('user/setInfo', { ...this.loginUser, ...data });

        const _data = {
          realname: data.realname,
          idCard:data.idCard,
          birthday: data.birthday,
          sex: data.sex,
          email: data.email,
          phone: data.phone,
          avatar: data.avatar,
          nation:data.nation,
          politics:data.politics,
          organization:data.organization,
          address:data.address,
          introduction: data.introduction,
          idPhoto:data.idPhoto,
          idCard1:data.idCard1,
          idCard2:data.idCard2
        };

        clearDeep(_data);
        updateUser(_data)
          .then((msg) => {
            loading.close();
            this.$message.success(msg);
          }).catch((e) => {
            loading.close();
            this.$message.error(e.message);
          });
      },
      /* 头像裁剪完成回调 */
      onCrop(result) {
        if(result){
          var file =null;
          var b = this.dataURLtoBlob(result);
          if(this.cropperType ==1){
            file = this.blobToFile(b,"头像");
          }else if(this.cropperType ==2){
            file = this.blobToFile(b,"证件照");
          }else if(this.cropperType ==3){
            file = this.blobToFile(b,"身份证正面");
          }else if(this.cropperType ==4){
            file = this.blobToFile(b,"身份证反面");
          }

          const loading = this.$loading({ lock: true });
          uploadFile(file)
            .then((res) => {
              loading.close();
              if(this.cropperType ==1){
                this.form.avatar = res.path;
                this.updateLoginUser(this.form);
              }else if(this.cropperType ==2){
                this.form.idPhoto = res.path;
              }else if(this.cropperType ==3){
                this.form.idCard1 = res.path;
              }else if(this.cropperType ==4){
                this.form.idCard2 = res.path;
              }
              this.visible = false;
            }).catch((e) => {
            loading.close();
            this.$message.error(e.message);
          });
        }else{
          this.visible = false;
        }
      },
      /* 打开图片裁剪 */
      openCropper(type) {
        if(type == 1){
          this.cropperImg =  this.form.avatar?this.form.avatar:this.avatarImg;
        }else if(type == 2){
          this.cropperImg =  this.form.idPhoto?this.form.idPhoto:null;
        }else if(type == 3){
          this.cropperImg =  this.form.idCard1?this.form.idCard1:null;
        }else if(type == 4){
          this.cropperImg =  this.form.idCard2?this.form.idCard2:null;
        }
        this.cropperType = type;
        this.visible = true;
      },
      delImg(type){
        if(type == 1){
          console.log(1)
        }else if(type == 2){
          this.form.idPhoto=null;
        }else if(type == 3){
          this.form.idCard1=null;
        }else if(type == 4){
          this.form.idCard2=null;
        }
      },
      //1,先将base64转换为blob
      dataURLtoBlob(dataurl) {
        var arr = dataurl.split(','),
          mime = arr[0].match(/:(.*?);/)[1],
          bstr = atob(arr[1]),
          n = bstr.length,
          u8arr = new Uint8Array(n);
        while (n--) {
          u8arr[n] = bstr.charCodeAt(n);
        }
        return new Blob([u8arr], { type: mime });
      },
      //2,再将blob转换为file
      blobToFile: function (theBlob, fileName) {
        theBlob.lastModifiedDate = new Date();  // 文件最后的修改日期
        theBlob.name = fileName;                // 文件名
        let split = theBlob.type.split("/");
        return new File([theBlob], fileName +"."+ split[1], {type: theBlob.type, lastModified: Date.now()});
      }
    }
  };
</script>

<style lang="scss" scoped>
  :deep(.cropper-bg),:deep(.cropper-canvas),:deep(.cropper-canvas img),:deep(.cropper-view-box img) ,:deep(.ele-cropper-img-group img){
    max-height: 360px!important;
  }
.user-info-phone {
  position: relative;
  overflow: hidden;
  border-radius:5px;
  & > i {
    position: absolute;
    color: #fff;
    font-size: 30px;
    display: none;
    z-index: 2;
  }

  &:hover {
    & > i {
      display: block;
    }

    &:after {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0, 0, 0, 0.3);
    }
  }
}

//弹框不要出现滚动条
.el-dialog__wrapper{
  overflow: hidden;
}

  .ele-body {
    padding-bottom: 0;
  }

  .el-card {
    margin-bottom: 15px;
  }

  /* 用户资料卡片 */
  .user-info-card {
    padding: 8px 0;
    text-align: center;

    .user-info-avatar-group {
      position: relative;
      cursor: pointer;
      margin: 0 auto;
      width: 110px;
      height: 110px;
      border-radius: 50%;
      overflow: hidden;

      & > i {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        color: #fff;
        font-size: 30px;
        display: none;
        z-index: 2;
      }

      &:hover {
        & > i {
          display: block;
        }

        &:after {
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background-color: rgba(0, 0, 0, 0.3);
        }
      }
    }

    .user-info-avatar {
      width: 110px;
      height: 110px;
      border-radius: 50%;
      object-fit: cover;
      background: var(--avatar-background-color);
    }

    .user-info-name {
      font-size: 24px;
      margin-top: 20px;
    }

    .user-info-desc {
      margin-top: 8px;
      font-size: 12px;
      color: #999;
      margin-bottom: 10px;
      font-family: auto;
      white-space: pre-wrap;
      white-space: -moz-pre-wrap;
      white-space: -o-pre-wrap;
      word-wrap: break-word;
    }
  }

  /* 用户信息列表 */
  .user-info-list {
    margin-top: 30px;

    .user-info-item {
      margin-bottom: 16px;
      display: flex;
      align-items: baseline;

      & > i {
        margin-right: 10px;
        font-size: 16px;
      }

      & > span {
        display: block;
      }
    }
  }

  /* 用户标签 */
  .user-info-tags .el-tag {
    margin: 10px 10px 0 0;
  }

  /* 用户账号绑定列表 */
  .user-account-list {
    padding: 16px 20px;

    .user-account-item {
      padding: 15px;

      .ele-text-secondary {
        margin-top: 6px;
      }

      .user-account-icon {
        width: 42px;
        height: 42px;
        line-height: 42px;
        text-align: center;
        color: #fff;
        font-size: 26px;
        border-radius: 50%;
        background-color: #3492ed;
        box-sizing: border-box;

        &.el-icon-_wechat {
          background-color: #4daf29;
          font-size: 28px;
        }

        &.el-icon-_alipay {
          background-color: #1476fe;
          padding-left: 5px;
          font-size: 32px;
        }
      }
    }
  }

  /* tab 页签 */
  .user-info-tabs {
    :deep(.el-tabs__nav-wrap) {
      padding-left: 24px;
    }

    :deep(.el-tabs__item) {
      height: 50px;
      line-height: 50px;
      font-size: 15px;
    }
  }
.elcol2{
  padding-left: 15px!important;
}

/* 响应式 */
@media screen and (max-width: 768px) {
  .elcol1{
    padding-top: 15px!important;
  }
  .elcol2{
    padding: 0!important;
  }
  .el-form{
    padding: 10px!important;
  }
}
</style>
