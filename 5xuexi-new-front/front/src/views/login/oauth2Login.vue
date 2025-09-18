<!--  -->
<template >
    <div></div>
</template>

<script>
import {oauth2Login} from "@/api/login";

export default {
  mounted() {
    oauth2Login({responseType: 'code',
      'token': this.$route.query.code||this.$route.query.access_token,remember: true})
      .then((data) => {
        if (data.code === 0) {
          this.$message.success(data.message);
          this.$router.replace(this.$route?.query?.from ?? '/').catch(() => {});
        }else{
          this.$message.error(data.message);
        }
      }).catch((e) => {
      this.$message.error(e.message);
    });
  }
}
</script>

