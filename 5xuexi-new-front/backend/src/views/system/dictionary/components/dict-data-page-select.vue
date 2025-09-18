<template>
      <ele-table-select
        ref="select"
        v-model="value"
        :clearable="clearable"
        :placeholder="placeholder"
        value-key="dictDataId"
        label-key="name"
        :table-config="tableConfig"
        :popper-width="580"
        :init-value="initValue"
        @change="onSelect"
      >
        <!-- 表头工具栏 -->
        <template v-slot:toolbar>
          <dict-data-page-select-search @search="search" />
        </template>
      </ele-table-select>
</template>

<script>
import {pageDictionaryData} from '@/api/system/dictionary-data';
  import DictDataPageSelectSearch from "@/views/system/dictionary/components/dict-data-page-select-search";

  export default {
    props: {
      params:undefined,
      // 回显值
      initValue: undefined,
      //支持多选
      multiple:{default: false},
      //取消按钮
      clearable:{default: true},
      //提示语
      placeholder: {default: "请选择上级字典"},
    },
    components: { DictDataPageSelectSearch },
    data() {
      return {
        value:undefined,
        // 可搜索表格配置
        tableConfig: {
          datasource({ page, limit, where, order }) {
            return pageDictionaryData({ ...where,...this.params, ...order, page, limit });
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
              prop: 'dictName',
              label: '字典名称',
              showOverflowTooltip: true
            },
            {
              prop: 'dictCode',
              label: '字典值',
              width: 140,
              showOverflowTooltip: true
            },
            {
              prop: 'dictDataName',
              label: '字典项名称',
              sortable: 'custom',
              showOverflowTooltip: true,
              minWidth: 120
            },
            {
              prop: 'dictDataCode',
              label: '字典项值',
              sortable: 'custom',
              showOverflowTooltip: true,
              minWidth: 110
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
      onSelect(value) {
        this.$emit('input', value);
      },
      // 搜索
      search(where) {
        this.$refs.select.reload({
          where: where,
          page: 1
        });
      }
    },
  };
</script>
