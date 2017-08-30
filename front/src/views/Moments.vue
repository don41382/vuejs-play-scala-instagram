<template>
  <div>
    <div class="wrapper">
      <div class="header">
        <h1>Everyday a new moment</h1>
        <p>We capture everyday a new moment until we get married.</p>
        <beat-loader :loading="loading" color="#007bff" size="20px"></beat-loader>
      </div>
      <div v-for="moment in moments">
        <moment :moment="moment"/>
      </div>
    </div>
  </div>
</template>

<script>
  import Moment from '../components/Moment.vue'
  import BeatLoader from 'vue-spinner/src/BeatLoader.vue'
  import axios from 'axios';
  import _ from 'lodash';

  export default {

    data: function () {
      return {
        loading: true,
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
          this.loading = false
          this.moments = response.data.data
        })
    },

    components: {
      'moment': Moment,
      BeatLoader
    }

  }
</script>

<style scoped lang="scss">
  *,
  *:before,
  *:after {
    box-sizing:border-box;
  }

  .header {
    text-align: center;
    margin-bottom: 10px;
    flex: 0 1 100%;
    grid-column: 1 / -1;
  }

  .wrapper {
    display: grid;
    margin: 0 auto;
    width: 320px;
    max-width: 1100px;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr  ));
    grid-auto-rows: minmax(auto, auto);
  }

  .wrapper {
    display: flex;
    flex-wrap: wrap;
    /* no grid support */
  }

  .panel {
    /* needed for the flex layout*/
    flex: 1 1 320px;
    margin: 10px;
  }

</style>