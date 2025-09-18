<template>
  <div class="ele-body">
    <el-card  shadow="never">
      <!-- 搜索表单 -->
      <menu-search v-model="appType" @search="reload" />
      <!-- 数据表格 -->
      <ele-pro-table
        ref="table"
        row-key="menuId"
        :where="{'appType':appType}"
        :columns="columns"
        :datasource="datasource"
        :default-expand-all="false"
        :need-page="false"
        :parse-data="parseData"
        cache-key="systemMenuTable"
        @done="onDone"
      >
        <!-- 表头工具栏 -->
        <template v-slot:toolbar>
          <el-button  v-permission="'sys:menu:save:8000d1a8cad74796a4c78fefe0ccac41'"
            size="small"
            type="primary"
            icon="el-icon-plus"
            class="ele-btn-icon"
            @click="openEdit()"
          >
            添加
          </el-button>
          <el-button class="ele-btn-icon" size="small" @click="expandAll">
            展开全部
          </el-button>
          <el-button class="ele-btn-icon" size="small" @click="foldAll">
            折叠全部
          </el-button>
        </template>
        <!-- 标题列 -->
        <template v-slot:title="{ row }">
          <i :class="row.icon"></i> {{ row.title }}
        </template>
        <!-- 类型列 -->
        <template v-slot:menuType="{ row }">
          <el-tag
            v-if="isUrl(row.path)"
            type="danger"
            size="mini"
            :disable-transitions="true"
          >
            外链
          </el-tag>
          <el-tag
            v-else-if="isUrl(row.component)"
            type="warning"
            size="mini"
            :disable-transitions="true"
          >
            内链
          </el-tag>
          <el-tag
            v-else
            :type="['success', 'primary','info'][row.menuType]"
            size="mini"
            :disable-transitions="true"
          >
            {{ ['目录','菜单','按钮'][row.menuType] }}
          </el-tag>
        </template>
        <!-- 操作列 -->
        <template v-slot:action="{ row }">
          <el-link  v-permission="'sys:menu:save:8000d1a8cad74796a4c78fefe0ccac41'"
            type="primary"
            :underline="false"
            icon="el-icon-plus"
            @click="openEdit(null, row.menuId)"
          >
            添加
          </el-link>
          <el-link  v-if="$hasPermission('sys:menu:update:9a7aab59-b6a749c68ab1502276a1cdcd') && row.systemDefault === 0 "
            type="primary"
            :underline="false"
            icon="el-icon-edit"
            @click="openEdit(row)"
          >
            修改
          </el-link>
          <el-popconfirm v-if="$hasPermission('sys:menu:remove:47e47bdd5fe444e4846a3a9df67dcd54') && row.systemDefault === 0 "
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
    <menu-edit
      :appType="appType"
      :data="current"
      :parent-id="parentId"
      :menu-list="menuList"
      :visible.sync="showEdit"
      @done="reload"
    />
  </div>
</template>

<script>
  import MenuSearch from './components/menu-search.vue';
  import MenuEdit from './components/menu-edit.vue';
  import { listMenus, removeMenu } from '@/api/system/menu';

  export default {
    name: 'SystemMenu',
    components: { MenuSearch, MenuEdit },
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
            prop: 'title',
            label: '菜单名称',
            showOverflowTooltip: true,
            minWidth: 110,
            slot: 'title'
          },
          {
            prop: 'path',
            label: '路由地址',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'component',
            label: '组件路径',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'authority',
            label: '权限标识',
            showOverflowTooltip: true,
            minWidth: 110
          },
          {
            prop: 'sortNumber',
            label: '排序',
            align: 'center',
            showOverflowTooltip: true,
            width: 60
          },
          {
            prop: 'hide',
            label: '可见',
            align: 'center',
            showOverflowTooltip: true,
            width: 60,
            formatter: (_row, _column, cellValue) => {
              return ['是', '否'][cellValue];
            }
          },
          {
            prop: 'menuType',
            label: '类型',
            align: 'center',
            showOverflowTooltip: true,
            width: 60,
            slot: 'menuType'
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
            width: 190,
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
        // 全部菜单数据
        menuList: [],
        // 上级菜单id
        parentId: null,
        //默认前端菜单
        appType:"app_backend"
      };
    },
    methods: {
      /* 表格数据源 */
      datasource({ where }) {
        return listMenus({ ...where });
      },
      /* 数据转为树形结构 */
      parseData(data) {
        return this.$util.toTreeData({
          data: data,
          idField: 'menuId',
          parentIdField: 'parentId'
        });
      },
      /* 表格渲染完成回调 */
      onDone({ data }) {
        //if (!this.menuList.length) {
        this.menuList = data;
        //}
      },
      /* 刷新表格 */
      reload(where) {
        if(where && where.appType){
          this.appType = where.appType;
        }
        this.$refs.table.reload({ where: where });
      },
      /* 显示编辑 */
      openEdit(row, parentId) {
        this.current = row;
        this.parentId = parentId;
        this.showEdit = true;
      },
      /* 删除 */
      remove(row) {
        if (row.children?.length) {
          this.$message.error('请先删除子节点');
          return;
        }
        const loading = this.$loading({ lock: true });
        removeMenu(row.menuId)
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
      /* 展开全部 */
      expandAll() {
        this.$refs.table.toggleRowExpansionAll(true);
      },
      /* 折叠全部 */
      foldAll() {
        this.$refs.table.toggleRowExpansionAll(false);
      },
      /* 判断是否是外链 */
      isUrl(url) {
        return !!(
          url &&
          (url.startsWith('http://') ||
            url.startsWith('https://') ||
            url.startsWith('//'))
        );
      },
    }
  };
</script>
