<template>
  <div :class="['ele-body' ,{ 'list-article-responsive': styleResponsive }]">
    <el-card shadow="never" class="major-card">
      <div v-if="majors.length>0">
        <el-menu :default-active="activeMajorIndex" v-if="majors.length>1" class="major-switch" mode="horizontal" @select="handleMajorSelect" >
          <el-menu-item v-for="(item,index) in majors" :key="index" :index="index+''"> {{ item.frontMajor.majorName}}</el-menu-item>
        </el-menu>

        <div class="article-list-item">
          <el-row :gutter="15">
            <el-col  :key="activeMajorIndex" v-bind="styleResponsive ? { xl:6,lg:7,md:9,sm:11,xs: 24 } : { span: 6 }">
                <div class="article-list-right">
                  <el-card shadow="never" body-style="padding:15px;">
                  <el-image :src="major?.frontMajor?.majorPicture?major.frontMajor.majorPicture:API_BASE_URL+'/system/sysStaticPicture/major'"/>
                  <div class="professional_name">
                    <h3>{{ major?.frontMajor?.majorName||'' }}</h3>
                    <el-tag size="mini">{{ major?.frontMajor?.majorYear||'' }}级</el-tag>
                  </div>
                  <!-- <div class="article-list-tags" style="padding: 10px" v-if="major">
                    <el-tag type="success">{{ major?.frontMajor?.majorType||'' }}</el-tag>
                    <el-tag type="danger">{{ major?.frontMajor?.majorGradation||'' }}</el-tag>
                    <el-tag type="info">{{ major?.frontMajor?.majorForms||'' }}</el-tag>
                    <el-tag type="warning">{{ major?.frontMajor?.majorLength||'' }}</el-tag>
                  </div> -->
                  <el-divider />
                  <div class="major-tag"  v-if="major">
                        <div>教育类别：<el-tag type="info" effect="plain">{{ major?.frontMajor?.majorType||'' }}</el-tag></div>
                        <div>层次：<el-tag type="info" effect="plain">{{ major?.frontMajor?.majorGradation||'' }}</el-tag></div>
                        <div>学习形式：<el-tag type="info" effect="plain">{{ major?.frontMajor?.majorForms||'' }}</el-tag></div>
                        <div>学制：<el-tag type="info" effect="plain">{{ major?.frontMajor?.majorLength||'' }}年制</el-tag></div>
                  </div>
                  </el-card>

                  <el-card shadow="never" class="major-introduction" body-style="padding:15px;">
                    <div slot="header" class="clearfix">
                      <h5>个人资料下载/上传</h5>
                    </div>

                    <div class="download-link-container">
                      <div class="links-container">
                        <el-link @click="doDownload(1)"
                                 type="primary"
                                 :underline="false"
                                 style="vertical-align: baseline; margin-right: 10px;"
                        >
                          下载登记表
                        </el-link>
                        <el-link @click="doDownload(2)"
                                 type="primary"
                                 :underline="false"
                                 style="vertical-align: baseline;"
                        >
                          下载学籍表
                        </el-link>
                      </div>
                      <div class="hint-container">
                        <p><i class="el-icon-warning" style="color: #e6a23c; margin-right: 5px;"></i>下载表格之前，先到<a href="/user/profile" target="_blank">个人中心</a>完善基本信息</p>
                      </div>
                    </div>

                    <div class="download-link-container"  style="margin-top: 15px">
                      <div class="links-container">
                        <el-link @click="printScore()"
                                 type="primary"
                                 :underline="false"
                                 style="vertical-align: baseline;"
                        >
                          打印个人成绩单
                        </el-link>
                      </div>
                      <div class="hint-container">
                        <p><i class="el-icon-warning" style="color: #e6a23c; margin-right: 5px;"></i>请另存为PDF，必须在一页显示(否则联系管理员)</p>
                      </div>
                    </div>

                    <div  class="download-link-container" style="margin-top: 15px">
                      <el-form
                        ref="form"
                        label-width="86px"
                        class="noBorderInput"
                        style="margin-top: 10px; margin-left: -11px"
                      >
                        <el-form-item label="论文标题:" prop="thesisName">
                          <el-input :disabled="major.thesisScore !=null"
                                    v-model.trim="major.thesisName"
                                    placeholder="请输入论文标题"
                                    clearable
                          />
                        </el-form-item>
                        <el-form-item label="论文附件:" prop="thesisFileArr" v-if="!(major.thesisScore !=null && major.thesisFileArr.length==0)">
                          <com-upload  :disabled="major.thesisScore !=null" v-model="major.thesisFileArr" :limit="3"/>
                        </el-form-item>
                        <el-form-item label="论文成绩:" v-if="major.thesisScore !=null"  prop="thesisScore">
                          <el-input disabled v-model.trim="major.thesisScore"/>
                        </el-form-item>
                        <div style="text-align: center" v-if="major.thesisScore ==null">
                          <el-button size="mini" type="primary" :loading="loadingSave" @click="save" >保存</el-button>
                        </div>
                      </el-form>
                    </div>
                  </el-card>

                  <el-card shadow="never" class="major-introduction" body-style="padding:15px;" v-if="major?.frontMajor?.comments && major?.frontMajor?.comments.length >= 30">
                    <div slot="header" class="clearfix">
                      <h5>专业简介</h5>
                    </div>
                    <div class="introduction" v-if="major?.frontMajor?.comments">
                      {{major?.frontMajor?.comments||''}}
                    </div>
                    <el-empty description="暂无简介" v-else></el-empty>
                  </el-card>
                </div>
            </el-col>
            <el-col class="course-list" v-bind="styleResponsive ? { xl:18,lg:17,md:15,sm:13,xs: 24 } : { span: 6 }">
              <div class="article-list-left" style="position: relative;">
                <el-card shadow="never" body-style="padding:0 15px 0;">
                <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect" >
                  <el-menu-item v-for="(item) in semester" :key="item.value" :index="item.value">{{item.label}}</el-menu-item>
                </el-menu>
                <course-list :courses="courseMap[activeIndex]" :score-rule="major?.scoreRule"></course-list>
                </el-card>
              </div>
            </el-col>
        </el-row>
        </div>
      </div>
      <div style="min-height: 80vh;text-align: center;margin-top: 100px" v-else-if="loadingState == false">
       <el-empty description="暂无专业" ></el-empty>
      </div>
    </el-card>
  </div>
</template>

<script>

import CourseList from './components/course-list.vue';
import ComUpload from '@/views/system/file/components/com-upload.vue';
import {getMajor, downloadTemplate1, downloadTemplate2, saveThesis, listUserCourses} from "@/api/index";
import { printPage } from 'ele-admin';
import {API_BASE_URL} from "@/config/setting";
import {download} from "@/api/common";

export default {
  components: { CourseList ,ComUpload},
  name: 'ListCardArticle',
  data() {
    return {
      numArr:{'1':"一",'2':"二",'3':"三",'4':"四",'5':"五",'6':"六",'7':"七",'8':"八",'9':"九",'10':"十"},
      API_BASE_URL:API_BASE_URL,
      loadingState:true,
      loadingSave:false,
      majors:[],
      courseMap:{},
      semester:[],
      activeMajorIndex:"",
      activeIndex: undefined,
      semesterMap:{1:"一",2:"二",3:"三",4:"四",5:"五",6:"六",7:"七",8:"八",9:"九",10:"十"}
    };
  },
  computed: {
    // 是否开启响应式布局
    styleResponsive() {
      return this.$store.state.theme.styleResponsive;
    },
    major(){
      var m = this.majors[this.activeMajorIndex];
      if(m.thesisFile){
        m.thesisFileArr=JSON.parse(m.thesisFile);
      }else{
        m.thesisFileArr=[];
      }
      return m;
    }
  },
  created() {
 //  this.$store.dispatch('theme/setBodyFull', true);
  },
  mounted() {
    getMajor().then((data) => {
       if(data && data.length>0){
         this.activeMajorIndex = (data.length-1) +"";
         this.majors = data;
         this.getSemester();
       }
      this.loadingState = false;
    }).catch((e) => {
      this.loadingState = false;
      this.$message.error(e.message);
    });
  },
  methods: {
    //下载两个表格
    doDownload(type){
      this.loading = true;
      var majorvo =  this.majors[this.activeMajorIndex];
      if(type == 1){
        downloadTemplate1(majorvo.majorId).then((data) => {
          this.loading = false;
          download(data,"登记表.doc");
        }).catch((e) => {
          this.loading = false;
          this.$message.error(e.message);
        });
      }else{
        downloadTemplate2(majorvo.majorId).then((data) => {
          this.loading = false;
          download(data,"学籍表.doc");
        }).catch((e) => {
          this.loading = false;
          this.$message.error(e.message);
        });
      }
    },
    // 批量打印成绩单（type1:changda；type2:others）
    printScore() {
      const loading = this.$loading({lock: true});

      // 组装参数
      var p = {};
      p.majorId = this.majors[this.activeMajorIndex].majorId
      listUserCourses(p).then((response) => {
        // 接收返回值
        const user = response.user;
        const userMajor = response.userMajor;
        const data = response.userCourseList;

        // 根据租户id不同，区分type
        let tenantId = userMajor?.tenantId;

        loading.close();
        if (!data) {
          this.$message.error('没有数据');
          return;
        }
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
        var pageHtml = '<div><table style="width: 85%;margin: 0 auto;font-size: 12px; ">' +
          '      <tr>' +
          '        <th colspan="4" style="padding: 10px 45px 10px 0; font-size: 28px;font-weight: 400;">' + (this.tenant?.tenantName || '') + '成绩单</th>' +
          '      </tr>' +
          '      <tr style="width: 100%">' +
          '        <td style="width: 25%">学号：' + (userMajor.userNumber || '') + '</td>' +
          '        <td style="width: 25%">姓名：' + (user?.realname || '') + '</td>' +
          '        <td style="width: 28%">身份证：' + (user?.idCard || '') + '</td>' +
          '        <td style="width: 22%">年级：' + (userMajor?.major.majorYear || '') + '级</td>' +
          '      </tr>' +
          '      <tr style="width: 100%">' +
          '        <td style="width: 25%">层次：' + (userMajor?.major.majorGradation || '') + '</td>' +
          '        <td style="width: 25%">专业：' + (userMajor?.major.majorName || '') + '</td>' +
          '        <td style="width: 28%">学制：' + (userMajor?.major.majorLength || '') + '年</td>' +
          '        <td style="width: 22%">函授站：' + (userMajor?.org.orgName || '') + '</td>' +
          '      </tr>' +
          '    </table>' +
          '    <table class="ele-printer-table" style="width:85%;margin: 0 auto;margin-top: 10px;text-align: center;">';

        if (tenantId === 7) {
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
        var _map = courseScoreMap[user.userId + "_" + userMajor.majorId];

        if (tenantId === 7) {
          var len = Math.floor(userMajor?.major.majorLength || 0) + 1;
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

          if (userMajor?.major.majorLength == '2.5') {//changda
            pageHtml += ' <tr  style="width: 100%;font-weight: 400;font-size: 12px;">\n' +
              '             <td>三<br>学<br>年</td>\n' +
              '             <td>五</td>\n' +
              '             <td colspan="5" style="height: 50px;">毕业论文</td>\n' +
              '             <td colspan="2">' + (userMajor.thesisScore || '') + '</td>\n' +
              '          </tr>';
          } else {
            pageHtml += ' <tr  style="width: 100%;font-weight: 400;font-size: 12px;">\n' +
              '             <td colspan="2">毕业论文</td>\n' +
              '             <td colspan="5" style="height: 50px;">' + (userMajor.thesisName || '') + '</td>\n' +  // userMajor.thesisName
              '             <td colspan="2">' + (userMajor.thesisScore || '') + '</td>\n' +
              '          </tr>';
          }

          // 计算毕业日期
          const majorYear = parseInt(userMajor?.major.majorYear || 0, 10);
          const majorLength = parseFloat(userMajor?.major.majorLength || 0);

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
          let scr = ''; // 审查人信息
          if (tenantId === 7) {
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

          const majorLength = parseFloat(userMajor?.major.majorLength || 0);

          var _len = (majorLength || 0) * 2 + 1;
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
    //切换学期
    handleSelect(key) {
      this.activeIndex = key;
    },
    handleMajorSelect(key){
     this.activeMajorIndex=key;
     this.getSemester();
    },
    getSemester(){
      this.semester =[];
      this.courseMap={};
      var majorvo =  this.majors[this.activeMajorIndex];
      var frontMajor =  majorvo.frontMajor;
      var frongCourseList = majorvo.frongCourseList;
      if(frongCourseList){
        for(var n = 0;n<frongCourseList.length;n++){
          var item = frongCourseList[n];
          var list = this.courseMap[item.semester];
          if(list){
            list.push(item)
          }else{
            list = [];
            list.push(item)
          }
          this.courseMap[item.semester] = list;
        }
      }
      var majorLength = frontMajor.majorLength*2;
      var active = '1';
      for(var i=1;i<=majorLength;i++){
        var xq ={};
        xq.label = "第"+this.semesterMap[i]+"学期";
        xq.value = i+'';
        if(this.courseMap[i]){
          active = i+'';
        }
        this.semester.push(xq);
      }
      this.activeIndex = active;
    },
    save(){
      this.loadingSave=true;
      var majorvo =  this.majors[this.activeMajorIndex];

      var data = {
        majorId:majorvo.majorId,
        thesisName: majorvo.thesisName
      };

      if(majorvo.thesisFileArr && majorvo.thesisFileArr.length>0){
        var list = [];
        for (var n = majorvo.thesisFileArr.length - 1; n >= 0; n--) {
          if(majorvo.thesisFileArr[n].status =='success') {
            var url =majorvo.thesisFileArr[n].response?.data?.path || majorvo.thesisFileArr[n].url;
            list.push({name:majorvo.thesisFileArr[n].name,url: url,status:'success'});
          }
        }
        data.thesisFile =JSON.stringify(list);
      }else{
        data.thesisFile='';
      }
      saveThesis(data).then((m) => {
        this.loadingSave=false;
        this.$message.success(m);
      }).catch((e) => {
        this.$message.error(e.message);
      });
    }
  }
};
</script>

<style lang="scss" scoped>

.download-link-container {
  border: 1px solid #dcdcdc; /* 细线颜色 */
  padding: 10px; /* 内边距 */
  border-radius: 4px; /* 圆角 */
  background-color:#f0f9eb;
}

.links-container {
  display: flex;
  align-items: center;
  justify-content: flex-start; /* 靠左对齐 */
}

.hint-container {
  margin-top: 10px; /* 提示信息与链接之间的间距 */
}

.download-hint {
  color: #606266;
  font-size: 14px;
  p {
    margin: 0;
    white-space: pre-wrap; /* 保留空白符并自动换行 */
  }
  a {
    color: #409EFF;
    text-decoration: none;
    &:hover {
      text-decoration: underline;
    }
  }
}

//.course-list{
//  min-height: calc(100vh - 86px);
//  background-color: #fff;
//}
.introduction{
  line-height: 2;
  text-indent: 2rem;
  min-height: 200px;
}
.major-card{
   overflow: visible;background-color:transparent;
  .major-switch{
  margin-bottom: 15px;
  }
  :deep(.el-card__body){
    padding: 0;
  }
}

.article-list-right{
  // border-right: 3px solid #ededed;
  // padding-right: 17px;
  min-height: 80vh;
  .professional_name{
    padding: 10px 0;
    display: flex;
    align-items: center;
    span{
      margin-left: 10px;
    }
  }
  .major-tag{
    display: flex;
    flex-wrap: wrap;
    padding: 10px 0;
    div{
      width: 50%;
      padding: 5px 0;
    }
  }
}
.article-list-tags{
  display: none;
}
  .el-menu--horizontal > .el-menu-item{
    height: 50px;
    line-height: 50px;
  }
  .el-image{
     max-height: 300px;
     border-radius: 6px
  }
  .top-search-group {
    max-width: 500px;
    margin: 0 auto;
    display: flex;

    :deep(.el-input input) {
      border-top-right-radius: 0;
      border-bottom-right-radius: 0;
      border-right-width: 0;
    }

    .el-button {
      border-top-left-radius: 0;
      border-bottom-left-radius: 0;
    }
  }

  /* 文章列表 */
  .article-list-wrapper {
    display: flex;
    padding-bottom: 15px ;
  }

  .article-list-left {
    flex: 1;
  }


  .article-list-content {
    margin-top: 15px;
    max-width: 700px;
  }

  .article-list-user-group {
    margin-top: 15px;

    & > span {
      vertical-align: middle;
    }
  }

  .article-list-footer {
    margin-top: 15px;

    .article-list-tool {
      display: inline-block;
      line-height: 22px;
      cursor: pointer;
    }

    .el-divider--vertical {
      margin: 0 16px;
    }
  }

  .el-menu-demo{
    displey:-webkit-flex; display: flex; -webkit-flex-flow:row nowrap;
    flex-flow:row nowrap; overflow-x: auto; list-style: none;
  }
  /* 隐藏滚动条 */
  .el-menu-demo::-webkit-scrollbar {
    display: none;
  }

  .el-menu-demo li {
    display: inline-block;
  }

  /* 响应式 */
  @media screen and (max-width: 768px ) {
    .article-list-left{
      top: 0px!important;
    }
    .major-introduction{
      display: none;
    }
    .article-list-right{
      min-height: auto;
      border-right: 0px;
    }
    .article-list-hide{
      display: none;
    }
    .article-list-tags{
      display: block;
    }

    .el-image{
      width: 100%;
      max-height: 300px
    }
    .article-list-tags>span{
      height: 20px;
      padding: 0 5px;
      line-height: 19px;
    }


    .article-list-left {
      margin-left: 0px;
    }

    .list-article-responsive {
      .article-list-wrapper {
        display: block;
      }

      .article-list-right {
        margin-top: 10px;
        padding-right: 0px;
        :deep(.el-image) {
          max-width: 100%;
        }
      }
    }
  }
</style>
