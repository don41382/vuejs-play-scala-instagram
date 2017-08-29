<template>
  <ul>
    <li><a class="active" href="/">Home</a></li>
  </ul>
</template>

<script>

</script>

<style scoped lang="scss">
  ul {
    list-style-type: none;
    margin: 0;
    padding: 0;
    overflow: hidden;
    background-color: rgba(255, 255, 255, .5);
    position: fixed;
    top: 0;
    width: 100%;
  }

  li {
    float: left;
  }

  li a {
    display: block;
    color: #9e9e9e;
    text-align: center;
    padding: 20px 30px;
    text-decoration: none;
  }

  li a:hover:not(.active) {
    color: #4ea4ff;
  }

  .active {
    color: #007bff;
  }
</style>