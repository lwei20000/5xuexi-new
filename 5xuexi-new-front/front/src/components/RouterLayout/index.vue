<!-- router-view 结合 keep-alive 组件 -->
<template>
  <transition :name="transitionName" mode="out-in" appear>
    <keep-alive :include="include">
      <router-view />
    </keep-alive>
  </transition>
</template>

<script>
  export default {
    name: 'RouterLayout',
    computed: {
      include() {
        return this.$store.getters['theme/keepAliveInclude'] ?? [];
      },
      transitionName() {
        return this.$store.state.theme.transitionName;
      }
    }
  };
</script>
