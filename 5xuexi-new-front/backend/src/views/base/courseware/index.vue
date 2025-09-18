<template>
  <ele-modal :lock-scroll="false"
    width="90%"
    :visible="visible"
    :centered="true"
    :close-on-click-modal="true"
    custom-class="ele-dialog-form"
    title="章节课时维护"
    @update:visible="updateVisible"
  >
    <el-card shadow="never" style=" padding-top: 0;">
      <!-- 搜索表单 -->
      <courseware-search @search="reload" />
      <!-- 数据表格 -->
      <ele-pro-table
        ref="table"
        row-key="coursewareId"
        :columns="columns"
        :datasource="datasource"
        :default-expand-all="true"
        :need-page="false"
        :parse-data="parseData"
        cache-key="systemCoursewareTable"
        @done="onDone"
      >
        <!-- 表头工具栏 -->
        <template v-slot:toolbar>
          <el-button v-if="$hasPermission('base:course:save:02efa9f1b1854a9f9dd9d89fcc1867c8') && data.isEdit"
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
          <el-button v-if="$hasPermission('base:course:save:02efa9f1b1854a9f9dd9d89fcc1867c8') && data.isEdit"
                     size="small"
                     type="primary"
                     icon="el-icon-plus"
                     class="ele-btn-icon"
                     @click="openQiniu()"
          >
            批量上传视频
          </el-button>
          <el-button v-if="$hasPermission('base:course:save:02efa9f1b1854a9f9dd9d89fcc1867c8') && data.isEdit"
                     size="small"
                     type="primary"
                     icon="el-icon-plus"
                     class="ele-btn-icon"
                     @click="openCopy()"
          >
            复制章节课时
          </el-button>

        </template>

        <!-- 视频列 -->
        <template v-slot:fileUrl="{ row }">
          <el-link v-if="row.fileUrl" type="primary" :underline="false" @click="openPreview(row)">
            预览
          </el-link>
        </template>

        <!-- 操作列 -->
        <template v-slot:action="{ row }">
          <el-link  v-if="$hasPermission('base:course:save:02efa9f1b1854a9f9dd9d89fcc1867c8') && data.isEdit"
            type="primary"
            :underline="false"
            icon="el-icon-plus"
            @click="openEdit(null, row.coursewareId)"
          >
            添加
          </el-link>
          <el-link v-if="$hasPermission('base:course:update:9db31879a48e44c5a3c6cd802172418d') && data.isEdit"
            type="primary"
            :underline="false"
            icon="el-icon-edit"
            @click="openEdit(row)"
          >
            修改
          </el-link>
          <el-popconfirm v-if="$hasPermission('base:course:remove:a0a553c5733544d4b275d68506fe7b40') && data.isEdit"
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
          <el-link  v-if="$hasPermission('base:course:save:02efa9f1b1854a9f9dd9d89fcc1867c8') && data.isEdit"
                   type="primary"
                   :underline="false"
                   icon="el-icon-edit"
                   @click="openQiniu(row)"
          >
            上传视频
          </el-link>
          <el-link  v-if="$hasPermission('base:course:save:02efa9f1b1854a9f9dd9d89fcc1867c8') && data.isEdit"
                    type="primary"
                    :underline="false"
                    icon="el-icon-edit"
                    @click="updateDuration(row)"
          >
            更新时长
          </el-link>
        </template>
      </ele-pro-table>
    </el-card>
    <!-- 编辑弹窗 -->
    <courseware-edit
      :course-id="data.courseId"
      :data="current"
      :parent-id="parentId"
      :courseware-list="coursewareList"
      :visible.sync="showEdit"
      @done="reload"
    />

    <!-- 预览 -->
    <courseware-preview :data="current" :visible.sync="showPreview"/>
    <!-- 视频上传 -->
    <qiniuVue :data="current" :multiple="multipleUpload" :visible.sync="showQiniu"  v-if="showQiniu" @done="reload"/>
    <!-- copy弹窗 -->
    <courseware-copy :data="data" :visible.sync="showCopy" @done="reload" />
  </ele-modal>

</template>

<script>
  import CoursewareSearch from './components/courseware-search.vue';
  import CoursewareEdit from './components/courseware-edit.vue';
  import CoursewareCopy from './components/courseware-copy.vue';
  import CoursewarePreview from './components/courseware-preview.vue';
  import { listCoursewares, removeCourseware } from '@/api/base/courseware';
  import qiniuVue from '../courseware/components/qiniu.vue';
  import {updateCourseware } from '@/api/base/courseware';
  export default {
    props: {
      // 弹窗是否打开
      visible: Boolean,
      data: Object
    },
    name: 'BaseCourseware',
    components: { CoursewareSearch, CoursewareEdit , CoursewarePreview,qiniuVue,CoursewareCopy},
    data() {
      return {
        multipleUpload:true,
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
            prop: 'coursewareName',
            label: '名称',
            showOverflowTooltip: true,
            minWidth: 180,
            slot: 'title'
          },
          {
            prop: 'sortNumber',
            label: '排序',
            showOverflowTooltip: true,
            minWidth: 40
          },
          {
            prop: 'duration',
            label: '时长',
            showOverflowTooltip: true,
            minWidth: 40,
            formatter: (_row, _column, cellValue) => {
              return this.secondsToMinSeconds(cellValue);
            }
          },
          {
            prop: 'size',
            label: '文件大小',
            showOverflowTooltip: true,
            minWidth: 40
          },
          {
            prop: 'fileType',
            label: '文件类型',
            align: 'center',
            showOverflowTooltip: true,
            minWidth: 40
          },
          {
            prop: 'fileUrl',
            label: '文件预览',
            align: 'center',
            showOverflowTooltip: true,
            minWidth: 40,
            slot: 'fileUrl'
          },
          {
            prop: 'updateTime',
            label: '更新时间',
            showOverflowTooltip: true,
            minWidth: 90,
            formatter: (_row, _column, cellValue) => {
              return this.$util.toDateString(cellValue);
            }
          },
          {
            columnKey: 'action',
            label: '操作',
            width: 360,
            align: 'center',
            resizable: false,
            slot: 'action',
            showOverflowTooltip: true,
            show:this.data.isEdit
          }
        ],
        // 当前编辑数据
        current: null,
        // 是否显示编辑弹窗
        showEdit: false,
        // 是否显示编辑弹窗
        showPreview:false,
        // 全部课件数据
        coursewareList: [],
        // 上级章节id
        parentId: null,
        // 七牛视频
        showQiniu:false,
        // 复制
        showCopy:false
      };
    },
    methods: {
      /* 表格数据源 */
      datasource({where}) {
        where.courseId = this.data.courseId;
        return  listCoursewares({ ...where});
      },
      /* 数据转为树形结构 */
      parseData(data) {
        return this.$util.toTreeData({
          data: data,
          idField: 'coursewareId',
          parentIdField: 'parentId'
        });
      },
      /* 表格渲染完成回调 */
      onDone({ data }) {
        this.coursewareList = data;
      },
      /* 刷新表格 */
      reload(where) {
        this.$refs.table.reload({ where: where });
      },
      /* 显示上传 */
      openQiniu(row) {
        if(row){
          this.multipleUpload=false;
          this.current = row;//单个上传
        }else{
          this.multipleUpload=true;
          this.current = this.data;//批量上传
        }
        this.showQiniu = true;
      },
      openCopy(){
        this.showCopy = true;
      },
      /* 显示编辑 */
      openEdit(row, parentId) {
        this.current = row;
        this.parentId = parentId;
        this.showEdit = true;
      },
      openPreview(row){
        this.current = row;
        this.showPreview = true;
      },
      /* 删除 */
      remove(row) {
        if (row.children?.length) {
          this.$message.error('请先删除子节点');
          return;
        }
        const loading = this.$loading({ lock: true });
        removeCourseware(row.coursewareId)
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
      updateDuration(row){
        if(!row.fileUrl){
          this.$message.error('没有视频');
        }
        var audio = new Audio();
        audio.src = row.fileUrl;
        audio.addEventListener("loadedmetadata",  () =>{
          row.duration = parseInt(audio.duration);
          updateCourseware(row).then(() => {
            this.$message.success("更新视频时长成功");
          }).catch((e) => {
            this.$message.error(e.message);
          });
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
      /* 更新visible */
      updateVisible(value) {
        this.$emit('update:visible', value);
      },
      secondsToMinSeconds(dateTime){
        if(dateTime==null){
          return "";
        }
        var theTime = parseInt(dateTime);// 秒
        var theTime1 = 0;// 分
        if(theTime > 60) {
          theTime1 = parseInt(theTime/60);
          theTime = parseInt(theTime%60);
        }
        if(theTime < 10){
          theTime = "0"+theTime;
        }
        if(theTime1 < 10){
          theTime1 = "0"+theTime1;
        }
        return theTime1+":"+theTime;
      },
    }
  };
</script>
