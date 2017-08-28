import Vue from 'vue'
import Router from 'vue-router'

import Welcome from './views/Welcome.vue'
import Moments from './views/Moments.vue'

Vue.use(Router)

const router = new Router({
  linkActiveClass: 'is-active',
  routes: [
    {
      name: 'Moments',
      path: '/',
      component: Moments
    },
    {
      name: 'Welcome',
      path: '/',
      component: Welcome
    },
    {
      name: 'Moments',
      path: '/moments',
      component: Moments
    },
    {
      path: '*',
      redirect: '/'
    }
  ]
})

export default router
