<!-- 考试 -->
<template>
  <div class="ele-body">
    <el-empty v-if="examStatus" :description="examStatusMag"></el-empty>
    <ele-split-layout v-else class="ele-bg-white eleSplitLayout"
                      width="316px"
                      :reverse="true"
                      allow-collapse
                      :leftStyle="{'border-right':'1px solid #ededed'}"
                      :right-style="{ overflow: 'hidden','padding': '10px 0px','word-wrap': 'break-word'}"
                      :collapseBtnStyle="{width:'20px'}"
    >
      <div class="questions_div">
        <el-empty v-if="questions==null" description="暂无答题卡"></el-empty>
        <div v-for="(value,key) in questions" :key="key" >
          <h6 style="margin: 10px 0;">{{indexArr[value.index]}}、{{key}}<span style="font-size: 10px">{{" （共 "+value.questionNum+" 小题，总分："+value.questionSumScore+"）"}}</span></h6>
          <el-row :gutter="15">
            <div v-if="value.questionType==1">
              <el-col  v-for="(item,index) in value.list" :key="index"  :span="8">
                <div class="course-list-item" style="display: flex;margin: 5px 0;"><span style="width: 40px;line-height: 28px;">{{index+1}}、</span>
                  <el-input maxlength="1" @input="forceUpdate" v-model="answersMap[item.paperQuestionId]"></el-input></div>
              </el-col>
            </div>
            <div v-if="value.questionType==2">
              <el-col v-for="(item,index) in value.list" :key="index" :span="12">
                <div class="course-list-item" style="display: flex;margin: 5px 0;"><span style="width: 40px;line-height: 28px;">{{index+1}}、</span>
                  <el-input maxlength="10" @input="forceUpdate" v-model="answersMap[item.paperQuestionId]"></el-input></div>
              </el-col>
            </div>
            <div v-if="value.questionType==3">
              <el-col v-for="(item,index) in value.list" :key="index" :span="12">
                <div class="course-list-item" style="display: flex;margin: 5px 0;"><span style="width: 40px;line-height: 15px;">{{index+1}}、</span>
                  <el-radio-group style="width: 100%;" @input="forceUpdate" v-model="answersMap[item.paperQuestionId]" >
                    <el-radio style="margin-right:5px" label="对">对</el-radio>
                    <el-radio label="错">错</el-radio>
                  </el-radio-group>
                </div>
              </el-col>
            </div>
            <div v-if="value.questionType==4">
              <el-col v-for="(item,index) in value.list" :key="index" :span="24">
                <div class="course-list-item" style="display: flex;margin: 5px 0;"><span style="width: 40px;line-height: 28px;">{{index+1}}、</span>
                  <el-input maxlength="100" @input="forceUpdate" v-model="answersMap[item.paperQuestionId]"></el-input>
                </div>
              </el-col>
            </div>
            <div v-if="value.questionType==5">
              <el-col v-for="(item,index) in value.list" :key="index" :span="24">
                <div class="course-list-item" style="display: flex;margin: 5px 0;"><span style="width: 40px;">{{index+1}}、</span>

                  <!--
                  <el-input type="textarea" show-word-limit rows="4" @input="forceUpdate" v-model="answersMap[item.paperQuestionId]"></el-input>
                  -->

                  <!-- 富文本编辑器 -->
                  <tinymce-editor
                    ref="editor"
                    v-model="answersMap[item.paperQuestionId]"
                    :init="{
                      height: 200,
                      width: '100%',
                      menubar: false,
                      plugins: 'lists image preview fullscreen ',  // 添加 fullscreen 插件
                      toolbar: 'numlist | image | preview | fullscreen',  // 添加 fullscreen 按钮
                      forced_root_block: false, // 禁用默认的 <p> 标签
                    }"
                  />

                </div>
              </el-col>
            </div>

          </el-row>
        </div>
        <div>
          <el-button style="width: 305px; position: fixed;bottom: 15px;" v-if="questions!=null" type="primary" @click="submit(1)">提交</el-button>
        </div>
      </div>
      <template v-slot:content>
<!--        <iframe style="width: 100%; height: 100%;border: 0px" v-if="paperFile.url" :src="'http://pdf.jxuxue.com/web/viewer.html?file='+paperFile.url"></iframe>-->
        <pdf-preview :url="paperFile.url"></pdf-preview>
      </template>
    </ele-split-layout>
  </div>
</template>

<script>
import {getPaper,submitPaper} from "@/api/index";
import PdfPreview from "@/views/front/components/pdf-preview";
import TinymceEditor from '@/components/TinymceEditor/index.vue';

export default {
  components: {TinymceEditor, PdfPreview},
  data() {
    return {
      indexArr:{1:"一",2:"二",3:"三",4:"四",5:"五",6:"六",7:"七",8:"八",9:"九",10:"十"},
      groupIndex:0,
      paperFile:'',
      questions:null,
      userMajorCourseExam:null,
      answersMap:{},
      //专业ID
      majorId:'',
      //课程ID
      courseId:'',
      //试卷提交状态
      submitStatus:false,

      examStatus:false,

      examStatusMag:'',

      //定时保存用户作答
      myExamTarget:null
    }
  },

  beforeDestroy() {
    //销毁监听事件
    window.removeEventListener("beforeunload",this.submit);
    this.submit();
  },
  created() {
    //监听关闭页面和刷新事件
    window.addEventListener( 'beforeunload', this.submit);

    this.majorId = this.$route.params.majorId;
    this.courseId = this.$route.params.courseId;
    if(!this.majorId || !this.courseId){
      this.$router.push({path:'/index'})
      return ;
    }
    getPaper(this.majorId,this.courseId).then((data) => {
        this.paperFile = JSON.parse(data.paper.paperFile)[0];
        var paperQuestionList = data.paper.paperQuestionList;
        this.userMajorCourseExam = data.paper.userMajorCourseExam;
        if(!this.userMajorCourseExam.itemList){
          this.userMajorCourseExam.itemList = [];
        }
        for(var n=0;n<this.userMajorCourseExam.itemList.length;n++){
          var _item = this.userMajorCourseExam.itemList[n];
          this.answersMap[_item.paperQuestionId] = _item.answer;
        }
        if(paperQuestionList && paperQuestionList.length>0){
          var _index=1;
          this.questions = {};
          paperQuestionList.sort(function (a,b){
            return a.questionSort - b.questionSort;
          });
          for(var i=0;i<paperQuestionList.length;i++){
            var item = paperQuestionList[i];
            var questionGroup = this.questions[item.questionGroup];
            if(questionGroup){
              questionGroup.questionNum += 1;
              questionGroup.questionSumScore += item.questionScore;
              questionGroup.list.push(item);
            }else{
              questionGroup={};
              questionGroup.index=_index++;
              questionGroup.questionType = item.questionType;
              questionGroup.questionNum = 1;
              questionGroup.questionSumScore = item.questionScore;
              questionGroup.list = [];
              questionGroup.list.push(item);
            }
            this.questions[item.questionGroup] = questionGroup;
          }
        }

      }).catch((e) => {
      this.examStatus = true;
      this.examStatusMag = e.message;
    });
  },
  methods: {
    forceUpdate(){
      if(this.myExamTarget){
        clearTimeout(this.myExamTarget);
      }
      //答题5分钟，没有操作进行提交保存
      this.myExamTarget = setTimeout(()=>{
        this.submit();
      }, 1000*60*5);

      this.$forceUpdate();
    },
    save(type){
      var loading = null;
      if(type == 1){
         loading = this.$loading({ lock: true });
       }
       submitPaper(this.userMajorCourseExam)
         .then(() => {
           if(type == 1){
             window.location.reload();
             this.$message.success("提交成功");
           }else{
             this.submitStatus=false;
           }
         }).catch((e) => {
           if(type == 1){
             loading.close();
           }
           this.submitStatus=false;
           this.$message.error(e.message);
         });
    },
    submit(type) {
      if(this.myExamTarget){
        clearTimeout(this.myExamTarget);
      }
      if(!this.submitStatus){
        this.submitStatus=true;
        var num = 0;
        var hasSubmit = false;//是否有作答过
        var itemList = [];
        for (var key in this.questions) {
          var itemArr = this.questions[key];
          for(var i=0;i<itemArr.list.length;i++){
            var item = itemArr.list[i];
            var answer = this.answersMap[item.paperQuestionId];
            if(!answer){
              num++;
            }else{
              hasSubmit = true;
            }
            var _item = {};
            _item.paperQuestionId = item.paperQuestionId;
            _item.answer = answer;
            itemList.push(_item);
          }
        }
        if(this.userMajorCourseExam){
          this.userMajorCourseExam.itemList = itemList;
        }else{
          this.submitStatus = false;
          return false;
        }

        if(type == 1){
          if(!hasSubmit){
            this.$message.error("没有作答，不能提交");
            this.submitStatus = false;
            return false;
          }

          this.userMajorCourseExam.status=1
          var msg = "提交后不能修改了，确认提交？";
          if(num>0){
            msg = "还有 "+num+" 题未作答，提交后不能修改，确认提交？"
          }
          this.$confirm(msg, '提示', { type: 'warning',lockScroll:false }).then(() => {
            this.save(type);
          }).catch(() => {
            this.submitStatus = false;
          });
        }else{
          this.userMajorCourseExam.status=0
          if(hasSubmit){
            this.save(type);
          }
        }
      }
    }
  },
};
</script>
<style lang="scss" scoped >
.eleSplitLayout{
  height: calc(100vh - 86px);
}
:deep(.el-input--medium .el-input__inner){
  height: 28px;
  line-height: 28px;
}
.questions_div{
  height: calc(100% - 45px);
  padding: 0 10px;
  overflow-y: scroll;
  overflow-x: hidden;
  border-left: 1px solid var(--border-color-base);
}
/* 响应式 */
@media screen and (max-width: 768px ) {
  .eleSplitLayout{
    height: calc(100vh - 56px);
  }
  .questions_div{
    height: calc(100% - 60px);
  }
}
</style>
