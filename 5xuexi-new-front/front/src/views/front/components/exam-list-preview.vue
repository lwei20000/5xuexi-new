<!-- 预览考试记录 -->
<template>
  <ele-modal :lock-scroll="false"
             width="800px"
             :visible="visible"
             :close-on-click-modal="true"
             custom-class="ele-dialog-form"
             title="考试记录"
             @update:visible="updateVisible"
  >
    <ul style="min-height: 200px">
      <li class="exam-item title">
        <div class="exam-score">得分</div>
        <div class="exam-time">作答开始时间</div>
        <div class="exam-time">作答结束时间</div>
        <div class="exam-status">状态</div>
      </li>
      <li class="exam-item"  v-for="(item,index) in dataList" :key="index">
        <div class="exam-score">{{item.score}}</div>
        <div class="exam-time">{{item.startTime}}</div>
        <div class="exam-time">{{item.endTime}}</div>
        <div class="exam-status">{{ ['答题进行中','提交未批改','提交已批改'][item.status] }}</div>
      </li>
      <el-empty v-if="dataList.length==0"></el-empty>
    </ul>
  </ele-modal>
</template>

<script>
  export default {
    props: {
      // 弹窗是否打开
      visible: Boolean,
      dataList: {type:[Array,Object]}
    },
    data() {
      return {
      };
    },
    computed: {
      // 是否开启响应式布局
      styleResponsive() {
        return this.$store.state.theme.styleResponsive;
      }
    },
    methods: {
      /* 更新visible */
      updateVisible(value) {
        this.$emit('update:visible', value);
      }
    },
  };

</script>
<style scoped lang='scss'>

ul,li{
  list-style:none;
  text-align: center;
  margin: 0;
  padding: 0;
}
.title{
  font-size: 16px;
  font-weight: bold;
  padding: 10px;
}

.exam-item{
  display: flex;
  padding: 15px;
}
.exam-score{
  width: 10%;
}
.exam-time{
  width: 32.5%;
}
.exam-status{
  width: 25%;
}
.exam-item >div{
  padding: 0 5px;
}

@media screen and (max-width: 768px ) {
  .title {
    font-size: 14px;
  }
  .exam-item{
    font-size: 12px;
    padding: 0;
    padding-top: 15px;
  }
  :deep(.el-dialog__body){
    padding: 0;
  }
}
</style>
