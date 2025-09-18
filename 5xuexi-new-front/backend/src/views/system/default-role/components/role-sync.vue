<!-- 同步资源弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="460px"
    :visible="visible"
    :close-on-click-modal="true"
    title="同步资源"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="82px">
      <el-form-item label="租户类型:" prop="tenantType">
        <tenant-type-select :params="{dictCode: 'tenant_type'}" v-model="form.tenantType" />
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
  import { syncDefaultRole } from '@/api/system/default-role';
  import TenantTypeSelect from '@/views/system/dictionary/components/dict-data-select.vue';
  export default {
    components: {TenantTypeSelect},
      props: {
      // 弹窗是否打开
      visible: Boolean
    },
    data() {
      const defaultForm = {
        tenantType:''
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          tenantType: [
            {
              required: true,
              message: '请选择租户类型',
              trigger: 'blur'
            }
          ]
        },
        // 提交状态
        loading: false
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
          syncDefaultRole(this.form.tenantType)
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
  };
</script>
