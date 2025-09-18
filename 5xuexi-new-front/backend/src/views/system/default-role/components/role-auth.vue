<!-- 角色权限分配弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="460px"
    title="分配权限"
    :visible="visible"
    @update:visible="updateVisible"
  >
    <el-scrollbar
      v-loading="authLoading"
      style="height: 60vh"
      wrap-style="overflow-x: hidden;"
    >
      <el-row>
        <el-col :span="12">
          <app-type-select :params="{dictCode: 'app_type'}" :clearable="false"  v-model="appType" @input="inputsearch"></app-type-select>
        </el-col>
        <el-col :span="12">
          <el-input placeholder="输入关键字进行过滤" v-model="filterText"></el-input>
        </el-col>
      </el-row>
      <el-tree style="padding-top: 20px;"
        ref="tree"
        show-checkbox
        :data="authData"
        node-key="menuId"
        :default-expand-all="true"
        :props="{ label: 'title' }"
        :default-checked-keys="checkedKeys"
        :filter-node-method="filterNode"
      >
        <template v-slot="{ data }">
          <span>
            <span>
                 <el-tag
                   :type="['success', 'primary','info'][data.menuType]"
                   size="mini"
                   :disable-transitions="true"
                 >
                {{ ['目录','菜单','按钮'][data.menuType] }}
                </el-tag>
           </span>
            <span style="padding-left: 20px;"> <i :class="data.icon"></i> {{ data.title }}</span>
          </span>
        </template>
      </el-tree>
    </el-scrollbar>
    <template v-slot:footer>
      <el-button @click="updateVisible(false)">取消</el-button>
      <el-button v-permission="'sys:defaultRole:update:7c548eca9215461dab0df93bcfbc730f'" type="primary" :loading="loading" @click="save">
        保存
      </el-button>
    </template>
  </ele-modal>
</template>

<script>
  import { listDefaultRoleMenus, updateDefaultRoleMenus } from '@/api/system/default-role';
  import AppTypeSelect from '@/views/system/dictionary/components/dict-data-select.vue';
  export default {
    components: {AppTypeSelect },
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 当前角色数据
      data: Object
    },
    data() {
      return {
        //默认前端菜单
        appType:"app_backend",
        // 权限数据
        authData: [],
        // 权限数据请求状态
        authLoading: false,
        // 提交状态
        loading: false,
        // 角色权限选中的keys
        checkedKeys: [],
        //过滤字段
        filterText: ''
      };
    },
    methods: {
      /* 查询权限数据 */
      query() {
        this.authData = [];
        this.checkedKeys = [];
        if (!this.data) {
          return;
        }
        this.authLoading = true;
        listDefaultRoleMenus(this.data.roleId,this.appType)
          .then((data) => {
            this.authLoading = false;
            // 转成树形结构的数据
            this.authData = this.$util.toTreeData({
              data: data,
              idKey: 'menuId',
              pidKey: 'parentId'
            });
            // 回显选中的数据
            const cks = [];
            this.$util.eachTreeData(this.authData, (d) => {
              if (d.checked && !d.children?.length) {
                cks.push(d.menuId);
              }
            });
            this.checkedKeys = cks;
          })
          .catch((e) => {
            this.authLoading = false;
            this.$message.error(e.message);
          });
      },
      /* 保存权限分配 */
      save() {
        this.loading = true;
        const ids = this.$refs.tree
          .getCheckedKeys()
          .concat(this.$refs.tree.getHalfCheckedKeys());
        updateDefaultRoleMenus(this.data.roleId,this.appType, ids)
          .then((msg) => {
            this.loading = false;
            this.$message.success(msg);
            this.updateVisible(false);
          })
          .catch((e) => {
            this.loading = false;
            this.$message.error(e.message);
          });
      },
      /* 更新visible */
      updateVisible(value) {
        this.$emit('update:visible', value);
      },
      inputsearch(appType){
        this.appType = appType;
        this.filterText=''
        this.query();
      },
      filterNode(value, data) {
        if (!value) return true;
        return data.title.indexOf(value) !== -1 || (data.path && data.path.indexOf(value) !== -1);
      }
    },
    watch: {
      visible(visible) {
        if (visible) {
          this.filterText=''
          this.query();
        }
      },
      filterText(val) {
        this.$refs.tree.filter(val);
      }
    }
  };
</script>
<style>
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
</style>
