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
        moments: []
      }
    },

    created: function () {
      axios.get("/api/moments")
        .then(response => {
          this.loading = false
          this.moments = response.data
        })
        .catch(error => {
           this.loading = false
           this.$notify.error({
            title: 'Bad response from Server',
            message: 'The server returned an invalid response. Please check the javascript logs for details.',
            duration: 0
          });
          console.error("bad request from server", error.response);
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
    grid-template-columns: repeat(auto-fill, minmax(320px, 320px));
    @media only screen and (min-width: 1080px) {
      max-width: 1000px;
    }
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
    max-width: 320px;
    margin: 2%;
    height: 98%;
  }
</style>