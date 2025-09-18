<!-- 试卷批改 -->
<template>
  <ele-modal :lock-scroll="false"
    width="1200px"
    :visible="visible"
    :centered="true"
     append-to-body
    :close-on-click-modal="true"
    custom-class="ele-dialog-form"
    title="试卷批改"
    @update:visible="updateVisible"
  >
    <div style="display: flex">
      <iframe style="width: 65%;height: 720px;border: 0px" :src="paperFile.url"></iframe>
      <div style="width: 34%;height: 720px;padding: 0 10px;overflow-y: scroll;overflow-x: hidden;padding-bottom: 45px;">
        <el-empty v-if="questions==null" description="暂无答题卡"></el-empty>
        <div v-for="(value,key) in questions" :key="key" >
          <h6 style="margin: 10px 0;">{{indexArr[value.index]}}、{{key}}<span style="font-size: 10px">{{" （共 "+value.questionNum+" 小题，总分： "+value.questionSumScore+"）"}}</span></h6>
          <el-row :gutter="15">
            <div v-if="value.questionType==1">
              <el-col  v-for="(item,index) in value.list" :key="index"  :span="8">
                <div class="course-list-item" style="display: flex;margin: 5px 0;"><span style="width: 40px;line-height: 28px;">{{index+1}}、</span>
                  <el-tooltip effect="light">
                    <div slot="content">
                      <span class="questionAnswerClass">分数：{{item.questionScore}}</span>
                      <br/>
                      <span class="questionAnswerClass">正确答案：{{item.questionAnswer}}</span>
                      <br v-if="item.questionAnalysis" />
                      <span class="questionAnswerClass" v-if="item.questionAnalysis">解析：{{item.questionAnalysis}}</span>
                    </div>
                    <el-input maxlength="1"  :style="(!answersMap[item.paperQuestionId] || answersMap[item.paperQuestionId]?.toUpperCase() != item.questionAnswer?.toUpperCase()) ?'outline: 1px solid red; border-radius: 5px;':''"
                              disabled v-model="answersMap[item.paperQuestionId]"></el-input>
                  </el-tooltip>
                </div>
              </el-col>
            </div>
            <div v-if="value.questionType==2">
              <el-col v-for="(item,index) in value.list" :key="index" :span="12">
                <div class="course-list-item" style="display: flex;margin: 5px 0;"><span style="width: 40px;line-height: 28px;">{{index+1}}、</span>
                  <el-tooltip effect="light">
                    <div slot="content">
                      <span class="questionAnswerClass">分数：{{item.questionScore}}</span>
                      <br/>
                      <span class="questionAnswerClass">正确答案：{{item.questionAnswer}}</span>
                      <br v-if="item.questionAnalysis" />
                      <span class="questionAnswerClass" v-if="item.questionAnalysis">解析：{{item.questionAnalysis}}</span>
                    </div>
                  <el-input maxlength="10"  :style="(!answersMap[item.paperQuestionId] || answersMap[item.paperQuestionId]?.toUpperCase() != item.questionAnswer?.toUpperCase()) ?'outline: 1px solid red; border-radius: 5px;':''"
                            disabled v-model="answersMap[item.paperQuestionId]"></el-input>
                  </el-tooltip>
                </div>
              </el-col>
            </div>
            <div v-if="value.questionType==3">
              <el-col v-for="(item,index) in value.list" :key="index" :span="8">
                <div class="course-list-item" style="display: flex;margin: 5px 0;"><span style="width: 40px;line-height: 15px;">{{index+1}}、</span>
                  <el-tooltip effect="light">
                    <div slot="content">
                      <span class="questionAnswerClass">分数：{{item.questionScore}}</span>
                      <br/>
                      <span class="questionAnswerClass">正确答案：{{item.questionAnswer}}</span>
                      <br v-if="item.questionAnalysis" />
                      <span class="questionAnswerClass" v-if="item.questionAnalysis">解析：{{item.questionAnalysis}}</span>
                    </div>
                    <el-radio-group style="width: 100%;" :style="(!answersMap[item.paperQuestionId] || answersMap[item.paperQuestionId] != item.questionAnswer) ?'outline: 1px solid red; border-radius: 5px;':''"
                                    disabled v-model="answersMap[item.paperQuestionId]" >
                      <el-radio style="margin-right:5px" label="对">对</el-radio>
                      <el-radio label="错">错</el-radio>
                    </el-radio-group>
                  </el-tooltip>
                </div>
              </el-col>
            </div>
            <div v-if="value.questionType==4">
              <el-col v-for="(item,index) in value.list" :key="index" :span="24">
                <div class="course-list-item" style="display: flex;margin: 5px 0;"><span style="width: 40px;line-height: 28px;">{{index+1}}、</span>
                  <el-tooltip effect="light">
                    <div slot="content">
                      <span class="questionAnswerClass">分数：{{item.questionScore}}</span>
                      <br/>
                      <span class="questionAnswerClass">正确答案：{{item.questionAnswer}}</span>
                      <br v-if="item.questionAnalysis" />
                      <span class="questionAnswerClass" v-if="item.questionAnalysis">解析：{{item.questionAnalysis}}</span>
                    </div>
                    <el-input maxlength="100" :style="(!answersMap[item.paperQuestionId] || answersMap[item.paperQuestionId] != item.questionAnswer) ?'outline: 1px solid red; border-radius: 5px;':''"
                              disabled v-model="answersMap[item.paperQuestionId]"></el-input>
                  </el-tooltip>
                </div>
              </el-col>
            </div>
            <div v-if="value.questionType==5">
              <el-col v-for="(item,index) in value.list" :key="index" :span="24">
                <div class="course-list-item" style="display: flex;margin: 5px 0;"><span style="width: 40px;">{{index+1}}、</span>
                  <el-tooltip effect="light" >
                    <div slot="content">
                      <span class="questionAnswerClass">分数：{{item.questionScore}}</span>
                      <br/>
                      <span class="questionAnswerClass">正确答案：{{item.questionAnswer}}</span>
                      <br v-if="item.questionAnalysis" />
                      <span class="questionAnswerClass" v-if="item.questionAnalysis">解析：{{item.questionAnalysis}}</span>
                    </div>

                    <!--
                    <el-input type="textarea" show-word-limit rows="4" disabled v-model="answersMap[item.paperQuestionId]"></el-input>
                    -->

                    <!-- 富文本编辑器 -->
                    <div style="height:auto; width:100%">
                      <tinymce-editor
                        ref="editor"
                        v-model="answersMap[item.paperQuestionId]"
                        :init="{
                          height: 200,
                          width: '100%',
                          plugins: 'preview',
                          toolbar: 'preview',
                          menubar: false, // 显示菜单栏
                          forced_root_block: false // 禁用默认的 <p> 标签
                       }"
                      />
                    </div>

                  </el-tooltip>
                  <div style="text-align: center">
                    <el-input class="correctInput" style="width: 60px" @input="forceUpdate(item)" v-model="scoreMap[item.paperQuestionId]"></el-input>
                    <el-button type="success" size="mini" style="width: 60px;margin-top: 2px" icon="el-icon-check" @click="buttonForceUpdate(item,1)"> </el-button><br/>
                    <el-button type="primary" size="mini" style="width: 60px;margin-top: 2px" @click="buttonForceUpdate(item,2/3)">2/3</el-button><br/>
                    <el-button type="primary" size="mini"  style="width: 60px;margin-top: 2px" @click="buttonForceUpdate(item,1/2)">1/2</el-button><br/>
                    <el-button type="primary" size="mini"  style="width: 60px;margin-top: 2px" @click="buttonForceUpdate(item,1/3)">1/3</el-button><br/>
                    <el-button type="danger" size="mini" style="width: 60px;margin-top: 2px" icon="el-icon-close" @click="buttonForceUpdate(item,0)"></el-button>
                  </div>
                </div>

              </el-col>
            </div>
          </el-row>
        </div>
        <div style="width: 400px;position: absolute;bottom: 5px;display: flex">
          <el-button style="width: 50%" v-if="userMajorCourseExam?.back" type="danger" @click="submitBack()">退回</el-button>
          <el-button :style="userMajorCourseExam?.back?'width: 50%':'width: 100%;z-index:1111111'" v-if="questions!=null" type="primary" @click="submitCorrect()">批改</el-button>
        </div>
      </div>
    </div>

  </ele-modal>
</template>

<script>
  import {getPaper,correctPaper,backPaper} from "@/api/exam/correct";
  import TinymceEditor from '@/components/TinymceEditor/index.vue';

  export default {
    components: {TinymceEditor},
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 用户考试记录ID
      userMajorCourseExamId: Number
    },
    data() {
      return {
        indexArr:{1:"一",2:"二",3:"三",4:"四",5:"五",6:"六",7:"七",8:"八",9:"九",10:"十"},
        groupIndex:0,
        paperFile:'',
        questions:null,
        userMajorCourseExam:null,
        answersMap:{},
        answerScoreMap:{},
        scoreMap:{}
      }
    },
    created() {
      if(this.userMajorCourseExamId){
        getPaper(this.userMajorCourseExamId)
          .then((data) => {
            this.paperFile = JSON.parse(data.paperFile)[0];
            var paperQuestionList = data.paperQuestionList;

            this.userMajorCourseExam = data.userMajorCourseExam;
            if(!this.userMajorCourseExam.itemList){
              this.userMajorCourseExam.itemList = [];
            }
            for(var n=0;n<this.userMajorCourseExam.itemList.length;n++){
              var _item = this.userMajorCourseExam.itemList[n];
              this.answersMap[_item.paperQuestionId] = _item.answer;
              this.answerScoreMap[_item.paperQuestionId] =_item.score;
            }

            if(paperQuestionList && paperQuestionList.length>0){
              var _index=1;
              this.questions = {};
              paperQuestionList.sort(function (a,b){
                return a.questionSort - b.questionSort;
              });
              for(var i=0;i<paperQuestionList.length;i++){
                var item = paperQuestionList[i];
                if(item.questionType == 5){
                  if(this.answerScoreMap[item.paperQuestionId] || this.answerScoreMap[item.paperQuestionId]===0){
                    this.scoreMap[item.paperQuestionId]=this.answerScoreMap[item.paperQuestionId];
                  }
                }
                var questionGroup = this.questions[item.questionGroup];

                if(questionGroup){
                  questionGroup.questionNum += 1;
                  questionGroup.questionSumScore +=  item.questionScore;
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
          this.$message.error(e.message);
        });
      }
    },
    computed: {
      // 是否开启响应式布局
      styleResponsive() {
        return this.$store.state.theme.styleResponsive;
      },
    },
    methods: {
      buttonForceUpdate(row,coefficient){
        this.scoreMap[row.paperQuestionId] = Math.round(row.questionScore*coefficient);
        this.$forceUpdate();
      },
      forceUpdate(row){
        if(this.scoreMap[row.paperQuestionId]){
          if(isNaN(this.scoreMap[row.paperQuestionId]) || this.scoreMap[row.paperQuestionId]<0 || !(/^[1-9]\d*$/.test(this.scoreMap[row.paperQuestionId]))){
            this.scoreMap[row.paperQuestionId] = 0
          } else if(this.scoreMap[row.paperQuestionId]>row.questionScore){
            this.scoreMap[row.paperQuestionId] = row.questionScore;
          }
        }
        this.$forceUpdate();
      },
      /* 更新visible */
      updateVisible(value) {
        this.$emit('update:visible', value);
      },
      submitBack(){
        this.$confirm('确定要退回作答吗?', '提示', { type: 'warning',lockScroll:false }).then(() => {
          const loading = this.$loading({ lock: true });
          backPaper(this.userMajorCourseExamId)
            .then(() => {
              loading.close();
              this.updateVisible(false);
              this.$emit('done');
              this.$message.success("退回成功");
            }).catch((e) => {
              loading.close();
              this.$message.error(e.message);
            });
        }).catch(() => {});
      },
      submitCorrect(){
          const loading = this.$loading({ lock: true });
          this.userMajorCourseExam.itemList =[];
          for (var key in this.scoreMap) {
            var _item = {};
            _item.paperQuestionId = key;
            _item.score =  this.scoreMap[key];
            this.userMajorCourseExam.itemList.push(_item);
          };
          correctPaper(this.userMajorCourseExam)
            .then(() => {
              loading.close();
              this.updateVisible(false);
              this.$emit('done');
              this.$message.success("提交成功");
            }).catch((e) => {
            loading.close();
            this.$message.error(e.message);
          });
      }
    },
  };
</script>
<style lang="scss" scoped >
.correctInput{
  width: 60px;
}
:deep(.el-input--medium .el-input__inner){
  height: 28px;
  line-height: 28px;
}
.correctInput :deep(.el-input__inner){
  padding: 0 5px;
}
.correctInput > :deep(.el-input--medium .el-input__inner){
  border-left: 0px; border-right: 0px; border-top: 0px;
}
.questionAnswerClass{
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  word-break: break-all;
  width: 300px;
}
.editor-div:hover{
  position: absolute;
  width: 600px;
  height: 500px;
}
</style>
