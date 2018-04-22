<template>
  <el-card :body-style="{ padding: '0px' }" class="panel">
    <img :src="moment.lowImage" class="image">
    <div style="padding: 14px;">
      <div>
        <div class="daysleft">
          <div class="title text-primary">{{ daysUntilWedding }}</div>
          <div class="subtitle text-primary">days left</div>
        </div>
        <span class="title" v-html="hashTag"/>
      </div>
      <div class="bottom clearfix">
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
        placeRender: function (i) {
          if (i == 0) {
            return "1st";
          } else if (i == 1) {
            return "2nd";
          } else if (i == 2) {
            return "3rd";
          } else {
            return i;
          }
        }
      }
    },

    computed: {
      hashTag: function () {
        const startsWithHashEngage = function (s) { return s.toLowerCase().startsWith("verlobungs") }

        return _.head(_.map(_.filter(this.moment.tags,startsWithHashEngage), function (tag) {
          return _.replace(tag.toLowerCase(),"verlobungs","<b>#verlobungs</b>&thinsp;")
        }))
      },

      daysUntilWedding: function () {
        var a = moment([2018, 6, 5]);
        var b = moment(this.moment.created);
        return a.diff(b, 'days')
      }
    },

    props: ['moment']
  }
</script>

<style scoped lang="scss">
   .liker {
    font-size: 12px;
    color: #999;
  }

  .liker .name {
    display: inline-block;
  }

  .liker .place {
    font-size: 8px;
    vertical-align: top;
    padding-right: 5px;
  }

  .bottom {
    line-height: 1;
  }

  .heart {
    color: #e6605a;
  }

  .title {
    font-size: 15px;
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
