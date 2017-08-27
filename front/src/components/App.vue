<template>
    <div id="hello">
        <div id="images" @click="show">
            <img src="/assets/images/soonmore.jpg" alt="soon more">
        </div>
        <h1>We are working on it! <span class="text-primary">Stay tuned!</span></h1>
    </div>
</template>

<script>
    export default {
      methods: {
        show() {
            const h = this.$createElement;

            this.$notify({
              title: 'Save the date',
              message: h('i', { style: 'color: teal' }, '9. June 2018')
            });
        }
      }
    }
</script>

<style scoped lang="scss">
    #hello {
        width: 600px;
        margin: 0 auto;
        color: gray;
        text-align: center;
        position: relative;
        top: 50%;
        transform: translateY(-50%);
    }

    #images {
        font-size: 2.3em;
        font-weight: bold;
        margin-bottom: 1em;
        img {
            height: 400px;
        }
    }
</style>