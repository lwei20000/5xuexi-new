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
import { listRoles } from '@/api/system/role';

  export default {
    props: {
      params:undefined,

      value: {type:[Array,String,Number]},
      //valueKey
      valueKey: {default: 'roleId'},
      //labelKey
      labelKey:{default: 'roleName'},
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
      placeholder: {default: "请选择角色"},
    },
    data() {
      return {
        // 数据
        data:  []
      };
    },
    created() {
      /* 获取角色数据 */
      listRoles(this.params)
        .then((list) => {
          this.data = this.$util.toTreeData({
            data: list,
            idField: 'roleId',
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
