<!-- 租户选择下拉框 -->
<template>
  <el-popover v-show="dataList && dataList.length>1"
    :width="300"
    trigger="click"
    v-model="visible"
    class="ele-notice-group"
    transition="el-zoom-in-top"
    popper-class="ele-notice-pop"
  >
    <template v-slot:reference>
      <div class="ele-admin-header-tool-item">
        <i class="el-icon-office-building"><span style="font-size: 14px; padding-left: 2px;position: relative;top: -2px;">组织</span></i>
      </div>
    </template>
    <div style="padding: 10px" class="sys-tenant-list">
      <el-input
        placeholder="输入关键字进行过滤"
        v-model="filterText">
      </el-input>

      <el-tree style="padding-top: 10px" v-loading="loading"
        ref="tree"
        :data="data"
        node-key="tenantId"
        :current-node-key="tenantId"
        empty-text="暂无数据"
        highlight-current
        :props="{ label: 'tenantName' }"
        :expand-on-click-node="false"
        :default-expand-all="true"
        :filter-node-method="filterNode"
        @node-click="updateValue"
      />
    </div>
  </el-popover>
</template>

<script>
import store from '@/store';
import { switchTenant} from '@/api/login';
  export default {
    data() {
      return {
        filterText: '',
        // 是否显示
        visible: false,

        loading:false,

        tenantId:'',
        // 字典数据
        data: [],
        dataList: []
      };
    },
    created() {
        this.tenantId = store.state.user.info.currentTenantId || '';
        this.dataList = store.state.user.tenants;
        this.data = this.$util.toTreeData({
          data: store.state.user.tenants,
          idField: 'tenantId',
          parentIdField: 'parentId'
        });
    },
    methods: {
      /* 更新选中数据 */
      updateValue(item) {
        this.loading = true;
        switchTenant({tenantId:item.tenantId,remember:false}).then((msg) => {
            this.loading = false;
            this.visible = false;
            this.$message.success(msg);
            window.location.href='/';
          }).catch((e) => {
            this.loading = false;
            this.$message.error(e.message);
          });
      },
      filterNode(value, data) {
        if (!value) return true;
        return data.tenantName.indexOf(value) !== -1;
      },
    },
    watch: {
      filterText(val) {
        this.$refs.tree.filter(val);
      }
    },
  };
</script>
<style lang="scss" scoped>
.sys-tenant-list {
  height: 300px;
  box-sizing: border-box;
  overflow: auto;
}

.sys-tenant-list :deep(.el-tree-node__content) {
  height: 40px;
  & > .el-tree-node__expand-icon {
      margin-left: 10px;
    }
}
</style>
