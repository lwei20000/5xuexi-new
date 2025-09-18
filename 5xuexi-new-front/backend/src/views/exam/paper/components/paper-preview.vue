<!-- 试卷试卷 -->
<template>
  <ele-modal :lock-scroll="false"
    width="1200px"
    :visible="visible"
    :centered="true"
     append-to-body
    :close-on-click-modal="true"
    custom-class="ele-dialog-form"
    title="预览试卷"
    @update:visible="updateVisible"
  >
    <div style="display: flex">
      <iframe style="width: 65%;height: 760px;border: 0px" :src="paperFile.url"></iframe>
      <div style="width: 34%;height: 760px;padding: 0 10px;overflow-y: scroll;overflow-x: hidden;">
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
                      <br v-if="item.questionAnalysis" />
                      <span class="questionAnswerClass" v-if="item.questionAnalysis">解析：{{item.questionAnalysis}}</span>
                    </div>
                  <el-input maxlength="1" disabled v-model="item.questionAnswer"></el-input>
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
                    <br v-if="item.questionAnalysis" />
                    <span class="questionAnswerClass" v-if="item.questionAnalysis">解析：{{item.questionAnalysis}}</span>
                  </div>
                    <el-input maxlength="10" disabled v-model="item.questionAnswer"></el-input>
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
                      <br v-if="item.questionAnalysis" />
                      <span class="questionAnswerClass" v-if="item.questionAnalysis">解析：{{item.questionAnalysis}}</span>
                    </div>
                      <el-radio-group style="width: 100%;" disabled v-model="item.questionAnswer" >
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
                      <br v-if="item.questionAnalysis" />
                      <span class="questionAnswerClass" v-if="item.questionAnalysis">解析：{{item.questionAnalysis}}</span>
                    </div>
                      <el-input maxlength="100" disabled v-model="item.questionAnswer"></el-input>
                  </el-tooltip>
                </div>
              </el-col>
            </div>
            <div v-if="value.questionType==5">
              <el-col v-for="(item,index) in value.list" :key="index" :span="24">
                <div class="course-list-item" style="display: flex;margin: 5px 0;"><span style="width: 40px;">{{index+1}}、</span>
                  <el-tooltip effect="light">
                    <div slot="content">
                      <span class="questionAnswerClass">分数：{{item.questionScore}}</span>
                      <br v-if="item.questionAnalysis" />
                      <span class="questionAnswerClass" v-if="item.questionAnalysis">解析：{{item.questionAnalysis}}</span>
                    </div>
                    <el-input type="textarea" show-word-limit rows="4" disabled v-model="item.questionAnswer"></el-input>
                  </el-tooltip>
                </div>

              </el-col>
            </div>
          </el-row>
        </div>
      </div>
    </div>

  </ele-modal>
</template>

<script>
  import {getPaper} from "@/api/exam/paper";

  export default {
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 试卷ID
      paperId: Number
    },
    data() {
      return {
        indexArr:{1:"一",2:"二",3:"三",4:"四",5:"五",6:"六",7:"七",8:"八",9:"九",10:"十"},
        groupIndex:0,
        paperFile:'',
        questions:null
      }
    },
    created() {
      if(this.paperId){
        getPaper(this.paperId)
          .then((data) => {
            this.paperFile = JSON.parse(data.paperFile)[0];
            if(data.paperQuestionList && data.paperQuestionList.length>0){
              var _index=1;
              this.questions = {};
              data.paperQuestionList.sort(function (a,b){
                return a.questionSort - b.questionSort;
              });
              for(var i=0;i<data.paperQuestionList.length;i++){
                var item = data.paperQuestionList[i];
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
      /* 更新visible */
      updateVisible(value) {
        this.$emit('update:visible', value);
      },
    },
  };
</script>
<style lang="scss" scoped >
:deep(.el-input--medium .el-input__inner){
  height: 28px;
  line-height: 28px;
}
</style>
