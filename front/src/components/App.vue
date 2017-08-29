<template>
  <div class="main">
    <navigation/>
    <router-view></router-view>
  </div>
</template>

<script>
  import Navigation from './Navigation.vue'

  export default {
    name: 'app',
    data () {
      return {
      }
    },

    components: {
      Navigation
    }

  }

</script>

<style scoped lang="scss">

</style>
