<!-- 章节课时COPY弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="760px"
    :visible="visible"
    append-to-body
    :close-on-click-modal="true"
    title="复制章节课时"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="82px">
      <el-row :gutter="15">
        <el-col v-bind="styleResponsive ? { sm: 24 } : { span: 24 }">
          <el-form-item label="来源课程:" prop="pcourseId">
            <course-select ref="courseSelect" :params="{bySelf:true}" v-model="form.pcourseId"  :initValue="form.initCourseValue" ></course-select>
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
  import {copyCourseware } from '@/api/base/courseware';
  import {clearDeep} from "@/api/common";
  import CourseSelect from "@/views/base/course/components/course-select";

  export default {
    components: {CourseSelect},
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object
    },
    data() {
      const defaultForm = {
        pcourseId: undefined,
        initCourseValue:undefined,
        courseId:undefined
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          pcourseId: [
            {
              required: true,
              message: '请选择来源课程',
              trigger: 'blur'
            }
          ]
        },
        // 提交状态
        loading: false
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
            ...this.form
          };
          data.courseId = this.data.courseId;
          clearDeep(data);
          copyCourseware(data)
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
      },
    },
    watch: {
      visible(visible) {
        if (visible) {
          this.form.pcourseId=undefined;
          this.form.initCourseValue=undefined;
        } else {
          this.$refs.form.clearValidate();
          this.form = { ...this.defaultForm };
        }
      }
    }
  };
</script>
