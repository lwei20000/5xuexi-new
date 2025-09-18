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
  import { listOrgs } from '@/api/system/org';

  export default {
    props: {

      params:undefined,

      value: {type:[Array,String,Number]},
      //valueKey
      valueKey: {default: 'orgId'},
      //labelKey
      labelKey:{default: 'orgName'},
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
      placeholder: {default: "请选择机构"},
    },
    data() {
      return {
        // 数据
        data:  []
      };
    },
    created() {
      /* 获取机构数据 */
      this.init();
    },
    methods: {
      init(){
        /* 获取机构数据 */
        listOrgs(this.params)
          .then((list) => {
            this.data = this.$util.toTreeData({
              data: list,
              idField: 'orgId',
              parentIdField: 'parentId'
            });
          })
          .catch((e) => {
            this.$message.error(e.message);
          });
      },
      updateValue(value) {
        this.$emit('input', value);
      }
    }
  };
</script>
