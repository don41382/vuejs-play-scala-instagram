import Vue from 'vue'
import Router from 'vue-router'

import Moments from './views/Moments.vue'

Vue.use(Router)

const router = new Router({
  linkActiveClass: 'is-active',
  routes: [
    {
      name: 'Home',
      path: '/',
      component: Moments
    },
    {
      path: '*',
      redirect: '/'
    }
  ]
})

export default router
