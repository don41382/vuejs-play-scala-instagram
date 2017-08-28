import Vue from 'vue';
import App from './components/App.vue';
import Element from 'element-ui'

import locale from 'element-ui/lib/locale'
import lang from 'element-ui/lib/locale/lang/en'
locale.use(lang)

import router from './router.js'

Vue.use(require('vue-moment'));
Vue.use(Element)

new Vue({
  el: '#app',
  router: router,
  render: h => h(App)
})

import '../sass/style.scss';
