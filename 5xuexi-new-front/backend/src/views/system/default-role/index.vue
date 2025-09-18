<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <role-search @search="reload" />
      <!-- 数据表格 -->
      <ele-pro-table
        ref="table"
        row-key="roleId"
        :columns="columns"
        :datasource="datasource"
        :default-expand-all="true"
        :need-page="false"
        :parse-data="parseData"
        cache-key="systemDefaultRoleTable"
        @done="onDone"
      >
        <!-- 表头工具栏 -->
        <template v-slot:toolbar>
          <el-button v-permission="'sys:defaultRole:save:c8507652b3ce41dabd15cedebfafdc1b'"
            size="small"
            type="primary"
            icon="el-icon-plus"
            class="ele-btn-icon"
            @click="openEdit()"
          >
            添加
          </el-button>
          <el-button v-permission="'sys:defaultRole:update:7c548eca9215461dab0df93bcfbc730f'"
            size="small"
            type="primary"
            icon="el-icon-plus"
            class="ele-btn-icon"
            @click="sync()"
          >
            同步资源
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
          <el-link v-permission="'sys:defaultRole:save:c8507652b3ce41dabd15cedebfafdc1b'"
            type="primary"
            :underline="false"
            icon="el-icon-plus"
            @click="openEdit(null, row.roleId,row.tenantType)"
          >
            添加
          </el-link>
          <el-link v-permission="'sys:defaultRole:update:7c548eca9215461dab0df93bcfbc730f'"
            type="primary"
            :underline="false"
            icon="el-icon-edit"
            @click="openEdit(row)"
          >
            修改
          </el-link>
          <el-link
            type="primary"
            :underline="false"
            icon="el-icon-finished"
            @click="openAuth(row)"
          >
            分配权限
          </el-link>
          <el-popconfirm v-permission="'sys:defaultRole:remove:4c2709638d8e4e2ebd4aa6c19b06fda6'"
            class="ele-action"
            title="确定要删除吗？"
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
    <role-edit :data="current" :parent-id="parentId" :pTenantType="pTenantType" :role-list="roleList" :visible.sync="showEdit" @done="reload" />
    <!-- 权限分配弹窗 -->
    <role-auth :data="current" :visible.sync="showAuth" />
    <!-- 同步资源弹窗 -->
    <role-sync :visible.sync="showSync" />
  </div>
</template>

<script>
  import RoleSearch from './components/role-search.vue';
  import RoleEdit from './components/role-edit.vue';
  import RoleSync from './components/role-sync.vue';
  import RoleAuth from './components/role-auth.vue';
  import { listDefaultRoles, removeDefaultRole } from '@/api/system/default-role';

  export default {
    name: 'SystemDefaultRole',
    components: {
      RoleSearch,
      RoleEdit,
      RoleAuth,
      RoleSync
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
            prop: 'tenantType',
            label: '租户类型',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'roleName',
            label: '角色名称',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'roleCode',
            label: '角色标识',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
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
            width: 330,
            align: 'center',
            resizable: false,
            slot: 'action',
            showOverflowTooltip: true
          }
        ],
        // 表格选中数据
        selection: [],
        // 当前编辑数据
        current: null,
        // 是否显示编辑弹窗
        showEdit: false,
        // 是否显示导入弹窗
        showAuth: false,
        // 是否显示同步资源
        showSync:false,
        // 全部角色数据
        roleList: [],
        // 上级id
        parentId: null,

        pTenantType:''
      };
    },
    methods: {
      /* 表格渲染完成回调 */
      onDone({ data }) {
        this.roleList = data;
      },
      /* 数据转为树形结构 */
      parseData(data) {
        return this.$util.toTreeData({
          data: data,
          idField: 'roleId',
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
      /* 表格数据源 */
      datasource({where}) {
        return listDefaultRoles({ ...where});
      },
      /* 刷新表格 */
      reload(where) {
        this.$refs.table.reload({where: where });
      },
      /* 显示编辑 */
      openEdit(row,parentId,tenantType) {
        this.current = row;
        this.parentId = parentId;
        this.pTenantType = tenantType;
        this.showEdit = true;
      },
      sync(){
        this.showSync = true;
      },
      /* 显示分配权限 */
      openAuth(row) {
        this.current = row;
        this.showAuth = true;
      },
      /* 删除 */
      remove(row) {
        if (row.children?.length) {
          this.$message.error('请先删除子节点');
          return;
        }
        const loading = this.$loading({ lock: true });
        removeDefaultRole(row.roleId)
          .then((msg) => {
            loading.close();
            this.$message.success(msg);
            this.reload();
          })
          .catch((e) => {
            loading.close();
            this.$message.error(e.message);
          });
      }
    }
  };
</script>
