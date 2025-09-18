<!-- 新增编辑考试发布 -->
<template>
  <ele-modal :lock-scroll="false"
    width="560px"
    :visible="visible"
    append-to-body
    :close-on-click-modal="true"
    :title="isUpdate ? '新增考试发布' : '编辑考试发布'"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="82px">
      <el-form-item label="试卷:" prop="paperId">
        <paperPageSelect ref="paperSelect" v-model="form.paperId" :params="{'courseId':courseId}"  :initValue="form.initPaperValue" ></paperPageSelect>
      </el-form-item>
      <el-form-item label="考试时间:" prop="dateRange">
        <el-date-picker
          v-model="form.dateRange"
          type="datetimerange"
          unlink-panels
          range-separator="-"
          start-placeholder="考试开始日期"
          end-placeholder="考试结束日期"
          value-format="yyyy-MM-dd HH:mm:ss"
          class="ele-fluid"
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
  import {addMajorCourseExam,updateMajorCourseExam} from "@/api/exam/publish";
  import paperPageSelect from '@/views/exam/paper/components/paper-page-select.vue';
  import {clearDeep} from "@/api/common";
  export default {
    components: { paperPageSelect },
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object,

      courseId: Number,

      // 专业课程id
      majorCourseId: Number
    },
    data() {
      const defaultForm = {
        majorCourseExamId: null,
        dateRange:'',
        paperId:undefined,
        initPaperValue:{}
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          paperId: [
            {
              required: true,
              message: '请选择试卷',
              trigger: 'blur'
            }
          ],
          dateRange: [
            {
              required: true,
              message: '请选择考试时间',
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
          const saveOrUpdate = this.isUpdate ? updateMajorCourseExam : addMajorCourseExam;
          const data = {
            ...this.form
          };

          data.startTime =  this.form.dateRange[0];
          data.endTime =  this.form.dateRange[1];

          data.majorCourseId = this.majorCourseId;
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
      },
    },
    watch: {
      visible(visible) {
        if (visible) {
          if (this.data) {
            var initPaper = {};
            if(this.data.paperId && this.data.paper){
              initPaper.paperId = this.data.paper.paperId;
              initPaper.paperName = this.data.paper.paperName;
            }
            var dateRange = [];
            dateRange[0] = this.data.startTime;
            dateRange[1] = this.data.endTime;

            this.$util.assignObject(this.form, {
              ...this.data,
              initPaperValue : initPaper,
              dateRange: dateRange
            });
            this.isUpdate = true;
          } else {
            this.form.paperId = undefined;
            this.form.initPaperValue = {};
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
