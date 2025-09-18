<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <announcement-search @search="reload" />
      <!-- 数据表格 -->
      <ele-pro-table
        ref="table"
        :columns="columns"
        row-key="id"
        :datasource="datasource"
        :need-page="true"
        :selection.sync="selection"
        cache-key="systemAnnouncementTable">

        <!-- 操作列 -->
        <template v-slot:action="{ row }">
          <el-link
                   type="primary"
                   :underline="false"
                   icon="el-icon-edit"
                   @click="open(row)"
          >
            查看
          </el-link>
        </template>

      </ele-pro-table>
    </el-card>
  </div>
</template>

<script>
  import AnnouncementSearch from './components/announcement-search.vue';
  import {_pageOpenAnnouncements} from '@/api/system/open-announcement';

  export default {
    name: 'SystemOpenAnnouncementList',
    components: {
      AnnouncementSearch
    },
    data() {
      return {
        // 表格列配置
        columns: [
          {
            columnKey: 'selection',
            type: 'selection',
            width: 45,
            align: 'center',
            fixed: 'left'
          },
          {
            columnKey: 'index',
            type: 'index',
            width: 45,
            align: 'center',
            showOverflowTooltip: true,
            fixed: 'left'
          },
          {
            prop: 'title',
            label: '标题',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 160
          },
          {
            prop: 'openAnnouncementType',
            label: '通知类型',
            sortable: 'custom',
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
          {
            columnKey: 'action',
            label: '操作',
            width: 180,
            align: 'center',
            resizable: false,
            slot: 'action',
            showOverflowTooltip: true
          }
        ],
        // 表格选中数据
        selection: []
      };
    },
    methods: {
      /* 表格数据源 */
      datasource({ page, limit, where, order }) {
        return _pageOpenAnnouncements({ ...where, ...order, page, limit});
      },
      /* 刷新表格 */
      reload(where) {
        this.$refs.table.reload({ page: 1, where: where });
      },
      open(row){
        this.$router.push({path:'/static/openAnnouncementDetail',query:{id:row.openAnnouncementId,tenantId:row.tenantId}}).catch(err=>{console.log(err)});
      },
    }
  };
</script>
