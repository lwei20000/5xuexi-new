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
              <el-button v-permission="'sys:group:save:2e2ca0f788b44a82b06295b0d13b2552'"
                         size="mini"
                         type="primary"
                         icon="el-icon-plus"
                         class="ele-btn-icon"
                         @click="openEdit()"
              >
                添加
              </el-button>
              <el-button v-permission="'sys:group:update:13a5e6afdbe84b54bd3b6535131d89f9'"
                         size="mini"
                         type="warning"
                         icon="el-icon-edit"
                         class="ele-btn-icon"
                         :disabled="!current"
                         @click="openEdit(current)"
              >
                修改
              </el-button>
              <el-button v-permission="'sys:group:remove:6223a9183d104654875054cd3f2131a9'"
                         size="mini"
                         type="danger"
                         icon="el-icon-delete"
                         class="ele-btn-icon"
                         :disabled="!current"
                         @click="remove"
              >
                删除
              </el-button>
            </div>
          </ele-toolbar>
          <div class="ele-border-lighter">
            <el-tree
              ref="tree"
              :data="groupList"
              highlight-current
              node-key="groupId"
              :props="{ label: 'groupName' }"
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
            :group-id="current.groupId"
          />
        </template>
      </ele-split-layout>
    </el-card>

    <!-- 编辑弹窗 -->
    <group-edit :data="editData"  :parent-id="parentId" :group-list="groupList" :visible.sync="showEdit" @done="query" />
  </div>
</template>

<script>
  import UserList from '../user/index.vue';
  import GroupEdit from './components/group-edit.vue';
  import { listGroups, removeGroup } from '@/api/system/group';

  export default {
    name: 'SystemGroup',
    components: {
      UserList,
      GroupEdit
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
        // 全部分组数据
        groupList: [],
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
        return data.groupName.indexOf(value) !== -1;
      },
      /* 查询 */
      query() {
        this.loading = true;
        listGroups()
          .then((list) => {
            this.loading = false;
            this.groupList = this.$util.toTreeData({
              data: list,
              idField: 'groupId',
              parentIdField: 'parentId'
            });
            this.$nextTick(() => {
              this.onNodeClick(this.groupList[0]);
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
          this.parentId = row.groupId;
          this.$refs.tree.setCurrentKey(row.groupId);
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
      /* 删除 */
      remove() {
        if (this.current.children?.length) {
          this.$message.error('请先删除子节点');
          return;
        }
        this.$confirm('确定要删除选中的分组吗?', '提示', { type: 'warning',lockScroll:false }).then(() => {
          const loading = this.$loading({ lock: true });
          removeGroup(this.current.groupId)
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
