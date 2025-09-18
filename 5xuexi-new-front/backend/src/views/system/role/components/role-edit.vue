<!-- 角色编辑弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="460px"
    :visible="visible"
    :close-on-click-modal="true"
    :title="isUpdate ? '修改角色' : '添加角色'"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="82px">
      <el-form-item label="上级角色:">
        <ele-tree-select
          clearable
          v-model="form.parentId"
          :data="roleList"
          label-key="roleName"
          value-key="roleId"
          default-expand-all
          placeholder="请选择上级角色"
        />
      </el-form-item>
      <el-form-item label="角色名称:" prop="roleName">
        <el-input
          clearable
          :maxlength="100"
          v-model="form.roleName"
          placeholder="请输入角色名称"
        />
      </el-form-item>
      <el-form-item label="角色代码:" prop="roleCode">
        <el-input
          clearable
          :maxlength="100"
          v-model="form.roleCode"
          placeholder="请输入角色代码"
        />
      </el-form-item>
      <el-form-item label="排序号:" prop="sortNumber">
        <el-input-number
          :min="0"
          :max="99999"
          v-model="form.sortNumber"
          controls-position="right"
          placeholder="请输入排序号"
          class="ele-fluid ele-text-left"
        />
      </el-form-item>
      <el-form-item label="数据权限:" prop="scopeType">
        <scope-type-select :params="{dictCode: 'scope_type'}" v-model="form.scopeType" @input="inputsearch" />
      </el-form-item>
      <el-form-item label="机构范围:" v-if="form.scopeType==7 || form.scopeType== 8 " prop="orgIds">
        <org-tree
          v-model="form.orgIds"
          :checkStrictly="true"
          :expandOnClickNode="false"
          placeholder="请选择机构"
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
  import { addRole, updateRole } from '@/api/system/role';
  import {clearDeep} from "@/api/common";
  import ScopeTypeSelect from '@/views/system/dictionary/components/dict-data-select.vue';
  import OrgTree from '../../org/components/org-tree.vue';
  export default {
    components: {ScopeTypeSelect , OrgTree},
      props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object,
      // 上级id
      parentId: null,
      // 角色数据
      roleList: Array,
    },
    data() {
      const defaultForm = {
        roleId: null,
        roleName: '',
        parentId:'',
        orgIds: [],
        sortNumber:0,
        scopeType:'',
        roleCode: '',
        comments: ''
      };
      return {
        dataList:[],
        orgDataList:[],
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          roleName: [
            {
              required: true,
              message: '请输入角色名称',
              trigger: 'blur'
            }
          ],
          roleCode: [
            {
              required: true,
              message: '请输入角色代码',
              trigger: 'blur'
            }
          ],
          sortNumber: [
            {
              required: true,
              message: '请输入排序号',
              trigger: 'blur'
            }
          ],
          scopeType: [
            {
              required: true,
              message: '请选择数据权限',
              trigger: 'blur'
            }
          ],
          orgIds: [
            {
              required: true,
              message: '请选择部门范围',
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
    methods: {
      inputsearch(scopeType){
        if(scopeType != 7 && scopeType != 8){
          this.form.orgIds=[];
        }
      },
      /* 保存编辑 */
      save() {
        this.$refs.form.validate((valid) => {
          if (!valid) {
            return false;
          }
          if(this.form.scopeType == 7 || this.form.scopeType==8){
            if(this.form.orgIds.length == 0){
              this.$message.error("需要指定机构范围");
              return false;
            }
          }

          this.loading = true;
          const saveOrUpdate = this.isUpdate ? updateRole : addRole;


          const data = {
            ...this.form,
            parentId: this.form.parentId || 0,
            orgs: this.form.orgIds.map((d) => {
              return { orgId: d };
            })
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
      }
    },
    watch: {
      visible(visible) {
        if (visible) {
          if (this.data) {
            this.$util.assignObject(this.form, {
              ...this.data,
              parentId: this.data.parentId || '',
              orgIds: this.data.orgs?this.data.orgs.map((d) => d.orgId):[]
            });
            this.isUpdate = true;
          } else {
            this.form.parentId = this.parentId || '';
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
