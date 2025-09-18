<!-- 批量添加弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="460px"
    :visible="visible"
    :close-on-click-modal="true"
    title="批量添加"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="82px">
      <el-form-item label="年份:" prop="year">
        <el-date-picker
          type="year"
          class="ele-fluid"
          v-model="form.year"
          value-format="yyyy"
          placeholder="请选择年份"
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
  import {generateRemark} from "@/api/report/remark";
  export default {
      props: {
      // 弹窗是否打开
      visible: Boolean
    },
    data() {
      const defaultForm = {
        year:''
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          year: [
            {
              required: true,
              message: '请选择年份',
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
          generateRemark(this.form)
            .then((msg) => {
              this.loading = false;
              this.$message.success(msg);
              this.updateVisible(false);
              this.$emit('done');
            }).catch((e) => {
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
