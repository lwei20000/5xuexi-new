<!-- 分组编辑弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="460px"
    :visible="visible"
    :close-on-click-modal="true"
    :title="isUpdate ? '修改分组' : '添加分组'"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="82px">
      <el-form-item label="上级分组:">
        <ele-tree-select
          clearable
          v-model="form.parentId"
          :data="groupList"
          label-key="groupName"
          value-key="groupId"
          default-expand-all
          placeholder="请选择上级分组"
        />
      </el-form-item>
      <el-form-item label="分组名称:" prop="groupName">
        <el-input
          clearable
          :maxlength="100"
          v-model="form.groupName"
          placeholder="请输入分组名称"
        />
      </el-form-item>
      <el-form-item label="分组代码:" prop="groupCode">
        <el-input
          clearable
          :maxlength="100"
          v-model="form.groupCode"
          placeholder="请输入分组代码"
        />
      </el-form-item>
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
  import { addGroup, updateGroup } from '@/api/system/group';
  import {clearDeep} from "@/api/common";
  export default {
    components: {},
      props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object,
      // 上级id
      parentId: null,
      // 分组数据
      groupList: Array,
    },
    data() {
      const defaultForm = {
        groupId: null,
        parentId: null,
        groupName:'',
        groupCode: '',
        sortNumber:0,
        comments: ''
      };
      return {
        dataList:[],
        orgDataList:[],
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          groupName: [
            {
              required: true,
              message: '请输入分组名称',
              trigger: 'blur'
            }
          ],
          groupCode: [
            {
              required: true,
              message: '请输入分组代码',
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
    methods: {
      /* 保存编辑 */
      save() {
        this.$refs.form.validate((valid) => {
          if (!valid) {
            return false;
          }

          this.loading = true;
          const saveOrUpdate = this.isUpdate ? updateGroup : addGroup;

          const data = {
            ...this.form,
            parentId: this.form.parentId || 0
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
