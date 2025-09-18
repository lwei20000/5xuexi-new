<!-- 文档编辑弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="980px"
    :visible="visible"
    :close-on-click-modal="true"
    custom-class="ele-dialog-form"
    :title="isUpdate ? '修改文档' : '添加文档'"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="102px">
      <el-row :gutter="15">
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="上级文档:">
            <ele-tree-select
              :data="docList"
              v-model="form.parentId"
              clearable
              :multiple="false"
              valueKey="docId"
              labelKey="name"
              placeholder="请选择上级文档"
              :default-expand-all="true"
              :check-strictly="false"
              :expand-on-click-node="false"
            />
          </el-form-item>
          <el-form-item label="文档名称:" prop="name">
            <el-input
              clearable
              :maxlength="200"
              v-model="form.name"
              placeholder="请输入文档名称"
            />
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="排序号:" prop="sortNumber">
            <el-input-number
              :min="0"
              :max="99999"
              v-model="form.sortNumber"
              controls-position="right"
              placeholder="请输入排序号"
              class="ele-fluid ele-text-left"
            />
          </el-form-item>
        </el-col>

        <el-col v-bind="styleResponsive ? { sm: 24 } : { span: 24 }">
          <el-form-item label="文档内容:" prop="content">
            <!-- 编辑器 -->
            <tinymce-editor
              ref="editor"
              v-model="form.content"
              :init="{ height: 520 }"
            />
          </el-form-item>
        </el-col>

      </el-row>
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
import TinymceEditor from '@/components/TinymceEditor/index.vue';
  import {
    addDoc,
    updateDoc
  } from '@/api/system/doc';
  import {clearDeep} from "@/api/common";

  export default {
    components: { TinymceEditor },
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object,
      // 上级id
      parentId: Number,
      //文档类型
      docType:String,
      //默认角色id
      defaultRoleId:Number,
      // 文档数据
      docList: Array
    },
    data() {
      const defaultForm = {
        docId: null,
        parentId: null,
        name: '',
        docType: null,
        sortNumber: null,
        content: '',
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          name: [
            {
              required: true,
              message: '请输入文档名称',
              trigger: 'blur'
            }
          ],
          sortNumber: [
            {
              required: true,
              message: '请输入排序号',
              trigger: 'blur'
            }
          ],
          content: [
            {
              required: true,
              message: '请输入富文本文档',
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
            parentId: this.form.parentId || 0
          };
          const saveOrUpdate = this.isUpdate ? updateDoc : addDoc;
          clearDeep(data);
          data.docType = this.docType;
          data.defaultRoleId = this.defaultRoleId;
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
      }
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
