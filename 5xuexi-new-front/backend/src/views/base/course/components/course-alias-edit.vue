<!-- 修改课程别名 -->
<template>
  <ele-modal :lock-scroll="false"
    width="560px"
    :visible="visible"
    :close-on-click-modal="true"
    title="修改课程别名"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="120px">
      <el-form-item label="课程代码别名:" prop="courseCode">
        <template v-slot:label>
          <el-tooltip content="每个租户对课程起不同别名" placement="top">
            <i class="el-icon-question" style="margin-right: 5px; color: #409EFF; cursor: pointer;"></i>
          </el-tooltip>
          <span>课程代码别名:</span>
        </template>
        <el-input
          clearable
          :maxlength="100"
          v-model="form.courseCode"
          placeholder="请输入课程代码别名"
        />
      </el-form-item>
      <el-form-item label="课程名称别名:" prop="courseName">
        <template v-slot:label>
          <el-tooltip content="每个租户对课程起不同别名" placement="top">
            <i class="el-icon-question" style="margin-right: 5px; color: #409EFF; cursor: pointer;"></i>
          </el-tooltip>
          <span>课程代码别名:</span>
        </template>
        <el-input
          clearable
          :maxlength="100"
          v-model="form.courseName"
          placeholder="请输入课程名称别名"
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
  import { updateAlias } from '@/api/base/course';
  import { clearDeep } from '@/api/common';

  export default {
    components: { },
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object
    },
    data() {
      const defaultForm = {
        courseId: null,
        courseCode: '',
        courseName: ''
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {

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
          const data = {
            courseId:this.form.courseId,
            courseCodeAlias:this.form.courseCode,
            courseNameAlias:this.form.courseName
          };
          clearDeep(data);
          updateAlias(data)
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
            this.$util.assignObject(this.form, this.data);
          }
        } else {
          this.$refs.form.clearValidate();
          this.form = { ...this.defaultForm };
        }
      }
    }
  };
</script>
