<template>
      <ele-table-select
        ref="select"
        v-model="value"
        :multiple="multiple"
        :clearable="clearable"
        :placeholder="placeholder"
        value-key="paperId"
        label-key="paperName"
        :table-config="tableConfig"
        :popper-width="580"
        :disabled="disabled"
        :init-value="initValue"
        @change="updateValue"
      >
        <!-- 表头工具栏 -->
        <template v-slot:toolbar>

        </template>
      </ele-table-select>
</template>

<script>
import { pagePapers } from "@/api/exam/paper";

  export default {
    props: {
      params:undefined,
      // 回显值
      initValue: undefined,
      //支持多选
      multiple:{default: false},
      //取消按钮
      clearable:{default: true},
      // 禁用
      disabled: {default: false},
      //提示语
      placeholder: {default: "请选择试卷"},
    },
    components: {  },
    data() {
      return {
        value: undefined,
        // 可搜索表格配置
        tableConfig: {
          datasource: ({ page, limit, where, order }) => {

            return pagePapers({ ...where,...this.params, ...order, page, limit });
          },
          columns: [
            {
              columnKey: 'selection',
              type: 'selection',
              width: 45,
              show:this.multiple,
              align: 'center',
              reserveSelection: true
            },
            {
              columnKey: 'index',
              type: 'index',
              width: 45,
              align: 'center',
              showOverflowTooltip: true
            },
            {
              prop: 'paperName',
              label: '试卷名称',
              showOverflowTooltip: true,
              minWidth: 110
            },
            {
              prop: 'createTime',
              label: '创建时间',
              showOverflowTooltip: true,
              minWidth: 110,
              formatter: (_row, _column, cellValue) => {
                return this.$util.toDateString(cellValue);
              }
            },
          ],
          pageSize: 5,
          pageSizes: [5, 10, 15, 20],
          rowClickChecked: true,
          rowClickCheckedIntelligent: false,
          toolkit: ['reload', 'columns'],
          size: 'small',
          toolStyle: { padding: '0 10px' }
        },
      };
    },
    methods: {
      updateValue(value) {
        this.$emit('input', value);
      },
    }
  };
</script>
