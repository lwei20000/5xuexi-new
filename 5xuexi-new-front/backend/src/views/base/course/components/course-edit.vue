<!-- 课程编辑弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="560px"
    :visible="visible"
    :close-on-click-modal="true"
    :title="isUpdate ? '修改课程' : '添加课程'"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="120px">
      <el-form-item label="课程代码:" prop="courseCode">
        <el-input
          clearable
          :maxlength="100"
          v-model="form.courseCode"
          placeholder="请输入课程代码"
        />
      </el-form-item>
      <el-form-item label="课程名称:" prop="courseName">
        <el-input
          clearable
          :maxlength="100"
          v-model="form.courseName"
          placeholder="请输入课程名称"
        />
      </el-form-item>
      <el-form-item label="课程图片:" prop="picture">
        <com-image-upload v-model="form.picture" :limit="1"/>
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
  import { addCourse, updateCourse } from '@/api/base/course';
  import { clearDeep } from '@/api/common';
  import ComImageUpload from '@/views/system/file/components/com-image-upload.vue';

  export default {
    components: {ComImageUpload },
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
        coursePicture: '',
        picture:[],
        courseName: '',
        comments: ''
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          courseCode: [
            {
              required: true,
              message: '请输入课程代码',
              trigger: 'blur'
            }
          ],
          courseName: [
            {
              required: true,
              message: '请输入课程名称',
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

          if(this.form.picture.length>0 && this.form.picture[0].url){
            this.form.coursePicture=this.form.picture[0].url;
          }else{
            this.form.coursePicture='';
          }
          this.loading = true;
          const saveOrUpdate = this.isUpdate ? updateCourse : addCourse;
          const data = {
            ...this.form
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
      },
    },
    watch: {
      visible(visible) {
        if (visible) {
          if (this.data) {
            this.$util.assignObject(this.form, this.data);
            if(this.data.coursePicture){
              var f={uid:1,status:'done',progress:100};
              f.url=this.data.coursePicture;
              this.form.picture=[f];
            }else{
              this.form.picture=[];
            }
            this.isUpdate = true;
          } else {
            this.form.picture=[];
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
