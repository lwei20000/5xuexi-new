<!-- 修改密码弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="420px"
    title="修改密码"
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="true"
    @update:visible="updateVisible"
    @closed="onClose"
  >
    <el-form
      ref="form"
      :model="form"
      :rules="rules"
      class="noBorderInput"
      label-width="86px"
      @keyup.enter.native="save"
    >
      <el-form-item label="旧密码:" prop="oldPassword">
        <el-input
          show-password
          v-model.trim="form.oldPassword"
          placeholder="请输入旧密码"
        />
      </el-form-item>
      <el-form-item label="新密码:" prop="password">
        <el-input
          show-password
          v-model.trim="form.password"
          placeholder="请输入新密码"
        />
      </el-form-item>
      <el-form-item label="确认密码:" prop="password2">
        <el-input
          show-password
          v-model.trim="form.password2"
          placeholder="请再次输入新密码"
        />
      </el-form-item>
    </el-form>
    <template v-slot:footer>
      <el-button @click="updateVisible(false)">取消</el-button>
      <el-button type="primary" @click="save">确定</el-button>
    </template>
  </ele-modal>
</template>

<script>
  import { updatePassword } from '@/api/layout';

  export default {
    props: {
      visible: Boolean
    },
    data() {
      return {
        // 按钮 loading
        loading: false,
        // 表单数据
        form: {
          oldPassword: '',
          password: '',
          password2: ''
        },
        // 表单验证
        rules: {
          oldPassword: [
            {
              required: true,
              message: '请输入旧密码',
              trigger: 'blur'
            }
          ],
          password: [
            {
              required: true,
              message: '请输入新密码',
              trigger: 'blur'
            }
          ],
          password2: [
            {
              required: true,
              trigger: 'blur',
              validator: (_rule, value, callback) => {
                if (!value) {
                  return callback(new Error('请再次输入新密码'));
                }
                if (value !== this.form.password) {
                  return callback(new Error('两次输入密码不一致'));
                }
                callback();
              }
            }
          ]
        }
      };
    },
    methods: {
      /* 修改 visible */
      updateVisible(value) {
        this.$emit('update:visible', value);
      },
      /* 保存修改 */
      save() {
        this.$refs.form.validate((valid) => {
          if (valid) {
            this.loading = true;
            updatePassword(this.form)
              .then((msg) => {
                this.loading = false;
                this.$message.success(msg);
                this.updateVisible(false);
              })
              .catch((e) => {
                this.loading = false;
                this.$message.error(e.message);
              });
          } else {
            return false;
          }
        });
      },
      /* 关闭回调 */
      onClose() {
        this.form = {
          oldPassword: '',
          password: '',
          password2: ''
        };
        this.$refs.form.resetFields();
        this.loading = false;
      }
    }
  };
</script>
