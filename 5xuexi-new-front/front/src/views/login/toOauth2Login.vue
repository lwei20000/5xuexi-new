<!--  -->
<template>
<div class=''></div>
</template>

<script>
import {toOauth2Login} from "@/api/login";
export default {
  mounted() {
    let from = this.$route.query.from ? this.$route.query.from : "/";//登陆后返回的页面
    toOauth2Login({
      'next': from,
      responseType: 'code',
      scope: 'simple'
    }).then((data) => {
        window.location.replace = data.data
      }).catch((e) => {
      this.$message.error(e.message);
    });
  }
}
</script>
