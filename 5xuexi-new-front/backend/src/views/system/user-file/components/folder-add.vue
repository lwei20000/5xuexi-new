<template>
  <ele-modal :lock-scroll="false"
    width="460px"
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    custom-class="ele-dialog-form"
    title="新建文件夹"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="102px">
      <el-form-item label="文件夹名称:" prop="name">
        <el-input
          clearable
          :maxlength="100"
          v-model="form.name"
          placeholder="请输入文件夹名称"
        />
      </el-form-item>
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
  import { addUserFile } from '@/api/system/user-file';

  export default {
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 父级 id
      parentId: Number
    },
    data() {
      return {
        // 表单数据
        form: {
          name: ''
        },
        // 表单验证规则
        rules: {
          name: [
            {
              required: true,
              message: '请输入文件夹名称',
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
          addUserFile({
            ...this.form,
            parentId: this.parentId,
            isDirectory: 1
          })
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
      /* 更新 visible */
      updateVisible(value) {
        this.$emit('update:visible', value);
      }
    },
    watch: {
      visible(visible) {
        if (!visible) {
          this.$refs.form.clearValidate();
          this.form.name = '';
        }
      }
    }
  };
</script>
