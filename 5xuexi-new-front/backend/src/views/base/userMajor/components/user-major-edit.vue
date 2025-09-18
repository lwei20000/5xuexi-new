<!-- 用户专业编辑弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="760px"
    :visible="visible"
    :close-on-click-modal="true"
    :title="isUpdate ? '修改用户专业' : '添加用户专业'"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="82px">
      <el-row :gutter="15"  style="padding-bottom: 20px;">
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="用户:" prop="userId">
            <user-select v-model="form.userId" :multiple="false" :initValue="form.initUserValue" ></user-select>
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="学号:" prop="userNumber">
            <el-input
              clearable
              :maxlength="100"
              v-model="form.userNumber"
              placeholder="请输入学号"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="15" style="padding-bottom: 20px;">
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="专业:" prop="majorId">
            <major-select ref="majorSelect" v-model="form.majorId" :initValue="form.initMajorValue" ></major-select>
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="站点:" prop="orgId">
            <org-tree
              v-model="form.orgId"
              :multiple="false"
              :checkStrictly="true"
              :expandOnClickNode="false"
              placeholder="请选择站点"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="15"  style="padding-bottom: 20px;">
        <el-form-item label="学籍照片:" prop="picture">
          <com-image-upload v-model="form.picture" :limit="1"/>
        </el-form-item>
      </el-row>
      <el-row :gutter="15"  style="padding-bottom: 20px;">
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="论文标题:" prop="thesisName">
            <el-input
              clearable
              :maxlength="100"
              v-model="form.thesisName"
              placeholder="请输入论文标题"
            />
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="论文成绩:" prop="thesisScore">
            <el-input
              clearable
              :maxlength="100"
              v-model="form.thesisScore"
              placeholder="请输入论文成绩"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="论文附件:" prop="thesisFileArr">
        <com-upload v-model="form.thesisFileArr" :limit="3"/>
      </el-form-item>
    </el-form>
    <template v-slot:footer>
      <el-button @click="updateVisible(false)">取消</el-button>
      <el-button type="primary" @click="save" :loading="loading">
        保存
      </el-button>
    </template>
  </ele-modal>
</template>

<script>
  import { addUserMajor, updateUserMajor } from '@/api/base/userMajor';
  import UserSelect from "@/views/system/user/components/user-select";
  import MajorSelect from '@/views/base/major/components/major-select.vue';
  import OrgTree from '@/views/system/org/components/org-tree.vue';
  import {clearDeep} from "@/api/common";
  import ComImageUpload from '@/views/system/file/components/com-image-upload.vue';
  import ComUpload from '@/views/system/file/components/com-upload.vue';
  export default {
    components: { UserSelect,ComImageUpload, MajorSelect, OrgTree,ComUpload},
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object
    },
    data() {
      const defaultForm = {
        id: null,
        userNumber:'',
        orgId:'',
        userId: '',
        initUserValue:undefined,
        majorId: '',
        initMajorValue:undefined,
        userPicture: '',
        picture:[],
        thesisName:'',
        thesisScore:'',
        thesisFileArr:[]
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          userId: [
            {
              required: true,
              message: '请选择用户',
              trigger: 'blur'
            }
          ],
          majorId: [
            {
              required: true,
              message: '请选择专业',
              trigger: 'blur'
            }
          ],
          orgId: [
            {
              required: true,
              message: '请选择站点',
              trigger: 'blur'
            }
          ],
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
          if(this.form.picture.length>0){
            this.form.userPicture =this.form.picture[0].url;
          }else{
            this.form.userPicture='';
          }

          this.loading = true;
          const data = {
            ...this.form
          };

          if(data.thesisFileArr && data.thesisFileArr.length>0){
            var list = [];
            for (var n = data.thesisFileArr.length - 1; n >= 0; n--) {
              if(data.thesisFileArr[n].status =='success') {
                var url =data.thesisFileArr[n].response?.data?.path || data.thesisFileArr[n].url;
                list.push({name:data.thesisFileArr[n].name,url: url,status:'success'});
              }
            }
            data.thesisFile =JSON.stringify(list);
          }else{
            data.thesisFile='';
          }

          const saveOrUpdate = this.isUpdate ? updateUserMajor : addUserMajor;
          clearDeep(data);
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
    },
    watch: {
      visible(visible) {
        if (visible) {
          if (this.data) {
            var initMajor = {};
            if(this.data.majorId && this.data.major){
              initMajor.majorId = this.data.majorId;
              initMajor.name = this.data.major.majorYear + '_' + this.data.major.majorCode + '_' + this.data.major.majorName+ '_' + this.data.major.majorType+ '_' + this.data.major.majorGradation+ '_' + this.data.major.majorForms+ '_' + this.data.major.majorLength+"年制";
            }

            var initUser = {};
            if(this.data.userId && this.data.user){
              initUser.userId = this.data.userId;
              initUser.name = this.data.user.username + "_"+ this.data.user.realname
            }

            this.$util.assignObject(this.form, {...this.data,
              majorId: this.data.majorId || undefined,
              initMajorValue : initMajor,
              userId: this.data.userId || undefined,
              initUserValue : initUser
            });

            if(this.data.thesisFile){
              this.form.thesisFileArr=JSON.parse(this.data.thesisFile);
            }else{
              this.form.thesisFileArr=[];
            }

            if(this.data.userPicture){
              var f={uid:1,status:'done',progress:100};
              f.url=this.data.userPicture;
              this.form.picture=[f];
            }else{
              this.form.picture=[];
            }
            this.isUpdate = true;
          } else {
            this.form.userId = undefined;
            this.form.initUserValue = {};
            this.form.majorId = undefined;
            this.form.initMajorValue = {};
            this.form.picture=[];
            this.form.thesisFileArr=[];
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
