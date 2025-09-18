<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <user-search @search="reload" />
      <!-- 数据表格 -->
      <ele-pro-table
        ref="table"
        :columns="columns"
        :datasource="datasource"
        :selection.sync="selection"
        cache-key="systemUserTable"
      >
        <!-- 表头工具栏 -->
        <template v-slot:toolbar>
          <el-button v-permission="'sys:user:save:2de4916849144fbf8b4e3a1cfbc60b80'"
            size="small"
            type="primary"
            icon="el-icon-plus"
            class="ele-btn-icon"
            @click="openEdit()"
          >
            新建
          </el-button>
          <el-button v-permission="'sys:user:remove:2dbb3c3c8ed24afd85caae0d320c6d31'"
            size="small"
            type="danger"
            icon="el-icon-delete"
            class="ele-btn-icon"
            @click="removeBatch"
          >
            删除
          </el-button>
          <el-button v-permission="'sys:user:save:2de4916849144fbf8b4e3a1cfbc60b80'"
            size="small"
            icon="el-icon-upload2"
            class="ele-btn-icon"
            @click="openImport"
          >
            导入
          </el-button>
        </template>
        <!-- 昵称列 -->
        <template v-slot:realname="{ row }">
          <router-link :to="'/system/user/details?id=' + row.userId">
            {{ row.realname }}
          </router-link>
        </template>
        <!-- 角色列 -->
        <template v-slot:roles="{ row }">
          <el-tag
            v-for="item in row.roles"
            :key="item.roleId"
            size="mini"
            type="primary"
            :disable-transitions="true"
          >
            {{ item.roleName }}
          </el-tag>
        </template>
        <!-- 机构列 -->
        <template v-slot:orgs="{ row }">
          <el-tag
            v-for="item in row.orgs"
            :key="item.orgId"
            size="mini"
            type="primary"
            :disable-transitions="true"
          >
            {{ item.orgFullName }}
          </el-tag>
        </template>
        <!-- 分组列 -->
        <template v-slot:groups="{ row }">
          <el-tag
            v-for="item in row.groups"
            :key="item.groupId"
            size="mini"
            type="primary"
            :disable-transitions="true"
          >
            {{ item.groupName }}
          </el-tag>
        </template>
        <!-- 状态列 -->
        <template v-slot:status="{ row }">
          <el-switch :disabled="!$hasPermission('sys:user:update:f13dbabbd410476193f385a39234da83') || row.systemDefault !== 0"
            :active-value="0"
            :inactive-value="1"
            v-model="row.status"
            @change="editStatus(row)"
          />
        </template>
        <!-- 操作列 -->
        <template v-slot:action="{ row }">
          <el-link v-if="$hasPermission('sys:user:update:f13dbabbd410476193f385a39234da83') && row.systemDefault === 0 "
            type="primary"
            :underline="false"
            icon="el-icon-edit"
            @click="openEdit(row)"
          >
            修改
          </el-link>
          <el-link v-if="$hasPermission('sys:user:update:f13dbabbd410476193f385a39234da83') && row.systemDefault === 0 "
            type="primary"
            :underline="false"
            icon="el-icon-key"
            @click="resetPsw(row)"
          >
            重置密码
          </el-link>
          <el-popconfirm v-if="$hasPermission('sys:user:remove:2dbb3c3c8ed24afd85caae0d320c6d31') && row.systemDefault === 0"
            class="ele-action"
            title="确定要删除此用户吗？"
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
    <user-edit :visible.sync="showEdit" :data="current" :org-id="orgId" :role-id="roleId" :group-id="groupId" @done="reload" />
    <!-- 导入弹窗 -->
    <user-import :visible.sync="showImport" @done="reload" />
  </div>
</template>

<script>
  import UserSearch from './components/user-search.vue';
  import UserEdit from './components/user-edit.vue';
  import UserImport from './components/user-import.vue';
  import {
    pageUsers,
    removeUser,
    removeUsers,
    updateUserStatus,
    updateUserPassword
  } from '@/api/system/user';

  export default {
    name: 'SystemUser',
    components: {
      UserSearch,
      UserEdit,
      UserImport
    },
    props: {
      // 机构id
      orgId: null,
      // 角色id
      roleId: null,
      // 分组id
      groupId: null
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
            fixed: 'left',
            selectable: (row) => {
              return row.systemDefault == 0;
            }
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
            minWidth: 110,
            slot: 'realname'
          },
          {
            prop: 'phone',
            label: '手机号',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'idCard',
            label: '身份证号',
            sortable: 'custom',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            columnKey: 'roles',
            label: '角色',
            showOverflowTooltip: true,
            minWidth: 110,
            slot: 'roles'
          },
          {
            columnKey: 'orgs',
            label: '机构',
            showOverflowTooltip: true,
            minWidth: 110,
            slot: 'orgs'
          },
          {
            columnKey: 'groups',
            label: '分组',
            showOverflowTooltip: true,
            minWidth: 110,
            slot: 'groups'
          },
          // {
          //   prop: 'createTime',
          //   label: '创建时间',
          //   sortable: 'custom',
          //   showOverflowTooltip: true,
          //   minWidth: 110,
          //   formatter: (_row, _column, cellValue) => {
          //     return this.$util.toDateString(cellValue);
          //   }
          // },
          {
            prop: 'status',
            label: '状态',
            align: 'center',
            sortable: 'custom',
            width: 80,
            resizable: false,
            slot: 'status',
            showOverflowTooltip: true
          },
          {
            columnKey: 'action',
            label: '操作',
            width: 220,
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
        showImport: false
      };
    },
    methods: {
      /* 表格数据源 */
      datasource({ page, limit, where, order }) {
        return pageUsers({ ...where, ...order, page, limit,
          orgId: this.orgId,
          roleId: this.roleId,
          groupId: this.groupId
        });
      },
      /* 刷新表格 */
      reload(where) {
        this.$refs.table.reload({ page: 1, where: where });
      },
      /* 打开编辑弹窗 */
      openEdit(row) {
        if(row){
          this.current = JSON.parse(JSON.stringify(row));
          if(this.current.userType){
            this.current.userType = JSON.parse(this.current.userType);
          }
        }else{
          this.current = row;
        }

        this.showEdit = true;
      },
      /* 打开导入弹窗 */
      openImport() {
        this.showImport = true;
      },
      /* 删除 */
      remove(row) {
        const loading = this.$loading({ lock: true });
        removeUser(row.userId)
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
      /* 批量删除 */
      removeBatch() {
        if (!this.selection.length) {
          this.$message.error('请至少选择一条数据');
          return;
        }
        this.$confirm('确定要删除选中的用户吗?', '提示', { type: 'warning',lockScroll:false })
          .then(() => {
            const loading = this.$loading({ lock: true });
            removeUsers(this.selection.map((d) => d.userId))
              .then((msg) => {
                loading.close();
                this.$message.success(msg);
                this.reload();
              })
              .catch((e) => {
                loading.close();
                this.$message.error(e.message);
              });
          })
          .catch(() => {});
      },
      /* 重置用户密码 */
      resetPsw(row) {
        this.$confirm('确定要重置此用户的密码为"123456"吗?', '提示', { type: 'warning',lockScroll:false })
          .then(() => {
            const loading = this.$loading({ lock: true });
            updateUserPassword(row.userId)
              .then((msg) => {
                loading.close();
                this.$message.success(msg);
              })
              .catch((e) => {
                loading.close();
                this.$message.error(e.message);
              });
          })
          .catch(() => {});
      },
      /* 更改状态 */
      editStatus(row) {
        const loading = this.$loading({ lock: true });
        updateUserStatus(row.userId, row.status)
          .then((msg) => {
            loading.close();
            this.$message.success(msg);
          })
          .catch((e) => {
            loading.close();
            row.status = !row.status ? 1 : 0;
            this.$message.error(e.message);
          });
      }
    },
    watch: {
      // 监听机构id变化
      orgId() {
        this.reload();
      },
      // 监听角色id变化
      roleId() {
        this.reload();
      },
      // 监听分组id变化
      groupId() {
        this.reload();
      }
    }
  };
</script>
