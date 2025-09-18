<!-- 字典选择下拉框 -->
<template>
  <el-select   class="ele-fluid"
    :value="value"
    :clearable="clearable"
    :multiple="multiple"
    :disabled="disabled"
    :placeholder="placeholder"
    @input="updateValue"
  >
    <el-option
      v-for="item in data"
      :key="item.dictDataId"
      :value="item.dictDataCode"
      :label="item.dictDataName"
    />
  </el-select>
</template>

<script>
  import { listDictionaryData } from '@/api/system/dictionary-data';

  export default {
    props: {
      // 选中的数据(v-modal)
      value: null,

      params:undefined,
      //支持多选
      multiple:{default: false},
      //取消按钮
      clearable:{default: true},
      // 禁用
      disabled: {default: false},
      // 提示信息
      placeholder: {
        type: String,
        default: '请选择字典'
      }
    },
    data() {
      return {
        // 字典数据
        data: []
      };
    },
    created() {
      /* 获取机构类型数据 */
      listDictionaryData({
        ...this.params,
      })
        .then((list) => {
          this.data = list;
        })
        .catch((e) => {
          this.$message.error(e.message);
        });
    },
    methods: {
      /* 更新选中数据 */
      updateValue(value) {
        this.$emit('input', value);
      }
    }
  };
</script>
