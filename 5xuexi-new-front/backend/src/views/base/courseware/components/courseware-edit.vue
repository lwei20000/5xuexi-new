<!-- 编辑弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="720px"
    :visible="visible"
     append-to-body
    :close-on-click-modal="true"
    custom-class="ele-dialog-form"
    :title="isUpdate ? '修改课件' : '添加课件'"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="92px">

          <el-form-item label="上级课件:">
            <ele-tree-select
              clearable
              v-model="form.parentId"
              :data="coursewareList"
              label-key="coursewareName"
              value-key="coursewareId"
              default-expand-all
              placeholder="请选择上级课件"
            />
          </el-form-item>
          <el-form-item label="课件名称:" prop="coursewareName">
            <el-input
              clearable
              v-model="form.coursewareName"
              placeholder="请输入课件名称"
            />
          </el-form-item>

          <el-form-item label="排序号:" prop="sortNumber">
            <el-input-number
              :min="0"
              v-model="form.sortNumber"
              placeholder="请输入排序号"
              controls-position="right"
              class="ele-fluid ele-text-left"
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
  import { addCourseware, updateCourseware } from '@/api/base/courseware';
  import { clearDeep } from '@/api/common';
  export default {
    components: {  },
    props: {
      courseId:null,
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object,
      // 上级课件id
      parentId: null,
      // 全部课件数据
      coursewareList: Array
    },
    data() {
      const defaultForm = {
        coursewareId: null,
        parentId: '',
        coursewareName: '',
        sortNumber: null
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          coursewareName: [
            {
              required: true,
              message: '请输入课件名称',
              trigger: 'blur'
            }
          ],
          sortNumber: [
            {
              required: true,
              message: '请输入排序号',
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
            ...this.form,
            courseId: this.courseId,
            parentId: this.form.parentId || 0
          };
          const saveOrUpdate = this.isUpdate ? updateCourseware : addCourseware;
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
            this.$util.assignObject(this.form, {
              ...this.data,
              parentId: this.data.parentId || ''
            });
            this.isUpdate = true;
          } else {
            this.form.parentId = this.parentId || '';
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
