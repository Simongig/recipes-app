import { createRouter, createWebHistory } from 'vue-router'
import Index from '../views/Index'
import CreateRecipe from '../views/CreateRecipe.vue'
import Login from '../views/Login.vue'
import RecipePdp from '../views/RecipePdp.vue'
import RecipesPlp from '../views/RecipesPlp.vue'

const routes = [
  {
    path: '/',
    name: 'Index',
    component: Index
  },
  {
    path: '/createRecipe',
    name: 'createRecipe',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: CreateRecipe
  }, {
    path: '/recipe/all',
    name: 'recipePlp',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: RecipesPlp
  },
  {
    path: '/login',
    name: 'login',
    component: Login
  },
  {
    path: '/recipe/id/:id',
    name: 'recipePdp',
    component: RecipePdp
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
