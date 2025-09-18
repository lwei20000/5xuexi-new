<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <login-record-search @search="reload" />
      <!-- 数据表格 -->
      <ele-pro-table
        ref="table"
        :datasource="datasource"
        :columns="columns"
        cache-key="systemLoginRecordTable"
      >
        <!-- 表头工具栏 -->
        <template v-slot:toolbar>
          <el-button
            size="small"
            type="primary"
            icon="el-icon-download"
            class="ele-btn-icon"
            @click="doDownloadData"
          >
            导出
          </el-button>
        </template>
        <!-- 操作类型列 -->
        <template v-slot:loginType="{ row }">
          <el-tag
            size="mini"
            :disable-transitions="true"
            :type="['success', 'danger', 'info', 'warning'][row.loginType]"
          >
            {{
              ['登录成功', '登录失败', '退出登录', '刷新TOKEN'][row.loginType]
            }}
          </el-tag>
        </template>
      </ele-pro-table>
    </el-card>
  </div>
</template>

<script>
  import {download} from "@/api/common";
  import LoginRecordSearch from './components/login-record-search.vue';
  import {
    pageLoginRecords,
    doDownloadData
  } from '@/api/system/login-record';

  export default {
    name: 'SystemLoginRecord',
    components: { LoginRecordSearch },
    data() {
      return {
        // 表格列配置
        columns: [
          {
            columnKey: 'index',
            type: 'index',
            width: 45,
            align: 'center',
            showOverflowTooltip: true,
            fixed: 'left'
          },
          {
            prop: 'username',
            label: '账号',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'realname',
            label: '姓名',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'ip',
            label: 'IP地址',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'device',
            label: '设备型号',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'os',
            label: '操作系统',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'browser',
            label: '浏览器',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            columnKey: 'loginType',
            prop: 'loginType',
            label: '操作类型',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 120,
            slot: 'loginType',
            filterMultiple: false,
            filters: [
              {
                text: '登录成功',
                value: 0
              },
              {
                text: '登录失败',
                value: 1
              },
              {
                text: '退出登录',
                value: 2
              },
              {
                text: '刷新TOKEN',
                value: 3
              }
            ]
          },
          {
            prop: 'comments',
            label: '备注',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'createTime',
            label: '登录时间',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110,
            formatter: (_row, _column, cellValue) => {
              return this.$util.toDateString(cellValue);
            }
          }
        ]
      };
    },
    methods: {
      /* 表格数据源 */
      datasource({ page, limit, where, order, filterValue }) {
        return pageLoginRecords({
          ...where,
          ...order,
          ...filterValue,
          page,
          limit
        });
      },
      /* 刷新表格 */
      reload(where) {
        this.$refs.table.reload({ page: 1, where: where });
      },
      /* 导出数据 */
      /* 导出数据 */
      doDownloadData(){
        const loading = this.$loading({ lock: true });
        this.$refs.table.doRequest(({ where, order }) => {
          doDownloadData({ ...where, ...order }).then((data) => {
            loading.close();
            download(data,"登录日志.xlsx");
          }).catch((e) => {
            loading.close();
            this.$message.error(e.message);
          });
        });
      }
    }
  };
</script>
