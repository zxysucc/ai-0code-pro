import { createRouter, createWebHistory } from 'vue-router'
import { message } from 'ant-design-vue'
import HomePage from '@/pages/HomePage.vue'
import { useLoginUserStore } from '@/stores/loginUser'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage,
    },
    {
      path: '/user/login',
      name: 'userLogin',
      component: () => import('@/pages/user/UserLoginPage.vue'),
    },
    {
      path: '/user/register',
      name: 'userRegister',
      component: () => import('@/pages/user/UserRegisterPage.vue'),
    },
    {
      path: '/admin/userManage',
      name: 'userManage',
      component: () => import('@/pages/admin/UserManagePage.vue'),
      meta: {
        access: 'admin',
      },
    },
    {
      path: '/admin/users',
      redirect: '/admin/userManage',
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/',
    },
  ],
})

router.beforeEach(async (to) => {
  const loginUserStore = useLoginUserStore()
  await loginUserStore.ensureLoginUser()

  if (to.meta.access === 'admin' && !loginUserStore.isLoggedIn) {
    return {
      path: '/user/login',
      query: { redirect: to.fullPath },
    }
  }

  if (to.meta.access === 'admin' && !loginUserStore.isAdmin) {
    void message.error('没有管理员权限')
    return '/'
  }

  return true
})

export default router
