<!--  -->
<template>
<div class=''></div>
</template>

<script>
import {API_BASE_URL} from "@/config/setting";
export default {
  mounted() {
    //获取前端地址
    let from = this.$route.query.from ? this.$route.query.from : "/";//登陆后返回的页面
    let thirdPartyName = this.$route.query.thirdPartyName ? this.$route.query.thirdPartyName : ''//第三方登录
    let paths = '/integrate/uaap/cas/to-cas-login?next=' + encodeURIComponent(from) + '&thirdPartyName=' + thirdPartyName;
    window.location.replace = API_BASE_URL + paths;
  }
}
</script>
