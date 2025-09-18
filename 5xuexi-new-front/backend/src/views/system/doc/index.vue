<!-- 开发文档 -->
<template>
  <div class="ele-body " v-loading="loading" >
    <div style="margin: 0px auto;min-height: 60px;position: relative;">
      <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleChange">
        <el-menu-item :index="item.dictDataCode" v-for="(item,index) in docTypeList" :key="index" >{{item.dictDataName}}</el-menu-item>
      </el-menu>
      <router-link style="position: absolute;top: 20px; right: 15px;" type="warning" to="/static/feedback"><i class="el-icon-s-help"></i>意见反馈</router-link>
    </div>

    <ele-split-layout style="min-height: calc(100vh - 126px);" class="ele-bg-white"
      width="316px"
      allow-collapse
      :leftStyle="{'border-right':'1px solid #ededed'}"
      :right-style="{ overflow: 'hidden','padding': '20px 20px 75px','word-wrap': 'break-word'}"
      :collapseBtnStyle="{width:'20px'}"
    >
    <div>
        <div style="width: 90%; margin-left: 5%;margin-top: 10px;">
          <el-select v-model="defaultRoleId" v-show="roleData.length>1" placeholder="选择角色" style="width: 100%" @change="nodeRoleClick">
            <el-option
              v-for="item in roleData"
              :key="item.value"
              :label="item.roleName"
              :value="item.roleId">
            </el-option>
          </el-select>

          <el-input style="padding-top: 10px;"
                    placeholder="输入关键字进行过滤"
                    v-model="filterText">
          </el-input>

        </div>
        <div>
          <!-- 操作按钮 -->
          <ele-toolbar class="ele-toolbar-actions">
            <div style="margin: 5px 0">
              <el-button v-permission="'sys:doc:save:43733cfe2cf94e3db1c953e1275e6bfa'"
                         size="small"
                         type="primary"
                         icon="el-icon-plus"
                         class="ele-btn-icon"
                         @click="openEdit()"
              >
                添加
              </el-button>
              <el-button v-permission="'sys:doc:update:ea93c40c0e32419c9e3b5201f357b988'"
                         size="small"
                         type="warning"
                         icon="el-icon-edit"
                         class="ele-btn-icon"
                         :disabled="!current"
                         @click="openEdit(current)"
              >
                修改
              </el-button>
              <el-button v-permission="'sys:doc:remove:ba89c990e23a4fe5adca033827705f23'"
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
              node-key="docId"
              highlight-current
              :props="{ label: 'name' }"
              :expand-on-click-node="false"
              :default-expand-all="false"
              :filter-node-method="filterNode"
              @node-click="nodeClick"
            />
          </div>
        </div>
      </div>
      <template v-slot:content>
<!--        <div style="margin-left: 260px;min-height: 100vh;background: #fff;" class="wdms">-->
        <div>
          <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item v-for="(item,index) in breadcrumbList" :key="index" >{{item}}</el-breadcrumb-item>
          </el-breadcrumb>
          <div class="wdms_html"  v-html="content"></div>
        </div>
      </template>

    </ele-split-layout>



    <!-- 编辑弹窗 -->
    <doc-edit
      :visible.sync="showEdit"
      :data="editData"
      :parent-id="parentId"
      :doc-list="data"
      :doc-type="activeIndex"
      :default-role-id="defaultRoleId"
      @done="loadData"
    />

  </div>
</template>

<script>
import {listDictionaryData} from "@/api/system/dictionary-data";
import {listAdminDocs, listDocs, removeDoc} from "@/api/system/doc";
import DocEdit from './components/doc-edit.vue';
import store from "@/store";
import {listDefaultRoles} from "@/api/system/default-role";
import Prism from "../../../../public/tinymce/skins/prism/prism.js"; //引入插件

export default {
  name: 'TOpenDocument',
  components: { DocEdit },
  data() {
    return {
      defaultRoleId:undefined,
      roleData:[],
      loading:false,
      showEdit:false,
      editData:null,
      docTypeList:[],
      activeIndex:'',
      breadcrumbList:[],
      filterText: '',
      data: [],
      content:'',  //富文本
      current:null, //当前点击的文档
      parentId:undefined //当前文档id
    }
  },
  mounted() {
    listDictionaryData({dictCode: 'doc_type'}).then((list) => {
        this.docTypeList = list;
        if( this.docTypeList && this.docTypeList.length>0){
          this.activeIndex = this.docTypeList[0].dictDataCode;
          this.loadData();
        }
    }).catch((e) => {
      this.$message.error(e.message);
    });

    if (this.$hasPermission('sys:doc:save:43733cfe2cf94e3db1c953e1275e6bfa')) {
      listDefaultRoles().then((list) => {
        if(list && list.length>0){
          for(var i=0;i<list.length;i++){
            var item = list[i];
            item.roleName = item.tenantType+"_"+item.roleName;
          }
        }
        this.roleData = list;
        if(this.roleData &&  this.roleData.length>0){
          this.defaultRoleId = this.roleData[0].roleId;
          this.loadData();
        }
      }).catch((e) => {
        this.$message.error(e.message);
      });
    }else{
      var list = store.state.user?.info?.roles;
      if(list && list.length>0){
        for(var i=0;i<list.length;i++){
          var _item={};
          if(list[i].systemDefault > 0){
            _item.roleId = list[i].systemDefault;
            _item.roleName = list[i].roleName;
            this.roleData.push(_item);
          }
        }
      }
      if( this.roleData &&  this.roleData.length>0){
        this.defaultRoleId = this.roleData[0].roleId;
        this.loadData();
      }
    }
  },
  methods: {
    nodeRoleClick(item){
      this.defaultRoleId = item;
      this.content ='';
      this.breadcrumbList=[];
      this.loadData();
    },
    /* 显示编辑 */
    openEdit(item) {
      this.editData = item;
      this.showEdit = true;
    },
    /* 删除 */
    remove() {
      this.$confirm('确定要删除选中的文档吗?', '提示', { type: 'warning',lockScroll:false }).then(() => {
          const loading = this.$loading({ lock: true });
          removeDoc(this.current.docId).then((msg) => {
              loading.close();
              this.$message.success(msg);
              this.content ='';
              this.breadcrumbList=[];
              this.loadData();
            }).catch((e) => {
              loading.close();
              this.$message.error(e.message);
            });
        })
        .catch(() => {});
    },
    handleChange(val){
      this.activeIndex = val;
      this.content ='';
      this.breadcrumbList=[];
      this.loadData();
    },
    // 加载表格数据
    loadData() {
      if(this.activeIndex && this.defaultRoleId){
        this.loading = true;
        var list = this.$hasPermission('sys:doc:save:43733cfe2cf94e3db1c953e1275e6bfa')?listAdminDocs:listDocs;
        list({docType: this.activeIndex,defaultRoleId:this.defaultRoleId}).then((list) => {
          this.loading = false;
          this.data = this.$util.toTreeData({
            data: list,
            idField: 'docId',
            parentIdField: 'parentId'
          });
          if(this.data && this.data.length>0){
            this.defaultUnfold(this.data[0]);
          }else{
            this.current = null;
            this.parentId = undefined;
          }
        }).catch((e) => {
          this.loading = false;
          this.$message.error(e.message);
        });
      }
    },

    defaultUnfold(item){
      if(item.content){
        this.$nextTick(() => {
          this.nodeClick(item);
        });
      }else{
        this.$nextTick(() => {
          this.$refs.tree.store.nodesMap[item.docId].expanded = true
        });
        if(item.children && item.children.length>0){
          this.defaultUnfold(item.children[0])
        }
      }
    },

    filterNode(value, data) {
      if (!value) return true;
      return data.name.indexOf(value) !== -1;
    },
    nodeClick(item){
      this.$refs.tree.setCurrentKey(item.docId);
      this.current = item;
      this.parentId = item.docId;
      this.breadcrumbList=[];
      this.defaultBreadcrumb(item);
      if(item.content){
        this.content = item.content;
      }else{
        this.content='';
      }
    },
    defaultBreadcrumb(item){
      var _item = this.$refs.tree.store.nodesMap[item.docId];
      this.breadcrumbList.unshift(_item.data.name);
      if(_item.level > 1) {
        this.defaultBreadcrumb(_item.parent.data);
      }else if(_item.level == 1){
        this.breadcrumbList.unshift(this.activeIndex);
      }
    }
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    },
    content(){
      this.$nextTick(() => {
        Prism.highlightAll()
      })
    }
  },
}
</script>

<style lang="scss" scoped>
.ele-border-lighter {
  min-height: calc(100vh - 264px);
  box-sizing: border-box;
  overflow: auto;
  padding-bottom: 20px;
}

.ele-border-lighter :deep(.el-tree-node__content) {
  height: 40px;

  & > .el-tree-node__expand-icon {
    margin-left: 10px;
  }
}

.ele-table-tool-default{
  border: 0px;
  background-color: transparent;
}

.el-menu--horizontal > .el-menu-item,.el-menu--horizontal > .el-menu-item.is-active {
  border-bottom: none;
}
.wdms_html{
  padding-top:10px;
}
.el-menu-demo{
  displey:-webkit-flex; display:flex; -webkit-flex-flow:row nowrap;
  flex-flow:row nowrap; overflow-x:auto; list-style:none;
}
/* 隐藏滚动条 */
.el-menu-demo::-webkit-scrollbar {
  display:none;
}
.el-menu-demo{
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.el-menu-demo li {
  display:inline-block;
}
</style>
