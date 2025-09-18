<template>
  <ele-modal :lock-scroll="false"
    width="980px"
    :visible="visible"
    append-to-body
    :close-on-click-modal="true"
    custom-class="ele-dialog-form"
    title="已读信息"
    @update:visible="updateVisible"
  >
    <el-card shadow="never" style=" padding-top: 0;">
      <!-- 搜索表单 -->
      <announcement-user-search @search="reload" />
      <!-- 数据表格 -->
      <ele-pro-table
        ref="table"
        row-key="id"
        :columns="columns"
        :datasource="datasource"
        cache-key="systemAnnouncementUserTable"
      >
      </ele-pro-table>
    </el-card>

  </ele-modal>
</template>
<script>


  import AnnouncementUserSearch from "@/views/system/announcement/components/announcement-user-search";
  import {pageAnnouncementUsers} from "@/api/system/announcement";
  export default {
    props: {
      // 弹窗是否打开
      visible: Boolean,
      announcementId: undefined,
      hasRead: undefined,
    },
    name: 'BaseCourseware',
    components: {AnnouncementUserSearch},
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
            label: '用户名',
            showOverflowTooltip: true,
            minWidth: 180
          },
          {
            prop: 'realname',
            label: '姓名',
            showOverflowTooltip: true,
            minWidth: 180
          },
          {
            prop: 'id',
            label: '是否已读',
            showOverflowTooltip: true,
            minWidth: 180,
            formatter: (_row, _column, cellValue) => {
              return cellValue?'是':'否';
            }
          },
        ],
      };
    },
    methods: {
      /* 表格数据源 */
      /* 表格数据源 */
      datasource({ page, limit, where, order }) {
        where.announcementId= this.announcementId;
        where.hasRead= this.hasRead;
        return pageAnnouncementUsers({ ...where, ...order, page, limit});
      },
      /* 刷新表格 */
      reload(where) {
        this.$refs.table.reload({ where: where });
      },
      /* 更新visible */
      updateVisible(value) {
        this.$emit('update:visible', value);
      },
    }
  };
</script>
