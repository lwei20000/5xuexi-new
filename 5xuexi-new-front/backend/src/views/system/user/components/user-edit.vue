<!-- 用户编辑弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="780px"
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="true"
    custom-class="ele-dialog-form"
    :title="isUpdate ? '修改用户' : '添加用户'"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-row :gutter="15">
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="账号:" prop="username">
            <el-input
              clearable
              :maxlength="100"
              :disabled="isUpdate"
              v-model="form.username"
              placeholder="请输入账号"
            />
          </el-form-item>
          <el-form-item label="姓名:" prop="realname">
            <el-input
              clearable
              :maxlength="100"
              v-model="form.realname"
              placeholder="请输入姓名"
            />
          </el-form-item>
          <el-form-item label="性别:" prop="sex">
            <el-select
              clearable
              class="ele-block"
              v-model="form.sex"
              placeholder="请选择性别"
            >
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
          <el-form-item label="角色:" prop="roleIds">
            <role-tree
              :key="forcedRefresh"
              v-model="form.roleIds"
              :checkStrictly="true"
              :expandOnClickNode="false"
              placeholder="请选择角色"
            />
          </el-form-item>
          <el-form-item label="机构:" prop="orgIds">
            <org-tree
              :key="forcedRefresh"
              v-model="form.orgIds"
              :checkStrictly="true"
              :expandOnClickNode="false"
              placeholder="请选择机构"
            />
          </el-form-item>
          <el-form-item label="分组:" prop="groupIds">
            <group-tree
              :key="forcedRefresh"
              v-model="form.groupIds"
              :checkStrictly="true"
              :expandOnClickNode="false"
              placeholder="请选择分组"
            />
          </el-form-item>
          <el-form-item label="邮箱:" prop="email">
            <el-input
              clearable
              :maxlength="100"
              v-model="form.email"
              placeholder="请输入邮箱"
            />
          </el-form-item>

        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="用户类型:" prop="userType">
            <dict-data-select :params="{dictCode: 'user_type'}" :multiple="true" v-model="form.userType" />
          </el-form-item>
          <el-form-item label="手机号:" prop="phone">
            <el-input
              clearable
              :maxlength="11"
              v-model="form.phone"
              placeholder="请输入手机号"
            />
          </el-form-item>
          <el-form-item label="出生日期:" prop="birthday">
            <el-date-picker
              type="date"
              class="ele-fluid"
              v-model="form.birthday"
              value-format="yyyy-MM-dd"
              placeholder="请选择出生日期"
              :picker-options="pickerOptions"
            />
          </el-form-item>
          <el-form-item label="身份证号:" prop="idCard">
            <el-input
              clearable
              :maxlength="18"
              v-model="form.idCard"
              placeholder="请输入身份证号"
            />
          </el-form-item>
          <el-form-item v-if="!isUpdate" label="登录密码:" prop="password">
            <el-input
              show-password
              :maxlength="100"
              v-model="form.password"
              placeholder="请输入登录密码"
              auto-complete="new-password"
            />
          </el-form-item>
          <el-form-item label="用户头像:" prop="avatar">
            <div class="user-info-phone" style="width: 120px;height: 160px" >
              <img style="width: 100%;height: 100%"  v-if="form.avatar" :src="form.avatar" alt="" />
              <div v-else style="width: 100%;height: 100%;background: #ededed"></div>
              <i @click="openCropper(1)" style="top: 50%;left: 30%; transform: translate(-50%, -50%);cursor: pointer;" class="el-icon-upload2"></i>
              <i @click="delImg(1)" style="top: 50%;left: 70%; transform: translate(-50%, -50%);cursor: pointer;" class="el-icon-delete"></i>
            </div>
          </el-form-item>

        </el-col>

      </el-row>
      <el-row :gutter="15">
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="民族:" prop="nation">
            <dict-data-select :params="{dictCode: 'nation'}"  v-model="form.nation" />
          </el-form-item>
          <el-form-item label="身份证正面:" prop="idCard1">
            <div class="user-info-phone" style="width: 240px;height: 180px" >
              <img style="width: 100%;height: 100%"  v-if="form.idCard1" :src="form.idCard1" alt="" />
              <div v-else style="width: 100%;height: 100%;background: #ededed"></div>
              <i @click="openCropper(3)" style="top: 50%;left: 30%; transform: translate(-50%, -50%);cursor: pointer;" class="el-icon-upload2"></i>
              <i @click="delImg(3)" style="top: 50%;left: 70%; transform: translate(-50%, -50%);cursor: pointer;" class="el-icon-delete"></i>
            </div>
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="政治面貌:" prop="politics">
            <dict-data-select :params="{dictCode: 'politics'}"  v-model="form.politics" />
          </el-form-item>
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

      <el-row :gutter="15">
        <el-col v-bind="styleResponsive ? { sm: 24 } : { span: 24 }">
          <el-form-item label="工作单位:" prop="organization">
            <el-input
              clearable
              :maxlength="500"
              v-model="form.organization"
              placeholder="请输入工作单位"
            />
          </el-form-item>
          <el-form-item label="家庭住址:" prop="address">
            <el-input
              clearable
              :maxlength="500"
              v-model="form.address"
              placeholder="请输入家庭住址"
            />
          </el-form-item>
        <el-form-item label="个人简介:">
          <el-input
            :rows="4"
            clearable
            type="textarea"
            :maxlength="20000"
            v-model="form.introduction"
            placeholder="请输入个人简介"
          />
        </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template v-slot:footer>
      <el-button @click="updateVisible(false)">取消</el-button>
      <el-button type="primary" :loading="loading" @click="save">
        保存
      </el-button>
    </template>

    <!-- 头像裁剪弹窗 -->
    <ele-cropper-dialog
      :src="cropperImg"
      :show.sync="visibleCropper"
      append-to-body
      :lock-scroll="false"
      :destroy-on-close="true"
      :close-on-click-modal="true"
      :options="{aspectRatio: ((cropperType==1||cropperType==2)?3/4:1.58/1), autoCropArea: 1, viewMode: 1, dragMode: 'crop' }"
      @crop="onCrop"
    />
  </ele-modal>
</template>

<script>
  import { emailReg, phoneReg } from 'ele-admin';
  import RoleTree from '../../role/components/role-tree';
  import OrgTree from '../../org/components/org-tree.vue';
  import GroupTree from '../../group/components/group-tree.vue';
  import DictDataSelect from '@/views/system/dictionary/components/dict-data-select.vue';
  import { addUser, updateUser } from '@/api/system/user';
  import {clearDeep} from "@/api/common";
  import {uploadFile} from "@/api/system/file";
  import EleCropperDialog from 'ele-admin/es/ele-cropper-dialog';
  export default {
    components: { RoleTree,OrgTree,DictDataSelect,GroupTree,EleCropperDialog},
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object,
      // 机构id
      orgId: null,
      // 角色id
      roleId: null,
      // 分组id
      groupId: null
    },
    data() {
      const defaultForm = {
        userId: null,
        username: '',
        realname: '',
        userType:'',
        idCard:"",
        sex: null,
        nation:'',
        politics:'',
        organization:'',
        address:'',
        roleIds: [],
        orgIds: [],
        groupIds: [],
        email: '',
        phone: '',
        birthday: '',
        password: '',
        introduction: '',
        idCard1:'',
        idCard2:'',
        avatar: '',
        idPhoto: ''
      };
      return {
        visibleCropper:false,
        cropperType:'',
        cropperImg:'',
        forcedRefresh: 0,
        pickerOptions: {
          disabledDate(time) {
            return time.getTime() > Date.now();
          }
        },
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          username: [
            {
              required: true,
              trigger: 'blur',
              message: '请输入用户账号'
            }
          ],
          realname: [
            {
              required: true,
              message: '请输入姓名',
              trigger: 'blur'
            }
          ],
          roleIds: [
            {
              required: true,
              message: '请选择角色',
              trigger: 'blur'
            }
          ],
          email: [
            {
              pattern: emailReg,
              message: '邮箱格式不正确',
              trigger: 'blur'
            }
          ],
          password: [
            {
              required: true,
              pattern: /^[\S]{5,18}$/,
              message: '密码必须为5-18位非空白字符',
              trigger: 'blur'
            }
          ],
          phone: [
            {
              pattern: phoneReg,
              message: '手机号格式不正确',
              trigger: 'blur'
            }
          ]
        },
        // 提交状态
        loading: false,
        // 是否是修改
        isUpdate: false
      };
    },
    computed: {
      // 是否开启响应式布局
      styleResponsive() {
        return this.$store.state.theme.styleResponsive;
      }
    },
    methods: {
      /* 保存编辑 */
      save() {
        this.$refs.form.validate((valid) => {
          if (!valid) {
            return false;
          }
          this.loading = true;
          const data = {
            ...this.form,
            roles: this.form.roleIds.map((d) => {
              return { roleId: d };
            }),
            orgs: this.form.orgIds.map((d) => {
              return { orgId: d };
            }),
            groups: this.form.groupIds.map((d) => {
              return { groupId: d };
            })
          };

          const saveOrUpdate = this.isUpdate ? updateUser : addUser;
          clearDeep(data);
          if(data.userType){
            data.userType = JSON.stringify(data.userType);
          }

          saveOrUpdate(data)
            .then((msg) => {
              this.loading = false;
              this.$message.success(msg);
              this.updateVisible(false);
              this.$emit('done');
            })
            .catch((e) => {
              this.loading = false;
              this.$message.error(e.message);
            });
        });
      },
      /* 更新visible */
      updateVisible(value) {
        this.$emit('update:visible', value);
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
              }else if(this.cropperType ==2){
                this.form.idPhoto = res.path;
              }else if(this.cropperType ==3){
                this.form.idCard1 = res.path;
              }else if(this.cropperType ==4){
                this.form.idCard2 = res.path;
              }
              this.visibleCropper = false;
            }).catch((e) => {
            loading.close();
            this.$message.error(e.message);
          });
        }else{
          this.visibleCropper = false;
        }
      },
      /* 打开图片裁剪 */
      openCropper(type) {
        if(type == 1){
          this.cropperImg =  this.form.avatar?this.form.avatar:null;
        }else if(type == 2){
          this.cropperImg =  this.form.idPhoto?this.form.idPhoto:null;
        }else if(type == 3){
          this.cropperImg =  this.form.idCard1?this.form.idCard1:null;
        }else if(type == 4){
          this.cropperImg =  this.form.idCard2?this.form.idCard2:null;
        }
        this.cropperType = type;
        this.visibleCropper = true;
      },
      delImg(type){
        if(type == 1){
          this.form.avatar=null;
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
    },
    watch: {
      visible(visible) {
        if (visible) {
          this.forcedRefresh = Date.now();
          if (this.data) {
            this.$util.assignObject(this.form, {
              ...this.data,
              roleIds: this.data.roles?this.data.roles.map((d) => d.roleId):[],
              orgIds: this.data.orgs?this.data.orgs.map((d) => d.orgId):[],
              groupIds: this.data.groups?this.data.groups.map((d) => d.groupId):[],
              password: ''
            });
            this.isUpdate = true;
          } else {
            if(this.orgId){
              this.form.orgIds = [this.orgId];
            }
            if(this.roleId){
              this.form.roleIds = [this.roleId];
            }
            if(this.groupId){
              this.form.groupIds = [this.groupId];
            }
            this.isUpdate = false;
          }
        } else {
          this.$refs.form.clearValidate();
          this.form = { ...this.defaultForm };
        }
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
</style>
