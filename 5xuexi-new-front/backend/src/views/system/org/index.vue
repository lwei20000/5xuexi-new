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
              <el-button v-permission="'sys:org:save:2e2ca0f789b44a82b06295b0d13b2551'"
                size="small"
                type="primary"
                icon="el-icon-plus"
                class="ele-btn-icon"
                @click="openEdit()"
              >
                添加
              </el-button>
              <el-button v-permission="'sys:org:update:13a5e5afdbe84b54bd3b6535131d89f9'"
                size="small"
                type="warning"
                icon="el-icon-edit"
                class="ele-btn-icon"
                :disabled="!current"
                @click="openEdit(current)"
              >
                修改
              </el-button>
              <el-button v-permission="'sys:org:remove:6233a9183d104654875074cd3f2131a9'"
                size="small"
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
              :data="data"
              highlight-current
              node-key="orgId"
              :props="{ label: 'orgName' }"
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
            :org-id="current.orgId"
          />
        </template>
      </ele-split-layout>
    </el-card>
    <!-- 编辑弹窗 -->
    <org-edit
      :visible.sync="showEdit"
      :data="editData"
      :parent-id="parentId"
      :org-list="data"
      @done="query"
    />
  </div>
</template>

<script>
  import UserList from '../user/index.vue';
  import OrgEdit from './components/org-edit.vue';
  import {
    listOrgs,
    removeOrg
  } from '@/api/system/org';

  export default {
    name: 'SystemOrg',
    components: { UserList, OrgEdit },
    data() {
      return {
        filterText: '',
        // 加载状态
        loading: true,
        // 列表数据
        data: [],
        // 选中数据
        current: null,
        // 是否显示表单弹窗
        showEdit: false,
        // 编辑回显数据
        editData: null,
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
        return data.orgName.indexOf(value) !== -1;
      },
      /* 查询 */
      query() {
        this.loading = true;
        listOrgs({detail:true})
          .then((list) => {
            this.loading = false;
            this.data = this.$util.toTreeData({
              data: list,
              idField: 'orgId',
              parentIdField: 'parentId'
            });
            this.$nextTick(() => {
              this.onNodeClick(this.data[0]);
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
          this.parentId = row.orgId;
          this.$refs.tree.setCurrentKey(row.orgId);
        } else {
          this.current = null;
          this.parentId = null;
        }
      },
      /* 显示编辑 */
      openEdit(item) {
        this.editData = item;
        this.showEdit = true;
      },
      /* 删除 */
      remove() {
        if (this.current.children?.length) {
          this.$message.error('请先删除子节点');
          return;
        }
        this.$confirm('确定要删除选中的机构吗?', '提示', { type: 'warning',lockScroll:false }).then(() => {
            const loading = this.$loading({ lock: true });
            removeOrg(this.current.orgId).then((msg) => {
                loading.close();
                this.$message.success(msg);
                this.query();
              }).catch((e) => {
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
