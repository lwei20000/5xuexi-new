<!-- 专业课程弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="60%"
    :visible="visible"
    :close-on-click-modal="true"
    title="专业课程"
    @update:visible="updateVisible"
  >
  <majorCourseIndex :major="major"></majorCourseIndex>
  </ele-modal>
</template>

<script>
  import majorCourseIndex from '../../majorCourse/index.vue';
  export default {
    components: {majorCourseIndex},
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      major: Object
    },
    data() {
      return {

      }
    },
    computed: {

    },
    methods: {
      /* 更新visible */
      updateVisible(value) {
        this.$emit('update:visible', value);
      },
    }
  };
</script>
