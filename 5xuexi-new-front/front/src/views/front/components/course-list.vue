<template>
  <div class="ele-body" style="padding:15px 0;max-width: 100%;min-height: calc(100vh - 165px);">
    <div>
      <el-row :gutter="15" v-if="courses">
        <el-col v-for="(item, index) in courses" :key="index" v-bind="styleResponsive ? { xl:6,lg:8,md:12,sm:24,xs: 24 } : { span: 6 }">
          <el-card class="course-list-item" shadow="hover">
            <div class="course-list-cover">
              <img :src="item.coursePicture?item.coursePicture:API_BASE_URL+'/system/sysStaticPicture/course'" :style="item.hasExam==1?('height:200px'):('height:175px')" alt=""  />
              <div class="jiaobiao">{{item.learingProgress||0}}%</div>
              <div class="course-list-title">
                <h6 class="ele-elip">{{ item.courseName }}</h6>
              </div>
            </div>

            <div class="course-list-info">
              <div class="course-list-info-list">
                <div>
                   <label>学习进度：</label><span>{{item.learingProgress||0}}%</span>
                </div>
                <div >
                  <label >学习成绩：</label><span>{{item.learingScore||0}}分</span>
                  <el-tooltip content="课程视频学习的进度得分" placement="top">
                    <i class="el-icon-_question ele-text-placeholder" style="cursor: pointer"></i>
                  </el-tooltip>
                </div>
              </div>
              <div class="course-list-info-list">
                <div>
                  <label>考试成绩：</label><el-button type="text" style="padding: 0;cursor: pointer" @click="openUserExam(item)">
                  {{item.examScore?(item.examScore+"分"+(item?.hasExamCorrect==1 ?"(待批改)":"")):(item?.hasExamCorrect==1 ?"待批改":"未考试")}}
                </el-button>
                </div>
                <div>
                  <label>总成绩：</label><span>{{item.totalScore?item.totalScore+"分":"未计算"}}</span>
                  <el-tooltip :content="item.totalScore?'学习成绩*'+(scoreRule.learningScoreProportions/100)+'+考试成绩*'+(scoreRule.examScoreProportions/100)+'=总成绩':'考试后才计算总成绩'" placement="top">
                    <i class="el-icon-_question ele-text-placeholder" style="cursor: pointer"></i>
                  </el-tooltip>
                </div>
              </div>
            </div>
            <div class="course-list-exam" v-if="item.hasExam != 1" style="text-align: center;padding-bottom: 10px;height: 15px;">
              <el-link @click="toExam(item)" type="primary">{{item.hasExam==2?'考试':'补考'}} <span style="font-size: 12px">({{item.startTime}}~{{item.endTime}})</span></el-link>
            </div>
            <div class="course-info-button">
<!--              <el-button plain block>成绩详情</el-button>-->
              <el-button plain  @click="golearn(item)">开始学习</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="15" style="min-height: 450px;text-align: center" v-else>
        <el-empty description="暂无课程" ></el-empty>
      </el-row>
    </div>

    <exam-list-preview :visible.sync="showExamsPreview" :data-list="userObjectExamList"  ></exam-list-preview>
  </div>
</template>

<script>
  import {API_BASE_URL} from "@/config/setting";

  import ExamListPreview from './exam-list-preview';
  export default {
    components: { ExamListPreview },
    props: {
      // 修改回显的数据
      courses: Array,
      //成绩计算规则
      scoreRule:undefined
    },
    name: 'ListCardcourse',
    data() {
      return {
        API_BASE_URL:API_BASE_URL,
        showExamsPreview:false,
        userObjectExamList:[]
      };
    },
    mounted() {

    },
    computed: {
      // 是否开启响应式布局
      styleResponsive() {
        return this.$store.state.theme.styleResponsive;
      }
    },
    methods:{
      golearn(item){
        this.$router.push({
          path:'/video/'+item.majorId+"/"+item.courseId
        })
      },
      toExam(item){
        this.$router.push({
          path:'/exam/'+item.majorId+"/"+item.courseId
        })
      },
      openUserExam(item){
        console.log(item)
        this.showExamsPreview = true;
        this.userObjectExamList = item?.userMajorCourseExamList || [];
      }
    }
  };
</script>

<style lang="scss" scoped>
  .jiaobiao {
    background-color: #01af63;
    position: absolute;
    width: 100px;
    right: -40px;
    top: -20px;
    line-height: 1.5;
    transform: rotate(45deg);
    font-size: 1rem;
    color: #fff;
    text-align: center;
    transform-origin: left center;
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

  /* 卡片列表 */
  .course-list-item {
    margin-bottom: 15px;
    //border: 1px solid var(--border-color-lighter);
    //--box-shadow-dark
    box-shadow: var(--box-shadow-base);
    :deep(.el-card__body) {
      padding: 0;
    }
  }

  .course-list-cover{
    position: relative;
    overflow: hidden;
    border-radius: 6px 6px 0 0;
      img {
      width: 100%;
      display: block;
      object-fit: cover;
      height: 180px;
    }
  }
  .course-list-title{
    position: absolute;
    bottom: 0;
    background: rgba(0, 0, 0, 0.6);
    width: 100%;
    h6{
      color: #fff;
      padding: 5px;
    }
  }
  .course-list-body {
    padding: 15px;
  }

  .course-list-desc {
    height: 44px;
    line-height: 22px;
    margin-top: 6px;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }

  .course-list-time {
    display: flex;
    margin-top: 6px;
    align-items: center;
  }

  .course-list-time-text {
    flex: 1;
  }
  .course-list-info{
    // font-size: 12px;
    padding: 10px 5px 5px 5px;
    display: flex;
    justify-content: space-between;
    font-size: 13px;
    .course-list-info-list{
      div{
        padding: 5px 0;
      }
    }
  }
  .course-info-button{
    display: flex;
    padding: 0px 5px 10px 5px;
    :deep(.el-button){
      width: 100%;
    }
  }
</style>
