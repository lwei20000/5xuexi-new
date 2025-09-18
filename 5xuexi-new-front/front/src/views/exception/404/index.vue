<template>
  <ele-exception
    title="404"
    description="抱歉, 你访问的页面不存在"
    style="margin: 145px 0"
  >
    <template v-slot:icon>
      <img
        alt=""
        src="@/assets/ic-404.svg"
        style="width: 305px; height: 245px"
      />
    </template>
    <template v-slot:extra>
      <router-link to="/">
        <el-button type="primary">返回首页</el-button>
      </router-link>
    </template>
  </ele-exception>
</template>

<script>
  import { EleException } from 'ele-admin';

  export default {
    // eslint-disable-next-line vue/multi-word-component-names
    name: 'Exception404',
    components: { EleException }
  };
</script>
