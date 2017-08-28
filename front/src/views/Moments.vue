<template>
  <div>
    <div class="wrapper">
      <div class="header">
        <h1>Everyday a new moment</h1>
        <p>We capture everyday a new moment until we get married.</p>
      </div>
      <div v-for="moment in moments">
        <moment :moment="moment"/>
      </div>
    </div>
  </div>
</template>

<script>
  import Moment from '../components/Moment.vue'
  import axios from 'axios';
  import _ from 'lodash';

  export default {

    data: function () {
      return {
        defaultImage:  "https://scontent.cdninstagram.com/t51.2885-15/s320x320/e35/21041973_485351558485401_3316635971457384448_n.jpg",
        moments: []
      }
    },

    created: function () {
      axios.get("/api/moments", {
          params: {
          }
        })
        .then(response => {
          this.moments = response.data.data
        })
    },

    components: {
      'moment': Moment
    }

  }
</script>

<style scoped lang="scss">

  .header {
    text-align: center;
    margin-bottom: 10px;
    flex: 0 1 100%;
    grid-column: 1 / -1;
  }

  .wrapper {
    display: grid;
    margin: 0 auto;
    max-width: 960px;
    grid-template-columns: repeat(auto-fill, minmax(320px, 320px));
    grid-auto-rows: minmax(auto, auto);
  }

  .panel {
    /* needed for the flex layout*/
    margin-left: 5px;
    margin-right: 5px;
    flex: 1 1 200px;
  }

</style>