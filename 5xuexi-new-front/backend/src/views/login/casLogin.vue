<!--  -->
<template>
<div class=''></div>
</template>

<script>
import {casLogin} from "@/api/login";
export default {
  mounted() {
    casLogin({'token': this.$route.query.ticket,remember: true})
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
