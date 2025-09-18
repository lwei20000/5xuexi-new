<template>
  <div class="ele-body">
    <el-card shadow="never" v-loading="loading">
      <ele-split-layout  style="min-height: 80vh"
                         width="316px"
                         allow-collapse
                         :leftStyle="{'border-right':'1px solid #ededed'}"
                         :right-style="{ overflow: 'hidden' }"
                         :collapseBtnStyle="{width:'20px'}"
      >
        <div>
          <el-input style="width: 95%;"
                    placeholder="输入关键字进行过滤"
                    v-model="filterText">
          </el-input>
          <!-- 操作按钮 -->
          <ele-toolbar class="ele-toolbar-actions">
            <div style="margin: 5px 0">
              <el-button v-permission="'sys:role:save:43733cfe2cf94e3db1c953e1275e6bfa'"
                         size="mini"
                         type="primary"
                         icon="el-icon-plus"
                         class="ele-btn-icon"
                         @click="openEdit()"
              >
                添加
              </el-button>
              <el-button v-permission="'sys:role:update:ea93c40c0e32419c9e3b5201f357b988'"
                         size="mini"
                         type="warning"
                         icon="el-icon-edit"
                         class="ele-btn-icon"
                         :disabled="!current || current.systemDefault != 0"
                         @click="openEdit(current)"
              >
                修改
              </el-button>
              <el-button v-permission="'sys:role:remove:ba89c990e23a4fe5adca033827705f23'"
                         size="mini"
                         type="danger"
                         icon="el-icon-delete"
                         class="ele-btn-icon"
                         :disabled="!current || current.systemDefault != 0"
                         @click="remove"
              >
                删除
              </el-button>
              <el-button
                         size="mini"
                         type="primary"
                         icon="el-icon-plus"
                         class="ele-btn-icon"
                         @click="openAuth"
              >
                权限
              </el-button>
            </div>
          </ele-toolbar>
          <div class="ele-border-lighter">
            <el-tree
              ref="tree"
              :data="roleList"
              highlight-current
              node-key="roleId"
              :props="{ label: 'roleName' }"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              :default-expand-all="true"
              @node-click="onNodeClick"
            />
          </div>
        </div>
        <template v-slot:content>
          <user-list
            v-if="current"
            :role-id="current.roleId"
          />
        </template>
      </ele-split-layout>
    </el-card>

    <!-- 编辑弹窗 -->
    <role-edit :data="editData"  :parent-id="parentId" :role-list="roleList" :visible.sync="showEdit" @done="query" />
    <!-- 权限分配弹窗 -->
    <role-auth :data="current" :visible.sync="showAuth" />
  </div>
</template>

<script>
  import UserList from '../user/index.vue';
  import RoleEdit from './components/role-edit.vue';
  import RoleAuth from './components/role-auth.vue';
  import { listRoles, removeRole } from '@/api/system/role';

  export default {
    name: 'SystemRole',
    components: {
      UserList,
      RoleEdit,
      RoleAuth
    },
    data() {
      return {
        filterText: '',
        loading:false,
        // 表格选中数据
        selection: [],
        // 当前编辑数据
        current: null,
        // 编辑回显数据
        editData: null,
        // 是否显示编辑弹窗
        showEdit: false,
        // 是否显示导入弹窗
        showAuth: false,
        // 全部角色数据
        roleList: [],
        // 上级id
        parentId: null
      };
    },
    created() {
      this.query();
    },
    methods: {
      filterNode(value, data) {
        if (!value) return true;
        return data.roleName.indexOf(value) !== -1;
      },
      /* 查询 */
      query() {
        this.loading = true;
        listRoles({detail:true})
          .then((list) => {
            this.loading = false;
            this.roleList = this.$util.toTreeData({
              data: list,
              idField: 'roleId',
              parentIdField: 'parentId'
            });
            this.$nextTick(() => {
              this.onNodeClick(this.roleList[0]);
            });
          }).catch((e) => {
          this.loading = false;
          this.$message.error(e.message);
        });
      },
      /* 选择数据 */
      onNodeClick(row) {
        if (row) {
          this.current = row;
          this.parentId = row.roleId;
          this.$refs.tree.setCurrentKey(row.roleId);
        } else {
          this.current = null;
          this.parentId = null;
        }
      },
      /* 显示编辑 */
      openEdit(row) {
        this.editData = row;
        this.showEdit = true;
      },
      /* 显示分配权限 */
      openAuth() {
        this.showAuth = true;
      },
      /* 删除 */
      remove() {
        if (this.current.children?.length) {
          this.$message.error('请先删除子节点');
          return;
        }
        this.$confirm('确定要删除选中的角色吗?', '提示',{ type: 'warning',lockScroll:false }).then(() => {
          const loading = this.$loading({ lock: true });
          removeRole(this.current.roleId)
            .then((msg) => {
              loading.close();
              this.$message.success(msg);
              this.query();
            })
            .catch((e) => {
              loading.close();
              this.$message.error(e.message);
            });
        }).catch(() => {});
      }
    },
    watch: {
      filterText(val) {
        this.$refs.tree.filter(val);
      }
    },
  };
</script>

<style lang="scss" scoped>
.ele-table-tool-default{
  border: 0px;
  background-color: transparent;
}
.ele-border-lighter {
  height: calc(100vh - 264px);
  box-sizing: border-box;
  overflow: auto;
}

.ele-border-lighter :deep(.el-tree-node__content) {
  height: 40px;

  & > .el-tree-node__expand-icon {
    margin-left: 10px;
  }
}
</style>
