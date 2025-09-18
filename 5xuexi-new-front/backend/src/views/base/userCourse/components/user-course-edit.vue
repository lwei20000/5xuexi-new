<!-- 课程成绩编辑弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="560px"
    :visible="visible"
    append-to-body
    :close-on-click-modal="true"
    title="修改课程成绩"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="82px">
      <el-form-item label="学习成绩:" prop="learingScore">
        <el-input-number
          :min="0"
          :max="100"
          v-model="form.learingScore"
          placeholder="请输入学习成绩"
          controls-position="right"
          class="ele-fluid ele-text-left"
        />
      </el-form-item>
      <el-form-item label="考试成绩:" prop="examScore">
        <el-input-number
          :min="-1"
          :max="100"
          v-model="form.examScore"
          placeholder="请输入考试成绩"
          controls-position="right"
          class="ele-fluid ele-text-left"
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

  import {clearDeep} from "@/api/common";
  import {updateUserExamScore} from "@/api/base/userCourse";
  export default {
    components: {},
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object
    },
    data() {
      const defaultForm = {
        id: null,
        examScore:'',
        learingScore:''
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          examScore: [
            {
              required: true,
              message: '请输入考试成绩',
              trigger: 'blur'
            }
          ],
          learingScore: [
            {
              required: true,
              message: '请输入学习成绩',
              trigger: 'blur'
            }
          ]
        },
        // 提交状态
        loading: false,
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
            ...this.form
          };
          clearDeep(data);
          updateUserExamScore(data)
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
            this.$util.assignObject(this.form, {
              id:this.data.id,
              learingScore:this.data.learingScore,
              examScore:this.data.examScore == null? -1:this.data.examScore
            });
          }
        } else {
          this.$refs.form.clearValidate();
          this.form = { ...this.defaultForm };
        }
      }
    }
  };
</script>
