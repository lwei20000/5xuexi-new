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
          <el-form-item label="机构范围:" prop="orgReceive" v-if="form.qbReceive==2">
            <org-tree :disabled="isUpdate"
              v-model="form.orgReceive"
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
          <el-form-item label="分组范围:" prop="groupReceive" v-if="form.qbReceive==2">
            <group-tree :disabled="isUpdate"
                        v-model="form.groupReceive"
                        :checkStrictly="true"
                        :expandOnClickNode="false"
                        placeholder="请选择分组"
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
              content="新加入组织的用户是否可见"
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
              v-model="form.orgReceiveHide"
              :disabled="isUpdate"
            />
            <el-tooltip
              placement="top"
              content="新加入机构的用户是否可见"
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
          <el-form-item label="新加入可见:" v-if="form.qbReceive==2">
            <el-switch
              :active-value="1"
              :inactive-value="0"
              v-model="form.groupReceiveHide"
              :disabled="isUpdate"
            />
            <el-tooltip
              placement="top"
              content="新加入分组的用户是否可见"
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
            <user-select v-model="form.userReceive"  :disabled="isUpdate" :initValue="form.initUserReceive" ></user-select>
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
import {addMessage, getMessage, updateMessage} from '@/api/system/message';
  import {clearDeep} from "@/api/common";
  import ComImageUpload from '@/views/system/file/components/com-image-upload.vue';
  import OrgTree from '../../org/components/org-tree.vue';
  import GroupTree from '../../group/components/group-tree';
  import RoleTree from '../../role/components/role-tree';
  import UserSelect from "@/views/system/user/components/user-select";
  export default {
    components: {OrgTree,RoleTree,UserSelect,ComImageUpload,GroupTree},
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object
    },
    data() {
      const defaultForm = {
        messageId: null,
        title: '',
        messagePicture:'',
        picture: [],
        content:'',
        targetUrl:'',
        qbReceive:1,
        qbReceiveHide:0,
        orgReceive:[],
        orgReceiveHide:0,
        roleReceive:[],
        roleReceiveHide:0,
        groupReceive:[],
        groupReceiveHide:0,
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
        this.form.orgReceiveHide = 0;
        this.form.orgReceive=[];
        this.form.roleReceiveHide = 0;
        this.form.roleReceive=[];
        this.form.groupReceiveHide = 0;
        this.form.groupReceive=[];
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
            this.form.messagePicture = this.form.picture[0].url;
          } else {
            this.form.messagePicture = '';
          }

          if(!this.isUpdate){
            var sysMessageReceiveList = [];
            if(this.form.qbReceive == 1){
              var qb = {receiveType:'1'};
              qb.addNewly = this.form.qbReceiveHide;
              sysMessageReceiveList.push(qb);
            }else{
              if(this.form.roleReceive && this.form.roleReceive.length>0){
                for(var i=0;i<this.form.roleReceive.length;i++){
                  var role = {receiveType:'3'};
                  role.addNewly = this.form.roleReceiveHide;
                  role.receiveId = this.form.roleReceive[i];
                  sysMessageReceiveList.push(role);
                }
              }
              if(this.form.orgReceive && this.form.orgReceive.length>0){
                for(var j=0;j<this.form.orgReceive.length;j++){
                  var org = {receiveType:'2'};
                  org.addNewly = this.form.orgReceiveHide;
                  org.receiveId = this.form.orgReceive[j];
                  sysMessageReceiveList.push(org);
                }
              }
              if(this.form.userReceive && this.form.userReceive.length>0){
                for(var k=0;k<this.form.userReceive.length;k++){
                  var user = {receiveType:'4'};
                  user.addNewly = 0;
                  user.receiveId = this.form.userReceive[k];
                  sysMessageReceiveList.push(user);
                }
              }
              if(this.form.groupReceive && this.form.groupReceive.length>0){
                for(var m=0;m<this.form.groupReceive.length;m++){
                  var group = {receiveType:'5'};
                  group.addNewly = this.form.groupReceiveHide;
                  group.receiveId = this.form.groupReceive[m];
                  sysMessageReceiveList.push(group);
                }
              }
            }
          }

          this.form.sysMessageReceiveList = sysMessageReceiveList;
          this.loading = true;
          const saveOrUpdate = this.isUpdate ? updateMessage : addMessage;
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
            getMessage(this.data.messageId).then((_data) => {
              this.loading = false;
              this.$util.assignObject(this.form, {..._data});

              if (_data.messagePicture) {
                var f = { uid: 1, status: 'done', progress: 100 };
                f.url = _data.messagePicture;
                this.form.picture = [f];
              } else {
                this.form.picture = [];
              }

              if(_data.sysMessageReceiveList && _data.sysMessageReceiveList.length>0){
                this.form.orgReceive= [];
                this.form.roleReceive= [];
                this.form.groupReceive= [];
                this.form.userReceive= [];
                this.form.initUserReceive= [];
                for(var i=0;i<_data.sysMessageReceiveList.length;i++){
                  var item = _data.sysMessageReceiveList[i];
                  if(item.receiveType == '1'){
                    this.form.qbReceive = 1;
                    this.form.qbReceiveHide = item.addNewly;
                  }else{
                    this.form.qbReceive = 2;
                    this.form.qbReceiveHide = 0;

                    if(item.receiveType == '2'){
                      this.form.orgReceive.push(item.receiveId);
                      this.form.orgReceiveHide = item.addNewly;
                    }else if(item.receiveType == '3'){
                      this.form.roleReceive.push( item.receiveId);
                      this.form.roleReceiveHide = item.addNewly;
                    }else if(item.receiveType == '4'){
                      var user = {};
                      user.userId = item.receiveId;
                      user.name = item.receiveName;
                      this.form.userReceive.push(item.receiveId);
                      this.form.initUserReceive.push(user);
                    }else if(item.receiveType == '5'){
                      this.form.groupReceive.push(item.receiveId);
                      this.form.groupReceiveHide = item.addNewly;
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
