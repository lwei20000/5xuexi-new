<template>
      <ele-tree-select
        :data="data"
        :value="value"
        :clearable="clearable"
        :multiple="multiple"
        :valueKey="valueKey"
        :labelKey="labelKey"
        :placeholder="placeholder"
        :disabled="disabled"
        :default-expand-all="defaultExpandAll"
        :check-strictly="checkStrictly"
        :expand-on-click-node="expandOnClickNode"
        @change="updateValue"
      />
</template>

<script>
import { listGroups } from '@/api/system/group';

  export default {
    props: {
      params:undefined,

      value: {type:[Array,String,Number]},
      //valueKey
      valueKey: {default: 'groupId'},
      //labelKey
      labelKey:{default: 'groupName'},
      //支持多选
      multiple:{default: true},
      //取消按钮
      clearable:{default: true},
      // 禁用
      disabled: {default: false},
      // 关闭父子联动
      checkStrictly:{default: false},
      //全部展开
      defaultExpandAll:{default: true},
      //点击是展开下一级
      expandOnClickNode:{default: true},
      //提示语
      placeholder: {default: "请选择分组"},
    },
    data() {
      return {
        // 数据
        data:  []
      };
    },
    created() {
      /* 获取分组数据 */
      listGroups(this.params)
        .then((list) => {
          this.data = this.$util.toTreeData({
            data: list,
            idField: 'groupId',
            parentIdField: 'parentId'
          });
        })
        .catch((e) => {
          this.$message.error(e.message);
        });
    },
    methods: {
      updateValue(value) {
        this.$emit('input', value);
      }
    }
  };
</script>
