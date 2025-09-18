<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <tenant-search @search="reload" />
      <!-- 数据表格 -->
      <ele-pro-table
        ref="table"
        :columns="columns"
        row-key="tenantId"
        :datasource="datasource"
        :default-expand-all="true"
        :need-page="false"
        cache-key="systemTenantTable"
        :parse-data="parseData"
        @done="onDone"
      >
        <!-- 表头工具栏 -->
        <template v-slot:toolbar>
          <el-button v-permission="'sys:tenant:save:63feb064eea24feda2e1bdc36bc0e0c9'"
            size="small"
            type="primary"
            icon="el-icon-plus"
            class="ele-btn-icon"
            @click="openEdit()"
          >
            添加
          </el-button>
          <el-button v-permission="'sys:tenant:save:63feb064eea24feda2e1bdc36bc0e0c9'"
                     size="small"
                     icon="el-icon-upload2"
                     class="ele-btn-icon"
                     @click="openImport"
          >
            导入
          </el-button>
          <el-button class="ele-btn-icon" size="small" @click="expandAll">
            展开全部
          </el-button>
          <el-button class="ele-btn-icon" size="small" @click="foldAll">
            折叠全部
          </el-button>
        </template>
        <!-- 操作列 -->
        <template v-slot:action="{ row }">
          <el-link v-permission="'sys:tenant:save:63feb064eea24feda2e1bdc36bc0e0c9'"
            type="primary"
            :underline="false"
            icon="el-icon-plus"
            @click="openEdit(null, row.tenantId)"
          >
            添加
          </el-link>
          <el-link v-permission="'sys:tenant:update:07d0e575721f4dc69d64972e57932c3b'"
            type="primary"
            :underline="false"
            icon="el-icon-edit"
            @click="openEdit(row)"
          >
            修改
          </el-link>
          <el-popconfirm v-permission="'sys:tenant:remove:becb660c31664810a0ed85e4e662fcda'"
            class="ele-action"
            title="确定要删除此租户吗？"
            @confirm="remove(row)"
          >
            <template v-slot:reference>
              <el-link type="danger" :underline="false" icon="el-icon-delete">
                删除
              </el-link>
            </template>
          </el-popconfirm>
        </template>
      </ele-pro-table>
    </el-card>
    <!-- 编辑弹窗 -->
    <tenant-edit :data="current"  :parent-id="parentId" :tenant-list="tenantList"  :visible.sync="showEdit" @done="reload" />
    <!-- 导入弹窗 -->
    <tenant-import :visible.sync="showImport" @done="reload" />
  </div>
</template>

<script>
  import TenantSearch from './components/tenant-search.vue';
  import TenantEdit from './components/tenant-edit.vue';
  import {
    listTenants,
    removeTenant
  } from '@/api/system/tenant';
  import TenantImport from "@/views/system/tenant/components/tenant-import";

  export default {
    name: 'SystemTenant',
    components: {
      TenantImport,
      TenantSearch,
      TenantEdit
    },
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
            prop: 'tenantName',
            label: '租户名称',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 160
          },
          {
            prop: 'tenantType',
            label: '租户类型',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'tenantFullName',
            label: '租户全称',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 160
          },
          {
            prop: 'createTime',
            label: '创建时间',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110,
            formatter: (_row, _column, cellValue) => {
              return this.$util.toDateString(cellValue);
            }
          },
          {
            columnKey: 'action',
            label: '操作',
            width: 230,
            align: 'center',
            resizable: false,
            slot: 'action',
            showOverflowTooltip: true
          }
        ],
        // 当前编辑数据
        current: null,
        // 是否显示编辑弹窗
        showEdit: false,

        tenantList:[],
        // 上级id
        parentId: null,
        // 是否显示导入弹窗
        showImport: false
      };
    },
    methods: {
      /* 表格渲染完成回调 */
      onDone({ data }) {
        this.tenantList = data;
      },
      /* 数据转为树形结构 */
      parseData(data) {
        return this.$util.toTreeData({
          data: data,
          idField: 'tenantId',
          parentIdField: 'parentId'
        });
      },
      /* 展开全部 */
      expandAll() {
        this.$refs.table.toggleRowExpansionAll(true);
      },
      /* 折叠全部 */
      foldAll() {
        this.$refs.table.toggleRowExpansionAll(false);
      },
      /* 打开导入弹窗 */
      openImport() {
        this.showImport = true;
      },
      /* 表格数据源 */
      datasource({where }) {
        return listTenants({ ...where});
      },
      /* 刷新表格 */
      reload(where) {
        this.$refs.table.reload({where: where });
      },
      /* 显示编辑 */
      openEdit(row,parentId) {
        this.current = row;
        this.parentId = parentId;
        this.showEdit = true;
      },
      /* 删除 */
      remove(row) {
        const loading = this.$loading({ lock: true });
        removeTenant(row.tenantId)
          .then((msg) => {
            loading.close();
            this.$message.success(msg);
            this.reload();
          })
          .catch((e) => {
            loading.close();
            this.$message.error(e.message);
          });
      },
    }
  };
</script>
