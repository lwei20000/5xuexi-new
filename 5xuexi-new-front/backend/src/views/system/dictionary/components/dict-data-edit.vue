<!-- 字典项编辑弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="460px"
    :visible="visible"
    :close-on-click-modal="false"
    :title="isUpdate ? '修改字典项' : '添加字典项'"
    @update:visible="updateVisible"
  >
    <el-form :model="form" ref="form" :rules="rules" label-width="96px">
      <el-form-item label="上级字典:" prop="parent">
        <dict-data-page-select v-model="form.parentId" :initValue="form.initParentValue" ></dict-data-page-select>
      </el-form-item>
      <el-form-item label="字典项名称:" prop="dictDataName">
        <el-input
          clearable
          :maxlength="200"
          v-model="form.dictDataName"
          placeholder="请输入字典项名称"
        />
      </el-form-item>
      <el-form-item label="字典项值:" prop="dictDataCode">
        <el-input
          clearable
          :maxlength="100"
          v-model="form.dictDataCode"
          placeholder="请输入字典项值"
        />
      </el-form-item>
      <el-form-item label="排序号:" prop="sortNumber">
        <el-input-number
          :min="0"
          :max="9999"
          v-model="form.sortNumber"
          placeholder="请输入排序号"
          controls-position="right"
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
      <el-button @click="updateVisible(false)">取消 </el-button>
      <el-button type="primary" :loading="loading" @click="save">
        保存
      </el-button>
    </template>
  </ele-modal>
</template>

<script>
import {addDictionaryData, updateDictionaryData} from '@/api/system/dictionary-data';
import DictDataPageSelect from './dict-data-page-select';
import {clearDeep} from "@/api/common";
export default {
  components: {DictDataPageSelect },
  props: {
    // 弹窗是否打开
    visible: Boolean,
    // 修改回显的数据
    data: Object,
    // 字典id
    dictId: Number
  },
  data() {
    const defaultForm = {
      parentId: undefined,
      dictDataId: null,
      dictDataName: '',
      dictDataCode: '',
      sortNumber: '',
      comments: '',
      initParentValue: undefined,
    };
    return {
      defaultForm,
      // 表单数据
      form: { ...defaultForm },
      // 表单验证规则
      rules: {
        dictDataName: [
          {
            required: true,
            message: '请输入字典项名称',
            trigger: 'blur'
          }
        ],
        dictDataCode: [
          {
            required: true,
            message: '请输入字典项值',
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
        const saveOrUpdate = this.isUpdate
          ? updateDictionaryData
          : addDictionaryData;
        const data = {
          ...this.form,
          dictId: this.dictId,
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
    },
  },
  watch: {
    visible(visible) {
      if (visible) {
        if (this.data) {
          var p = {};
          if(this.data.parentId){
            p.dictDataId = this.data.parentId;
            p.name = this.data.parentDictName + '_' + this.data.parentDictDataName;
          }
          this.$util.assignObject(this.form, {
            ...this.data,
            parentId: this.data.parentId || undefined,
            initParentValue : p,
          });
          this.isUpdate = true;
        } else {
          this.form.parentId = undefined;
          this.form.initParentValue = {};
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
