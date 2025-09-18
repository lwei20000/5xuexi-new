<!-- 公告编辑弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="960px"
    :visible="visible"
    :close-on-click-modal="true"
    :title="isUpdate ? '修改公告' : '添加公告'"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="102px">
      <el-row :gutter="15">
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="公告标题:" prop="title">
            <el-input
              clearable
              v-model="form.title"
              placeholder="请输入公告标题"
            />
          </el-form-item>
          <el-form-item label="发布范围:" prop="qbReceive">
            <el-radio-group v-model="form.qbReceive"  :disabled="isUpdate" @change="qbReceiveChange">
              <el-radio :label="1">全部</el-radio>
              <el-radio :label="2">其他</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="租户范围:" prop="tenantReceive" v-if="form.qbReceive==2">
            <tenant-tree :disabled="isUpdate"
              v-model="form.tenantReceive"
              :checkStrictly="true"
              :expandOnClickNode="false"
              placeholder="请选择租户"
            />
          </el-form-item>
          <el-form-item label="角色范围:" prop="roleReceive" v-if="form.qbReceive==2">
            <role-tree :disabled="isUpdate"
              v-model="form.roleReceive"
              :checkStrictly="true"
              :expandOnClickNode="false"
              placeholder="请选择角色"
            />
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="公告类型:" prop="systemAnnouncementType">
            <system-announcement-type-select :params="{dictCode: 'system_announcement_type'}" v-model="form.systemAnnouncementType" />
          </el-form-item>

          <el-form-item label="新加入可见:">
            <el-switch
              :active-value="1"
              :inactive-value="0"
              v-model="form.qbReceiveHide"
              :disabled="form.qbReceive == 2 || isUpdate"
            />
            <el-tooltip
              placement="top"
              content="新加入的用户是否可见"
            >
              <i
                class="el-icon-_question"
                style="vertical-align: middle; margin-left: 8px"
              ></i>
            </el-tooltip>
          </el-form-item>
          <el-form-item label="新加入可见:" v-if="form.qbReceive==2">
            <el-switch
              :active-value="1"
              :inactive-value="0"
              v-model="form.tenantReceiveHide"
              :disabled="isUpdate"
            />
            <el-tooltip
              placement="top"
              content="新加入租户的用户是否可见"
            >
              <i
                class="el-icon-_question"
                style="vertical-align: middle; margin-left: 8px"
              ></i>
            </el-tooltip>
          </el-form-item>
          <el-form-item label="新加入可见:" v-if="form.qbReceive==2">
            <el-switch
              :active-value="1"
              :inactive-value="0"
              v-model="form.roleReceiveHide"
              :disabled="isUpdate"
            />
            <el-tooltip
              placement="top"
              content="新加入角色的用户是否可见"
            >
              <i
                class="el-icon-_question"
                style="vertical-align: middle; margin-left: 8px"
              ></i>
            </el-tooltip>
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 24 } : { span: 24 }" style="margin-top: 20px">
          <el-form-item label="用户范围:" prop="userReceive" v-if="form.qbReceive==2">
            <user-all-select v-model="form.userReceive"  :disabled="isUpdate" :initValue="form.initUserReceive" ></user-all-select>
          </el-form-item>
          <el-form-item label="公告内容:" prop="content">
            <!-- 编辑器 -->
            <tinymce-editor
              ref="editor"
              v-model="form.content"
              :init="{ height: 520 }"
            />
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }" style="margin-top:20px" >
          <el-form-item label="公告图片:" prop="picture">
            <com-image-upload v-model="form.picture" :limit="1" />
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }" style="margin-top:20px" >
          <el-form-item label="公告附件:" prop="attachment">
            <com-upload v-model="form.attachment" :limit="3"/>
          </el-form-item>
        </el-col>
      </el-row>
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
  import { addSystemAnnouncement, updateSystemAnnouncement ,getSystemAnnouncement} from '@/api/system/system-announcement';
  import {clearDeep,htmlToString} from "@/api/common";
  import SystemAnnouncementTypeSelect from '@/views/system/dictionary/components/dict-data-select.vue';
  import ComImageUpload from '@/views/system/file/components/com-image-upload.vue';
  import TinymceEditor from '@/components/TinymceEditor/index.vue';
  import TenantTree from '../../tenant/components/tenant-tree.vue';
  import RoleTree from '../../default-role/components/role-tree';
  import UserAllSelect from "@/views/system/user/components/user-all-select";
  import ComUpload from '@/views/system/file/components/com-upload.vue';
  export default {
    components: {SystemAnnouncementTypeSelect ,TenantTree,TinymceEditor,RoleTree,UserAllSelect,ComImageUpload,ComUpload},
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object
    },
    data() {
      const defaultForm = {
        systemAnnouncementId: null,
        title: '',
        systemAnnouncementType:'',
        systemAnnouncementPicture:'',
        picture: [],
        systemAnnouncementAttachment:'',
        attachment:[],
        content:'',
        contentText:'',
        qbReceive:1,
        qbReceiveHide:0,
        tenantReceive:[],
        tenantReceiveHide:0,
        roleReceive:[],
        roleReceiveHide:0,
        userReceive:[],
        initUserReceive:[]
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          title: [
            {
              required: true,
              message: '请输入公告标题',
              trigger: 'blur'
            }
          ],
          systemAnnouncementType: [
            {
              required: true,
              message: '请选择公告类型',
              trigger: 'blur'
            }
          ],
          qbReceive: [
            {
              required: true,
              message: '请选择发布范围',
              trigger: 'blur'
            }
          ],
          content: [
            {
              required: true,
              message: '请输入公告内容',
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
      qbReceiveChange(){
        this.form.qbReceiveHide = 0;
        this.form.tenantReceiveHide = 0;
        this.form.tenantReceive=[];
        this.form.roleReceiveHide = 0;
        this.form.roleReceive=[];
        this.form.userReceive=[];
        this.form.initUserReceive=[];
      },
      /* 保存编辑 */
      save() {
        this.$refs.form.validate((valid) => {
          if (!valid) {
            return false;
          }

          if (this.form.picture.length > 0 && this.form.picture[0].url) {
            this.form.systemAnnouncementPicture = this.form.picture[0].url;
          } else {
            this.form.systemAnnouncementPicture = '';
          }

          if(this.form.attachment && this.form.attachment.length>0){
            var list = [];
            for (var n = this.form.attachment.length - 1; n >= 0; n--) {
              if(this.form.attachment[n].status =='success') {
                var url = this.form.attachment[n].response?.data?.path || this.form.attachment[n].url;
                list.push({name:this.form.attachment[n].name,url: url,status:'success'});
              }
            }
            this.form.systemAnnouncementAttachment =JSON.stringify(list);
          }else{
            this.form.systemAnnouncementAttachment='';
          }

          var text = htmlToString(this.form.content);
          if(text && text.length>200){
            text = text.substring(0,200);
          }
          this.form.contentText = text;
          if(!this.isUpdate){
            var sysSystemAnnouncementReceiveList = [];
            if(this.form.qbReceive == 1){
              var qb = {receiveType:'1'};
              qb.addNewly = this.form.qbReceiveHide;
              sysSystemAnnouncementReceiveList.push(qb);
            }else{
              if(this.form.roleReceive && this.form.roleReceive.length>0){
                for(var i=0;i<this.form.roleReceive.length;i++){
                  var role = {receiveType:'3'};
                  role.addNewly = this.form.roleReceiveHide;
                  role.receiveId = this.form.roleReceive[i];
                  sysSystemAnnouncementReceiveList.push(role);
                }
              }
              if(this.form.tenantReceive && this.form.tenantReceive.length>0){
                for(var j=0;j<this.form.tenantReceive.length;j++){
                  var tenant = {receiveType:'2'};
                  tenant.addNewly = this.form.tenantReceiveHide;
                  tenant.receiveId = this.form.tenantReceive[j];
                  sysSystemAnnouncementReceiveList.push(tenant);
                }
              }
              if(this.form.userReceive && this.form.userReceive.length>0){
                for(var k=0;k<this.form.userReceive.length;k++){
                  var user = {receiveType:'4'};
                  user.addNewly = 0;
                  user.receiveId = this.form.userReceive[k];
                  sysSystemAnnouncementReceiveList.push(user);
                }
              }
            }
          }

          this.form.sysSystemAnnouncementReceiveList = sysSystemAnnouncementReceiveList;
          this.loading = true;
          const saveOrUpdate = this.isUpdate ? updateSystemAnnouncement : addSystemAnnouncement;
          const data = {
            ...this.form
          };
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
            this.loading = true;
            getSystemAnnouncement(this.data.systemAnnouncementId).then((_data) => {
              this.loading = false;

              this.$util.assignObject(this.form, {..._data});

              if (_data.systemAnnouncementPicture) {
                var f = { uid: 1, status: 'done', progress: 100 };
                f.url = _data.systemAnnouncementPicture;
                this.form.picture = [f];
              } else {
                this.form.picture = [];
              }

              if(_data.systemAnnouncementAttachment){
                this.form.attachment=JSON.parse(_data.systemAnnouncementAttachment);
              }else{
                this.form.attachment=[];
              }

              if(_data.sysSystemAnnouncementReceiveList && _data.sysSystemAnnouncementReceiveList.length>0){
                this.form.tenantReceive= [];
                this.form.roleReceive= [];
                this.form.userReceive= [];
                this.form.initUserReceive= [];
                for(var i=0;i<_data.sysSystemAnnouncementReceiveList.length;i++){
                  var item = _data.sysSystemAnnouncementReceiveList[i];
                  if(item.receiveType == '1'){
                    this.form.qbReceive = 1;
                    this.form.qbReceiveHide = item.addNewly;
                  }else{
                    this.form.qbReceive = 2;
                    this.form.qbReceiveHide = 0;
                    if(item.receiveType == '2'){
                      this.form.tenantReceive.push(item.receiveId);
                      this.form.tenantReceiveHide = item.addNewly;
                    }else if(item.receiveType == '3'){
                      this.form.roleReceive.push( item.receiveId);
                      this.form.roleReceiveHide = item.addNewly;
                    }else if(item.receiveType == '4'){
                      var user = {};
                      user.userId = item.receiveId;
                      user.name = item.receiveName;
                      this.form.userReceive.push(item.receiveId);
                      this.form.initUserReceive.push(user);
                    }
                  }
                }
              }
            }).catch((e) => {
              this.loading = false;
              this.$message.error(e.message);
            });
            this.isUpdate = true;
          } else {
            this.form.picture=[];
            this.form.attachment=[];
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
