import Vue from 'vue';
import App from './components/App.vue';
import Element from 'element-ui'

Vue.use(Element)

new Vue(App).$mount('#app');

import '../sass/style.scss';
