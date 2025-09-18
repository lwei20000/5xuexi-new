<template>
  <div class="ele-body">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <user-major-search @search="reload" />
      <!-- 数据表格 -->
      <ele-pro-table
        ref="table"
        :pageSizes="[10,30,50,100,300]"
        :columns="columns"
        :datasource="datasource"
        :selection.sync="selection"
        cache-key="systemCourseTable"
      >
        <!-- 用户登记照 -->
        <template v-slot:userPics="{ row }">
          <div style="display: flex; justify-content: center;">
            <el-image
              v-if="row.user && row.user.idPhoto"
              :src="row.user.idPhoto"
              :preview-src-list="[row.user.idPhoto]" style="width: 50px; cursor: pointer;"
              :alt="'登记照'"
              @click.native="$event.target.click()"
            ></el-image>
            <span v-else>--</span>
          </div>
        </template>

        <!-- 表头工具栏 -->
        <template v-slot:toolbar>
          <el-button  v-permission="'base:userMajor:save:adaaf9bf787c432d991ca6fd0bf6fc95'"
                      size="small"
                      type="primary"
                      icon="el-icon-plus"
                      class="ele-btn-icon"
                      @click="openEdit()"
          >
            添加
          </el-button>
          <el-button v-permission="'base:userMajor:save:adaaf9bf787c432d991ca6fd0bf6fc95'"
                     size="small"
                     icon="el-icon-upload2"
                     class="ele-btn-icon"
                     @click="openImport"
          >
            导入
          </el-button>
          <el-button v-permission="'base:userMajor:thesisImport:adaaf9bf787c432d991ca6fd0bf6fc95'"
                     size="small"
                     icon="el-icon-upload2"
                     class="ele-btn-icon"
                     @click="openThesisImport"
          >
            导入论文成绩
          </el-button>
          <el-button  v-permission="'base:userMajor:remove:be7616c3-8a2e4ac2a137d87dfdf22662'"
                      size="small"
                      type="danger"
                      icon="el-icon-delete"
                      class="ele-btn-icon"
                      @click="removeBatch"
          >
            删除
          </el-button>
          <el-button v-permission="'base:userMajor:list:bf30a126b7cf4916a7fcd8137f1cebdf'"
                     size="small"
                     icon="el-icon-printer"
                     class="ele-btn-icon"
                     @click="batchPrintTag"
          >
            批量打印档案标签
          </el-button>
          <el-button v-permission="'base:userMajor:printer1:bf30a126b7cf4916a7fcd8137f1cebdf'"
                     size="small"
                     icon="el-icon-printer"
                     class="ele-btn-icon"
                     @click="batchPrintScore(1)"
          >
            批量打印成绩单
          </el-button>
          <el-button v-permission="'base:userMajor:printer2:bf30a126b7cf4916a7fcd8137f1cebdf'"
                     size="small"
                     icon="el-icon-printer"
                     class="ele-btn-icon"
                     @click="batchPrintScore(2)"
          >
            批量打印成绩单
          </el-button>
          <el-button v-permission="'base:userMajor:downloadRegister:bf30a126b7cf4916a7fcd8137f1cebdf'"
                     size="small"
                     icon="el-icon-printer"
                     class="ele-btn-icon"
                     @click="batchDownload(1)"
          >
            批量下载登记表
          </el-button>
          <el-button v-permission="'base:userMajor:downloadEnrollment:bf30a126b7cf4916a7fcd8137f1cebdf'"
                     size="small"
                     icon="el-icon-printer"
                     class="ele-btn-icon"
                     @click="batchDownload(2)"
          >
            批量下载学籍表
          </el-button>
        </template>
        <!-- 操作列 -->
        <template v-slot:action="{ row }">
          <el-link  v-permission="'base:userMajor:update:452847da630c40519612451da8ed520e'"
                    type="primary"
                    :underline="false"
                    icon="el-icon-edit"
                    @click="openEdit(row)"
          >
            修改
          </el-link>
          <el-popconfirm v-permission="'base:userMajor:remove:be7616c3-8a2e4ac2a137d87dfdf22662'"
                         class="ele-action"
                         title="确定要删除此学生专业吗？"
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
    <user-major-edit :data="current" :visible.sync="showEdit" @done="reload" />
    <!-- 导入弹窗 -->
    <user-major-import :visible.sync="showImport" @done="reload" />
    <!-- 导入弹窗 -->
    <user-major-thesis-import :visible.sync="showThesisImport" @done="reload" />
  </div>
</template>

<script>
import UserMajorSearch from './components/user-major-search.vue';
import UserMajorEdit from './components/user-major-edit.vue';
import UserMajorImport from './components/user-major-import.vue';
import {
  pageUserMajors,
  removeUserMajor,
  removeUserMajors,
  downloadRegistration,
  downloadEnrollment
} from '@/api/base/userMajor';
import {listUserCourses} from "@/api/base/userCourse";
import { printPage } from 'ele-admin';
import store from "@/store";
import UserMajorThesisImport from "@/views/base/userMajor/components/user-major-thesis-import";
export default {
  name: 'BaseUserMajor',
  components: {
    UserMajorThesisImport,
    UserMajorSearch,
    UserMajorImport,
    UserMajorEdit
  },
  data() {
    return {
      numArr:{'1':"一",'2':"二",'3':"三",'4':"四",'5':"五",'6':"六",'7':"七",'8':"八",'9':"九",'10':"十"},
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
          prop: 'userId',
          label: '学生',
          sortable: 'custom',
          showOverflowTooltip: true,
          minWidth: 200,
          formatter: (_row) => {
            var userStr = "";
            if(_row.user){
              userStr=_row.user.realname+"_"+_row.user.username;
            }
            return userStr;
          }
        },
        {
          prop: 'userPics',
          label: '登记照',
          minWidth: 50,
          align: 'center',
          slot: 'userPics'
        },
        {
          prop: 'userNumber',
          label: '学号',
          sortable: 'custom',
          showOverflowTooltip: true,
          minWidth: 80
        },
        {
          prop: 'majorId',
          label: '专业',
          sortable: 'custom',
          showOverflowTooltip: true,
          minWidth: 230,
          formatter: (_row) => {
            var majorStr = "";
            if(_row.major){
              majorStr=_row.major.majorYear+"_" +_row.major.majorCode+"_"+
                _row.major.majorName+"_"+_row.major.majorType+"_"+_row.major.majorGradation +
                "_"+_row.major.majorForms+"_"+_row.major.majorLength+"年制";
            }
            return majorStr;
          }
        },
        {
          prop: 'orgId',
          label: '站点',
          sortable: 'custom',
          showOverflowTooltip: true,
          minWidth: 160,
          formatter: (_row) => {
            var orgStr = "";
            if(_row.org){
              orgStr= _row.org.orgFullName;
            }
            return orgStr;
          }
        },
        {
          prop: 'thesisName',
          label: '论文标题',
          sortable: 'custom',
          showOverflowTooltip: true,
          minWidth: 120
        },
        {
          prop: 'thesisScore',
          label: '论文成绩',
          sortable: 'custom',
          showOverflowTooltip: true,
          minWidth: 80
        },
        {
          columnKey: 'action',
          label: '操作',
          width: 130,
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
      showImport: false,

      showThesisImport:false,
      //租户信息
      tenant:{}
    };
  },
  created() {
    var tenantId = store.state.user.info.currentTenantId || '';
    for(var j=0;j<store.state.user.tenants.length;j++) {
      var _item = store.state.user.tenants[j];
      if (_item.tenantId == tenantId) {
        this.tenant = _item;
        break;
      }
    }
  },
  methods: {
    /* 表格数据源 */
    datasource({page, limit, where, order}) {
      return pageUserMajors({...where, ...order, page, limit});
    },
    /* 刷新表格 */
    reload(where) {
      this.$refs.table.reload({page: 1, where: where});
    },
    /* 打开导入弹窗 */
    openImport() {
      this.showImport = true;
    },
    openThesisImport() {
      this.showThesisImport = true;
    },
    /* 显示编辑 */
    openEdit(row) {
      this.current = row;
      this.showEdit = true;
    },
    /* 删除 */
    remove(row) {
      const loading = this.$loading({lock: true});
      removeUserMajor(row.id)
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
    // 批量打印档案标签
    batchPrintTag() {
      if (!this.selection.length) {
        this.$message.error('请至少选择一条数据');
        return;
      }
      var pageHtmls = [];
      var len = Math.ceil(this.selection.length / 3);
      for (var i = 0; i < len; i++) {
        var pageHtml = '<div style="width:520px;height: 978px;margin: 0 auto;">';
        for (var j = 0; j < 3; j++) {
          var mItem = this.selection[i * 3 + j];
          if (mItem) {
            pageHtml += '<div style="padding-top:58px;"><div style="text-align: center;font-size: 12px;border: 1px solid;">';

            pageHtml += '<div style="width: 100%;font-size: 12px;display: flex;height: 32px;line-height: 32px">' +
              '        <div style="width: 20%;border-right: 1px solid;border-bottom: 1px solid">函授站</div>' +
              '        <div style="width: 80%;text-align: left;border-bottom: 1px solid;">&nbsp;&nbsp;&nbsp;&nbsp;' + this.tenant?.tenantName + ' - ' + (mItem.org?.orgName || '') + '</div>' +
              '      </div>';

            pageHtml += '<div style="width: 100%;font-size: 12px;display: flex;height: 32px;line-height: 32px">' +
              '        <div style="width: 20%;border-right: 1px solid;border-bottom: 1px solid">姓名</div>' +
              '        <div style="width: 30%;border-right: 1px solid;border-bottom: 1px solid">' + (mItem.user?.realname || '') + '</div>' +
              '        <div style="width: 20%;border-right: 1px solid;border-bottom: 1px solid">性别</div>' +
              '        <div style="width: 30%;border-bottom: 1px solid">' + (mItem.user?.sex || '') + '</div>' +
              '      </div>';

            pageHtml += '<div style="width: 100%;font-size: 12px;display: flex;height: 32px;line-height: 32px">' +
              '        <div style="width: 20%;border-right: 1px solid;border-bottom: 1px solid">学号</div>' +
              '        <div style="width: 30%;border-right: 1px solid;border-bottom: 1px solid">' + (mItem.userNumber || '') + '</div>' +
              '        <div style="width: 20%;border-right: 1px solid;border-bottom: 1px solid">层次</div>' +
              '        <div style="width: 30%;border-bottom: 1px solid">' + (mItem.major?.majorGradation || '') + '</div>' +
              '      </div>';

            pageHtml += '<div style="width: 100%;font-size: 12px;display: flex;height: 32px;line-height: 32px">' +
              '        <div style="width: 20%;border-right: 1px solid;border-bottom: 1px solid">专业</div>' +
              '        <div style="width: 30%;border-right: 1px solid;border-bottom: 1px solid">' + (mItem.major?.majorName || '') + '</div>' +
              '        <div style="width: 20%;border-right: 1px solid;border-bottom: 1px solid">学制</div>' +
              '        <div style="width: 30%;border-bottom: 1px solid">' + (mItem.major?.majorLength || '') + '年</div>' +
              '      </div>';

            pageHtml += '<div style="width: 100%;font-size: 12px;display: flex">' +
              '        <div style="width: 20%;border-right: 1px solid;padding-top: 40px">档<br>案<br>清<br>单</div>' +
              '        <div style="width: 80%;text-align: left;">' +
              '          <div style="border-bottom: 1px solid;height: 32px;line-height: 32px;">&nbsp;&nbsp;&nbsp;&nbsp;1、' + this.tenant?.tenantName + '高等教育毕业生档案目录</div>' +
              '          <div style="border-bottom: 1px solid;height: 32px;line-height: 32px;">&nbsp;&nbsp;&nbsp;&nbsp;2、' + this.tenant?.tenantName + '学籍资料（考生信息表、录取通知书）</div>' +
              '          <div style="border-bottom: 1px solid;height: 32px;line-height: 32px;">&nbsp;&nbsp;&nbsp;&nbsp;3、' + this.tenant?.tenantName + '学生登记表</div>' +
              '          <div style="border-bottom: 1px solid;height: 32px;line-height: 32px;">&nbsp;&nbsp;&nbsp;&nbsp;4、' + this.tenant?.tenantName + '毕业生生登记表</div>' +
              '          <div style="height: 32px;line-height: 32px;">&nbsp;&nbsp;&nbsp;&nbsp;5、其他</div>' +
              '        </div>' +
              '       </div>';

            pageHtml += ' </div></div>';
          }
        }
        pageHtml += '</div>';
        pageHtmls.push(pageHtml);
      }
      printPage({
        pages: pageHtmls,
        horizontal: false, // 是否横向打印
        blank: false, // 是否打开新页面打印
        close: true, // 如果是打开新页面，打印完是否关闭
        iePreview: true, // 是否兼容 ie 打印预览
        print: true, // 如果是打开新窗口是否自动打印
        padding: undefined, // 页面间距
        isDebug: false, // 调试模式
        height: undefined, // 每一页高度
        width: undefined, // 每一页宽度
        loading: true, // 是否显示 loading
        before: () => { // 打印开始的回调
        },
        done: () => { // 打印完成的回调
        },
        margin: 0, // 打印机页间距
        header: '', // 页眉 html
        footer: '', // 页脚 html
        title: '' // 页面标题，浏览器打印设置显示页眉会显示页面标题，不要标题还应该设置 blank: true
      });
    },
    // 批量打印成绩单（type1:changda；type2:others）
    batchPrintScore(type) {
      if (!this.selection.length) {
        this.$message.error('请至少选择一条数据');
        return;
      }
      const loading = this.$loading({lock: true});
      var p = {};
      p.userMajorIds = this.selection.map((d) => d.id);
      listUserCourses(p).then((data) => {
        loading.close();
        var courseScoreMap = {};
        for (var n = 0; n < data.length; n++) {
          var item = data[n];
          var map = courseScoreMap[item.userId + "_" + item.majorId];
          if (map) {
            var list = map[item.semester];
            if (list) {
              list.push(item);
            } else {
              list = [];
              list.push(item);
            }
            map[item.semester] = list;
          } else {
            map = {};
            var _list = [];
            _list.push(item);
            map[item.semester] = _list;
          }
          courseScoreMap[item.userId + "_" + item.majorId] = map;
        }
        var pageHtmls = [];
        for (var i = 0; i < this.selection.length; i++) {
          var mItem = this.selection[i];
          var pageHtml = '<div><table style="width: 85%;margin: 0 auto;font-size: 12px; ">' +
            '      <tr>' +
            '        <th colspan="4" style="padding: 10px 45px 10px 0; font-size: 28px;font-weight: 400;">' + (this.tenant?.tenantName || '') + '成绩单</th>' +
            '      </tr>' +
            '      <tr style="width: 100%">' +
            '        <td style="width: 18%">学号：' + (mItem.userNumber || '') + '</td>' +
            '        <td style="width: 27%">姓名：' + (mItem.user?.realname || '') + '</td>' +
            '        <td style="width: 28%">身份证：' + (mItem.user?.idCard || '') + '</td>' +
            '        <td style="width: 27%">年级：' + (mItem.major?.majorYear || '') + '级</td>' +
            '      </tr>' +
            '      <tr style="width: 100%">' +
            '        <td style="width: 18%">层次：' + (mItem.major?.majorGradation || '') + '</td>' +
            '        <td style="width: 27%">专业：' + (mItem.major?.majorName || '') + '</td>' +
            '        <td style="width: 28%">学制：' + (mItem.major?.majorLength || '') + '年</td>' +
            '        <td style="width: 27%">函授站：' + (mItem.org?.orgName || '') + '</td>' +
            '      </tr>' +
            '    </table>' +
            '    <table class="ele-printer-table" style="width:85%;margin: 0 auto;margin-top: 10px;text-align: center;">';
          if (type == 1) {
            pageHtml += '      <tr  style="width: 100%;font-size: 12px;">\n' +
              '        <th style="width: 8%;font-weight: 400;padding: 2px 10px">学年</th>\n' +
              '        <th style="width: 8%;font-weight: 400;padding: 2px 10px">学期</th>\n' +
              '        <th style="width: 22%;font-weight: 400;padding: 2px 10px">课程</th>\n' +
              '        <th style="width: 8%;font-weight: 400;padding: 2px 10px">考试成绩</th>\n' +
              '        <th style="width: 8%;font-weight: 400;padding: 2px 10px">补考成绩</th>\n' +
              '        <th style="width: 8%;font-weight: 400;padding: 2px 10px">学期</th>\n' +
              '        <th style="width: 22%;font-weight: 400;padding: 2px 10px">课程</th>\n' +
              '        <th style="width: 8%;font-weight: 400;padding: 2px 10px">考试成绩</th>\n' +
              '        <th style="width: 8%;font-weight: 400;padding: 2px 10px">补考成绩</th>\n' +
              '      </tr>';
          } else {
            pageHtml += '<tr  style="width: 100%;font-size: 12px;">\n' +
              '        <th style="width: 9%;font-weight: 400;padding: 5px">学年</th>\n' +
              '        <th style="width: 9%;font-weight: 400;padding: 5px">学期</th>\n' +
              '        <th style="width: 23%;font-weight: 400;padding: 5px">课程</th>\n' +
              '        <th style="width: 9%;font-weight: 400;padding: 5px">成绩</th>\n' +
              '        <th style="width: 9%;font-weight: 400;padding: 5px">学年</th>\n' +
              '        <th style="width: 9%;font-weight: 400;padding: 5px">学期</th>\n' +
              '        <th style="width: 23%;font-weight: 400;padding: 5px">课程</th>\n' +
              '        <th style="width: 9%;font-weight: 400;padding: 5px">成绩</th>\n' +
              '      </tr>';
          }
          var _map = courseScoreMap[mItem.userId + "_" + mItem.majorId];
          if (type == 1) {
            var len = Math.floor(mItem.major?.majorLength || 0) + 1;
            var totalRow = 0;
            for (var m = 1; m < len; m++) {
              var list1 = _map[m * 2 - 1] || [];
              var list2 = _map[m * 2] || [];
              var rowspan = list1.length;
              if (rowspan < list2.length) {
                rowspan = list2.length;
              }
              if (rowspan > 0) {
                for (var t = 0; t < rowspan; t++) {
                  totalRow++;
                  if (t == 0) {
                    pageHtml += '      <tr  style="width: 100%;font-weight: 400;font-size: 12px;">\n' +
                      '        <td rowspan="' + rowspan + '">' + this.numArr[m] + '<br>学<br>年</td>\n' +
                      '        <td rowspan="' + rowspan + '">' + this.numArr[m * 2 - 1] + '</td>\n' +
                      '        <td style="padding: 0 5px">' + (list1[t]?.course?.courseName || '') + '</td>\n' +
                      '        <td>' + (list1[t]?.totalScore || (list1[t]?.course?.courseName ? 0 : '')) + '</td>\n' +
                      '        <td></td>\n' +
                      '        <td rowspan="' + rowspan + '">' + this.numArr[m * 2] + '</td>\n' +
                      '        <td style="padding: 0 5px">' + (list2[t]?.course?.courseName || '') + '</td>\n' +
                      '        <td>' + (list2[t]?.totalScore || (list2[t]?.course?.courseName ? 0 : '')) + '</td>\n' +
                      '        <td></td>\n' +
                      '      </tr>\n';
                  } else {
                    pageHtml += '      <tr  style="width: 100%;font-weight: 400;font-size: 12px;">\n' +
                      '        <td style="padding: 0 5px">' + (list1[t]?.course?.courseName || '') + '</td>\n' +
                      '        <td>' + (list1[t]?.totalScore || (list1[t]?.course?.courseName ? 0 : '')) + '</td>\n' +
                      '        <td></td>\n' +
                      '        <td style="padding: 0 5px">' + (list2[t]?.course?.courseName || '') + '</td>\n' +
                      '        <td>' + (list2[t]?.totalScore || (list2[t]?.course?.courseName ? 0 : '')) + '</td>\n' +
                      '        <td></td>\n' +
                      '      </tr>\n';
                  }
                }
              } else {
                totalRow += 1.5;
                pageHtml += '  <tr  style="width: 100%;font-weight: 400;font-size: 12px;">\n' +
                  '        <td>' + this.numArr[m] + '<br>学<br>年</td>\n' +
                  '        <td>' + this.numArr[m * 2 - 1] + '</td>\n' +
                  '        <td></td>\n' +
                  '        <td></td>\n' +
                  '        <td></td>\n' +
                  '        <td>' + this.numArr[m * 2] + '</td>\n' +
                  '        <td></td>\n' +
                  '        <td></td>\n' +
                  '        <td></td>\n' +
                  '      </tr>\n';
              }
            }

            if (mItem.major?.majorLength == '2.5') { //changda
              pageHtml += ' <tr  style="width: 100%;font-weight: 400;font-size: 12px;">\n' +
                '             <td>三<br>学<br>年</td>\n' +
                '             <td>五</td>\n' +
                '             <td colspan="5" style="height: 50px;">毕业论文</td>\n' +
                '             <td colspan="2">' + (mItem?.thesisScore || '') + '</td>\n' +
                '          </tr>';
            } else {  // others
              pageHtml += ' <tr  style="width: 100%;font-weight: 400;font-size: 12px;">\n' +
                '             <td colspan="2">毕业论文</td>\n' +
                '             <td colspan="5" style="height: 50px;">' + (mItem?.thesisName || '') + '</td>\n' +
                '             <td colspan="2">' + (mItem?.thesisScore || '') + '</td>\n' +
                '          </tr>';
            }

            // 计算毕业日期
            const majorYear = parseInt(mItem.major?.majorYear || 0, 10);
            const majorLength = parseFloat(mItem.major?.majorLength || 0);

            const nowDate = new Date(majorYear + Math.floor(majorLength) + "/01/15");
            if (majorLength > Math.floor(majorLength)) {
              nowDate.setMonth(6)
            }
            let date = {
              year: nowDate.getFullYear(),
              month: nowDate.getMonth() + 1,
              date: nowDate.getDate()
            }
            if (date.month < 10) {
              date.month = '0' + date.month
            }
            if (date.date < 10) {
              date.date = '0' + date.date
            }
            let sum = date.year + '年' + date.month + '月' + date.date + '日'; // 毕业日期

            // 根据租户id不同，计算审查人信息
            let tenantId = store.state.user.info.currentTenantId;
            let scr = ''; // 审查人信息
            if (tenantId == 7) {
              scr = '审查人: 杨柳';
            } else {
              scr = '审查人:';
            }

            pageHtml += '<tr  style="width: 100%;font-weight: 400;font-size: 14px;">\n' +
              '             <td colspan="9"><div><p style="height:calc(100vh - ' + (totalRow * 32 + 380) + 'px);min-height: 40px">毕业资格审查结果</p></div>' +
              '                             <div style="text-align: right"><p style="margin-right: 70px">' + scr + '</p></div>' +
              '                             <div style="text-align: right"><p style="margin-right: 55px">' + sum + '</p></div>' +
              '             </td>\n'
            '          </tr>\n';

          } else {
            var _len = (mItem.major?.majorLength || 0) * 2 + 1;
            var index = 0;
            for (var j = 1; j < _len; j++) {
              var _list1 = _map[j] || [];
              for (var h = 0; h < _list1.length; h++) {
                index++;
                if (index % 2 == 1) {
                  pageHtml += ' <tr  style="width: 100%;font-weight: 400;font-size: 12px;">';
                }
                pageHtml += '<td>' + Math.ceil(j / 2) + '</td>\n' +
                  '        <td>' + j + '</td>' +
                  '        <td style="padding: 0 5px">' + (_list1[h]?.course?.courseName || '') + '</td>\n' +
                  '        <td>' + (_list1[h]?.totalScore || (_list1[h]?.course?.courseName ? 0 : '')) + '</td>\n';
                if (index % 2 == 0) {
                  pageHtml += '</tr>';
                }
              }
            }
            if (index > 0 && index % 2 == 1) {
              pageHtml += '<td></td>\n' +
                '        <td></td>' +
                '        <td></td>\n' +
                '        <td></td>' +
                '</tr>';
            }
          }
          pageHtml += ' </table></div>';
          pageHtmls.push(pageHtml);
        }
        printPage({
          pages: pageHtmls,
          style: '<style>.ele-printer-table td{height: 32px;padding: 0 10px;}</style>',
          blank: false, // 是否打开新页面打印
          close: true, // 如果是打开新页面，打印完是否关闭
          iePreview: true, // 是否兼容 ie 打印预览
          print: true, // 如果是打开新窗口是否自动打印
          padding: undefined, // 页面间距
          isDebug: false, // 调试模式
          height: undefined, // 每一页高度
          width: undefined, // 每一页宽度
          loading: true, // 是否显示 loading
          before: () => { // 打印开始的回调
          },
          done: () => { // 打印完成的回调
          },
          margin: 0, // 打印机页间距
          header: '', // 页眉 html
          footer: '', // 页脚 html
          title: '' // 页面标题，浏览器打印设置显示页眉会显示页面标题，不要标题还应该设置 blank: true
        });

      }).catch((e) => {
        loading.close();
        this.$message.error(e.message);
      });
    },
    /* 批量下载（1登记表；2学籍表） */
    async batchDownload(type) {
      if (!this.selection.length) {
        this.$message.error('请至少选择一条数据');
        return;
      }
      const loading = this.$loading({lock: true});

      try {
        // 构造请求参数
        const userMajorIds = this.selection.map((d) => d.id);

        // 根据 type 选择下载方法
        const response = type === 1
          ? await downloadRegistration(userMajorIds)
          : await downloadEnrollment(userMajorIds);

        // 处理文件下载
        const blob = new Blob([response], {type: 'application/zip'});
        const url = window.URL.createObjectURL(blob);

        // 创建下载链接
        const link = document.createElement('a');
        link.href = url;

        // 根据 type 设置文件名
        const fileName = type === 1
          ? 'registration_list.zip'
          : 'enrollment_list.zip';
        link.setAttribute('download', fileName);

        // 触发下载
        document.body.appendChild(link);
        link.click();

        // 清理资源
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);

        this.$message.success('下载成功');
      } catch (e) {
        this.$message.error(e.message || '下载失败');
      } finally {
        loading.close();
      }
    },
    /* 批量删除 */
    removeBatch() {
      if (!this.selection.length) {
        this.$message.error('请至少选择一条数据');
        return;
      }
      this.$confirm('确定要删除选中的学生专业吗?', '提示', { type: 'warning',lockScroll:false }).then(() => {
        const loading = this.$loading({ lock: true });
        removeUserMajors(this.selection.map((d) => d.id))
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
    }
  }
};
</script>
