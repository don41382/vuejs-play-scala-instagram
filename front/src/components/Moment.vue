<template>
  <el-card :body-style="{ padding: '0px' }" class="panel">
    <img :src="moment.stdImage" class="image">
    <div style="padding: 14px;">
      <div>
        <div class="daysleft">
          <div class="title text-primary">{{ daysUntilWedding }}</div>
          <div class="subtitle text-primary">days left</div>
        </div>
        <span v-html="hashTag"/>
      </div>
      <div class="bottom clearfix">
        <time class="time">{{ moment.created | moment("from", "now") }}</time>
      </div>
    </div>
  </el-card>
</template>

<script>
  import _ from 'lodash';
  const moment = require('moment')

  export default {
    data () {
      return {
      }
    },

    computed: {
      hashTag: function () {
        return _.head(_.map(this.moment.tags,function (tag) {
          return _.replace(tag.toLowerCase(),"verlobungs","<b>#verlobungs</b>&thinsp;")
        }))
      },

      daysUntilWedding: function () {
        var a = moment([2018, 5, 6]);
        var b = moment(this.moment.created);
        return a.diff(b, 'days')
      }
    },

    props: ['moment']
  }
</script>

<style scoped lang="scss">
   .time {
    font-size: 13px;
    color: #999;
  }

  .bottom {
    margin-top: 10px;
    line-height: 0px;
  }

  .daysleft {
    float: right;
    text-align: center;
  }

  .daysleft .title {
    font-size: 30px;
    line-height: 37px;
    font-weight: bold;
  }

  .daysleft .subtitle {
    font-size: 10px;
    margin-top: -5px;
  }

  .image {
    width: 100%;
    display: block;
  }

  .clearfix:before,
  .clearfix:after {
      display: table;
      content: "";
  }

  .clearfix:after {
      clear: both
  }
</style>
