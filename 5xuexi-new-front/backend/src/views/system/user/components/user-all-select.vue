<template>
      <ele-table-select
        ref="select"
        v-model="value"
        :multiple="multiple"
        :clearable="clearable"
        :placeholder="placeholder"
        value-key="userId"
        label-key="name"
        :table-config="tableConfig"
        :popper-width="580"
        :disabled="disabled"
        :init-value="initValue"
        @change="updateValue"
      >
        <!-- 表头工具栏 -->
        <template v-slot:toolbar>
          <user-all-select-search @search="search" />
        </template>
      </ele-table-select>
</template>

<script>
import {pageAllUsers} from '@/api/system/user';
import UserAllSelectSearch from './user-all-select-search';

export default {
    props: {
      params:undefined,
      // 回显值
      initValue: undefined,
      //支持多选
      multiple:{default: true},
      //取消按钮
      clearable:{default: true},
      // 禁用
      disabled: {default: false},
      //提示语
      placeholder: {default: "请选择用户"},
    },
    components: { UserAllSelectSearch },
    data() {
      return {
        value: undefined,
        // 可搜索表格配置
        tableConfig: {
          datasource: ({ page, limit, where, order }) => {
             return pageAllUsers({ ...where,...this.params, ...order, page, limit });
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
              prop: 'name',
              label: '用户',
              sortable: 'custom',
              showOverflowTooltip: true,
              minWidth: 110
            },
            {
              columnKey: 'idCard',
              label: '身份证号',
              showOverflowTooltip: true,
              minWidth: 110
            },
            {
              columnKey: 'phone',
              label: '手机号',
              showOverflowTooltip: true,
              minWidth: 110
            },
            {
              columnKey: 'email',
              label: '邮箱',
              showOverflowTooltip: true,
              minWidth: 110
            }
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
      // 搜索
      search(where) {
        this.$refs.select.reload({
          where: where,
          page: 1
        });
      }
    }
  };
</script>
