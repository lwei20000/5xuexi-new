<!-- 消息编辑弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="960px"
    :visible="visible"
    :close-on-click-modal="true"
    :title="isUpdate ? '修改消息' : '添加消息'"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="102px">
      <el-row :gutter="15">
        <el-col v-bind="styleResponsive ? { sm: 24 } : { span: 24 }">
          <el-form-item label="消息标题:" prop="title">
            <el-input
              clearable
              v-model="form.title"
              placeholder="请输入消息标题"
            />
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
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
                         placeholder="请选择机构"
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
          <el-form-item label="消息内容:" prop="content">
            <el-input
              type="textarea"
              :rows="4"
              placeholder="请输入消息内容"
              v-model="form.content">
            </el-input>
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }" style="margin-top:20px" >
          <el-form-item label="消息图标:" prop="picture">
            <com-image-upload v-model="form.picture" :limit="1" />
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 24 } : { span: 24 }">
          <el-form-item label="消息跳转地址:" prop="targetUrl">
            <el-input
              clearable
              v-model="form.targetUrl"
              placeholder="请输入消息跳转地址"
            />
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
  import { addSystemMessage, updateSystemMessage,getSystemMessage } from '@/api/system/system-message';
  import {clearDeep} from "@/api/common";
  import ComImageUpload from '@/views/system/file/components/com-image-upload.vue';
  import TenantTree from '../../tenant/components/tenant-tree.vue';
  import RoleTree from '../../default-role/components/role-tree';
  import UserAllSelect from "@/views/system/user/components/user-all-select";
  export default {
    components: {TenantTree,RoleTree,UserAllSelect,ComImageUpload},
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object
    },
    data() {
      const defaultForm = {
        systemMessageId: null,
        title: '',
        systemMessagePicture:'',
        picture: [],
        content:'',
        targetUrl:'',
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
              message: '请输入消息标题',
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
              message: '请输入消息内容',
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
            this.form.systemMessagePicture = this.form.picture[0].url;
          } else {
            this.form.systemMessagePicture = '';
          }

          if(!this.isUpdate){
            var sysSystemMessageReceiveList = [];
            if(this.form.qbReceive == 1){
              var qb = {receiveType:'1'};
              qb.addNewly = this.form.qbReceiveHide;
              sysSystemMessageReceiveList.push(qb);
            }else{
              if(this.form.roleReceive && this.form.roleReceive.length>0){
                for(var i=0;i<this.form.roleReceive.length;i++){
                  var role = {receiveType:'3'};
                  role.addNewly = this.form.roleReceiveHide;
                  role.receiveId = this.form.roleReceive[i];
                  sysSystemMessageReceiveList.push(role);
                }
              }
              if(this.form.tenantReceive && this.form.tenantReceive.length>0){
                for(var j=0;j<this.form.tenantReceive.length;j++){
                  var tenant = {receiveType:'2'};
                  tenant.addNewly = this.form.tenantReceiveHide;
                  tenant.receiveId = this.form.tenantReceive[j];
                  sysSystemMessageReceiveList.push(tenant);
                }
              }
              if(this.form.userReceive && this.form.userReceive.length>0){
                for(var k=0;k<this.form.userReceive.length;k++){
                  var user = {receiveType:'4'};
                  user.addNewly = 0;
                  user.receiveId = this.form.userReceive[k];
                  sysSystemMessageReceiveList.push(user);
                }
              }
            }
          }

          this.form.sysSystemMessageReceiveList = sysSystemMessageReceiveList;
          this.loading = true;
          const saveOrUpdate = this.isUpdate ? updateSystemMessage : addSystemMessage;
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
            getSystemMessage(this.data.systemMessageId).then((_data) => {
              this.loading = false;
                this.$util.assignObject(this.form, {..._data});

                if (_data.systemMessagePicture) {
                  var f = { uid: 1, status: 'done', progress: 100 };
                  f.url = _data.systemMessagePicture;
                  this.form.picture = [f];
                } else {
                  this.form.picture = [];
                }

                if(_data.sysSystemMessageReceiveList && _data.sysSystemMessageReceiveList.length>0){
                  this.form.tenantReceive= [];
                  this.form.roleReceive= [];
                  this.form.userReceive= [];
                  this.form.initUserReceive= [];
                  for(var i=0;i<_data.sysSystemMessageReceiveList.length;i++){
                    var item = _data.sysSystemMessageReceiveList[i];
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
