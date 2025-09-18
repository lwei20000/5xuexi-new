<!-- 机构编辑弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="780px"
    :visible="visible"
    :close-on-click-modal="true"
    custom-class="ele-dialog-form"
    :title="isUpdate ? '修改机构' : '添加机构'"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="102px">
      <el-row :gutter="15">
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="上级机构:" prop="parentId" :rules="(!isUpdate || (isUpdate && data?.parentId>0)) ?[{ required: true, message: '请选择上级机构', trigger: 'blur' }]:[]" >
            <org-tree ref="orgTree"
              :disabled="isUpdate && data?.parentId==0"
              v-model="form.parentId"
              :multiple="false"
              :expandOnClickNode="false"
              placeholder="请选择上级机构"
            />
          </el-form-item>
          <el-form-item label="机构代码:" prop="orgCode">
            <el-input
              clearable
              :disabled="isUpdate && data?.parentId==0"
              :maxlength="200"
              v-model="form.orgCode"
              placeholder="请输入机构代码"
            />
          </el-form-item>
          <el-form-item label="机构名称:" prop="orgName">
            <el-input
              clearable
              :disabled="isUpdate && data?.parentId==0"
              :maxlength="200"
              v-model="form.orgName"
              placeholder="请输入机构名称"
            />
          </el-form-item>
          <el-form-item label="机构全称:" prop="orgFullName">
            <el-input
              clearable
              disabled
              :maxlength="100"
              v-model="form.orgFullName"
              placeholder="请输入机构全称"
            />
          </el-form-item>
          <el-form-item label="机构全称代码:" prop="orgFullCode">
            <el-input
              clearable
              disabled
              :maxlength="100"
              v-model="form.orgFullCode"
              placeholder="请输入机构全称代码"
            />
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="机构类型:" prop="orgType">
            <org-type-select :params="{dictCode: 'org_type'}"  :disabled="isUpdate && data?.parentId==0" v-model="form.orgType" />
          </el-form-item>
          <el-form-item label="排序号:" prop="sortNumber">
            <el-input-number
              :disabled="isUpdate && data?.parentId==0"
              :min="0"
              :max="99999"
              v-model="form.sortNumber"
              controls-position="right"
              placeholder="请输入排序号"
              class="ele-fluid ele-text-left"
            />
          </el-form-item>
          <el-form-item label="备注:">
            <el-input
              :rows="4"
              type="textarea"
              :maxlength="200"
              v-model="form.comments"
              placeholder="请输入备注"
            />
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 24 } : { span: 24 }">
        <el-form-item label="机构主管:" prop="leaders">
           <user-select v-model="form.leaders" :initValue="form.initLeaderValue" ></user-select>
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
  </ele-modal>
</template>

<script>
  import OrgTree from './org-tree.vue';
  import OrgTypeSelect from '@/views/system/dictionary/components/dict-data-select.vue';
  import {
    addOrg,
    updateOrg
  } from '@/api/system/org';
  import {clearDeep} from "@/api/common";
  import UserSelect from "@/views/system/user/components/user-select";

  export default {
    components: {UserSelect, OrgTree, OrgTypeSelect },
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object,
      // 上级id
      parentId: Number,
      // 机构数据
      orgList: Array
    },
    data() {
      const defaultForm = {
        orgId: null,
        parentId: null,
        orgCode: '',
        orgName: '',
        orgFullName: '',
        orgFullCode:'',
        orgType: null,
        sortNumber: null,
        comments: '',
        leaders:[],
        initLeaderValue:[],
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          orgCode: [
            {
              required: true,
              message: '请输入机构CODE',
              trigger: 'blur'
            }
          ],
          orgName: [
            {
              required: true,
              message: '请输入机构名称',
              trigger: 'blur'
            }
          ],
          orgType: [
            {
              required: true,
              message: '请选择机构类型',
              trigger: 'blur'
            }
          ],
          sortNumber: [
            {
              required: true,
              message: '请输入排序号',
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
            parentId: this.form.parentId || 0,
            leaders: this.form.leaders.map((d) => {
              return { userId: d };
            })
          };
          const saveOrUpdate = this.isUpdate ? updateOrg : addOrg;
          clearDeep(data);
          saveOrUpdate(data)
            .then((msg) => {
              this.loading = false;
              this.$message.success(msg);
              //更新机构
              this.$refs.orgTree.init();
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
      }
    },
    watch: {
      visible(visible) {
        if (visible) {
          if (this.data) {
            this.$util.assignObject(this.form, {
              ...this.data,
              parentId: this.data.parentId || '',
              leaders: this.data.leaders?this.data.leaders.map((d) => d.userId):[],
              initLeaderValue: this.data.leaders?this.data.leaders.map((d) =>{
                d.name = d.username + "_"+ d.realname
                return d;
              }):[],
            });
            this.isUpdate = true;
          } else {
            this.form.parentId = this.parentId || '';
            this.form.initLeaderValue=[];
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
