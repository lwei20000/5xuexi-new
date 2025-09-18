<!-- 专业课程编辑弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="560px"
    :visible="visible"
    append-to-body
    :close-on-click-modal="true"
    :title="isUpdate ? '修改专业课程' : '添加专业课程'"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="82px">
      <el-form-item label="专业:" prop="majorId" v-if="!major">
        <major-select ref="majorSelect" v-model="form.majorId" :initValue="form.initMajorValue" ></major-select>
      </el-form-item>
      <el-form-item label="学期:" prop="semester">
        <dict-data-select :params="{dictCode: 'semester'}" v-model="form.semester" />
      </el-form-item>
      <el-form-item label="课程:" prop="courseId">
        <course-select ref="courseSelect" v-model="form.courseId"  :initValue="form.initCourseValue" ></course-select>
      </el-form-item>
    </el-form>
    <template v-slot:footer>
<!--      <el-button @click="updateVisible(false)">取消</el-button>-->
      <el-button type="primary" @click="save" :loading="loading">
        保存
      </el-button>
    </template>
  </ele-modal>
</template>

<script>
  import { addMajorCourse, updateMajorCourse } from '@/api/base/majorCourse';
  import CourseSelect from '@/views/base/course/components/course-select.vue';
  import DictDataSelect from '@/views/system/dictionary/components/dict-data-select.vue';
  import MajorSelect from '@/views/base/major/components/major-select.vue';
  import {clearDeep} from "@/api/common";
  export default {
    components: { CourseSelect, MajorSelect, DictDataSelect },
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object,
      // 专业数据
      major: Object
    },
    data() {
      const defaultForm = {
        id: null,
        semester:'',
        courseId: undefined,
        initCourseValue:undefined,
        majorId: undefined,
        initMajorValue:undefined
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          semester: [
            {
              required: true,
              message: '请输入学期',
              trigger: 'blur'
            }
          ],
          majorId: [
            {
              required: true,
              message: '请选择专业',
              trigger: 'blur'
            }
          ],
          courseId: [
            {
              required: true,
              message: '请选择课程',
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
      /* 保存编辑 */
      save() {
        this.$refs.form.validate((valid) => {
          if (!valid) {
            return false;
          }
          this.loading = true;
          const saveOrUpdate = this.isUpdate ? updateMajorCourse : addMajorCourse;
          const data = {
            ...this.form
          };
          if(this.major){
            data.majorId = this.major.majorId
          }
          clearDeep(data);
          saveOrUpdate(data)
            .then((msg) => {
              this.loading = false;
              this.$message.success(msg);
              // 修改这里：不关闭弹窗，只触发done事件，并传递专业ID用于刷新
              if (this.form.majorId) {
                this.$emit('done', { majorId: this.form.majorId });
              } else {
                this.$emit('done');
              }
              // 如果是新增模式，只重置部分字段，保持已选择的值
              if (!this.isUpdate) {
                // 保留当前表单中的值，只重置id和其他必要字段
                this.form.id = null;
              }
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

      // 监听专业ID变化，清空课程选择
      'form.majorId'(newVal, oldVal) {
        // 当专业改变时，清空课程选择（新增模式下或者从一个有效专业切换到另一个有效专业）
        if ((newVal && !this.isUpdate) || (newVal && oldVal)) {
          this.form.courseId = undefined;
          this.form.initCourseValue = {};
        }
      },

      visible(visible) {
        if (visible) {
          if (this.data) {
            var initMajor = {};
            if(this.data.majorId && this.data.major){
              initMajor.majorId = this.data.majorId;
              initMajor.name = this.data.major.majorYear +"_" +this.data.major.majorCode+ '_' + this.data.major.majorName+ '_' + this.data.major.majorType+ '_' + this.data.major.majorGradation+ '_' + this.data.major.majorForms+ '_' + this.data.major.majorLength+"年制";
            }

            var initCourse = {};
            if(this.data.courseId && this.data.course){
              initCourse.courseId = this.data.courseId;
              initCourse.name = this.data.course.courseCode+"_"+this.data.course.courseName;
            }

            this.$util.assignObject(this.form, {
              ...this.data,
              majorId: this.data.majorId || undefined,
              initMajorValue : initMajor,
              courseId: this.data.courseId || undefined,
              initCourseValue : initCourse
            });
            this.isUpdate = true;
          } else {
            this.form.courseId = undefined;
            this.form.initCourseValue = {};
            this.form.majorId = undefined;
            this.form.initMajorValue = {};
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
