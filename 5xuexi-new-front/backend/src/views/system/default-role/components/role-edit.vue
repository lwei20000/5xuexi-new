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
      <el-form-item label="租户类型:" prop="tenantType">
        <tenant-type-select :params="{dictCode: 'tenant_type'}" :disabled="isUpdate || pTenantType !=null" v-model="form.tenantType" />
      </el-form-item>
      <el-form-item label="角色名称:" prop="roleName">
        <el-input
          clearable
          :maxlength="100"
          v-model="form.roleName"
          placeholder="请输入角色名称"
        />
      </el-form-item>
      <el-form-item label="角色标识:" prop="roleCode">
        <el-input
          clearable
          :maxlength="100"
          v-model="form.roleCode"
          placeholder="请输入角色标识"
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
        <scope-type-select :params="{dictCode: 'scope_type'}" v-model="form.scopeType" />
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
  import { addDefaultRole, updateDefaultRole } from '@/api/system/default-role';
  import {clearDeep} from "@/api/common";
  import ScopeTypeSelect from '@/views/system/dictionary/components/dict-data-select.vue';
  import TenantTypeSelect from '@/views/system/dictionary/components/dict-data-select.vue';
  export default {
    components: {ScopeTypeSelect,TenantTypeSelect },
      props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object,
      // 上级id
      parentId: null,
      pTenantType:null,
      // 角色数据
      roleList: Array,
    },
    data() {
      const defaultForm = {
        roleId: null,
        roleName: '',
        sortNumber:0,
        tenantType:'',
        parentId:'',
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
              message: '请输入角色标识',
              trigger: 'blur'
            }
          ],
          tenantType: [
            {
              required: true,
              message: '请选择租户类型',
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
          ]
        },
        // 提交状态
        loading: false,
        // 是否是修改
        isUpdate: false
      };
    },
    methods: {
      /* 保存编辑 */
      save() {
        this.$refs.form.validate((valid) => {
          if (!valid) {
            return false;
          }

          this.loading = true;
          const saveOrUpdate = this.isUpdate ? updateDefaultRole : addDefaultRole;


          const data = {
            ...this.form,
            parentId: this.form.parentId || 0
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
              parentId: this.data.parentId || ''
            });
            this.isUpdate = true;
          } else {
            this.form.parentId = this.parentId || '';
            this.isUpdate = false;
          }
          if(this.pTenantType){
            this.form.tenantType = this.pTenantType;
          }
        } else {
          this.$refs.form.clearValidate();
          this.form = { ...this.defaultForm };
        }
      }
    }
  };
</script>
