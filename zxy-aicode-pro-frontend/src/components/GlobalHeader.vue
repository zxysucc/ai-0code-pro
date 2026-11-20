<script setup lang="ts">
import { computed, h } from 'vue'
import { storeToRefs } from 'pinia'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { message, type MenuProps } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { getRequestErrorMessage } from '@/utils/requestError'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()
const { isAdmin, isLoggedIn, loginUser } = storeToRefs(loginUserStore)

const selectedKeys = computed(() => [route.path])

const menuItems = computed(() => {
  const items = [
    {
      key: '/',
      label: '首页',
      title: '首页',
    },
    {
      key: 'codefather',
      label: h(
        'a',
        {
          href: 'https://www.codefather.cn',
          target: '_blank',
          rel: 'noopener noreferrer',
        },
        '编程导航',
      ),
      title: '编程导航',
    },
  ]

  if (isAdmin.value) {
    items.splice(1, 0, {
      key: '/admin/userManage',
      label: '用户管理',
      title: '用户管理',
    })
  }

  return items
})

const displayName = computed(
  () => loginUser.value?.userName || loginUser.value?.userAccount || '用户',
)

const avatarText = computed(() => displayName.value.slice(0, 1).toUpperCase())

const handleMenuClick: MenuProps['onClick'] = ({ key }) => {
  if (typeof key === 'string' && key.startsWith('/')) {
    void router.push(key)
  }
}

const handleLogin = () => {
  void router.push('/user/login')
}

const handleUserMenuClick: MenuProps['onClick'] = async ({ key }) => {
  if (key === 'admin') {
    await router.push('/admin/userManage')
    return
  }

  if (key !== 'logout') return

  try {
    const result = await loginUserStore.logout()

    if (result.code !== 200 || !result.data) {
      void message.error(result.message || '退出失败')
      return
    }

    void message.success('已退出登录')
    await router.replace('/user/login')
  } catch (error) {
    void message.error(getRequestErrorMessage(error, '退出失败，请稍后重试'))
  }
}
</script>

<template>
  <a-layout-header class="global-header">
    <div class="header-inner">
      <RouterLink class="brand" to="/" aria-label="返回首页">
        <img class="brand-mark" src="/brand-mark.svg" alt="" />
        <span class="brand-name">ZXY AI 零代码</span>
      </RouterLink>

      <a-menu
        class="navigation"
        mode="horizontal"
        :items="menuItems"
        :selected-keys="selectedKeys"
        @click="handleMenuClick"
      />

      <a-dropdown v-if="isLoggedIn" placement="bottomRight">
        <a-button class="user-trigger" type="text">
          <a-avatar class="user-avatar" :src="loginUser?.userAvatar">
            {{ avatarText }}
          </a-avatar>
          <span class="user-name">{{ displayName }}</span>
        </a-button>
        <template #overlay>
          <a-menu @click="handleUserMenuClick">
            <a-menu-item v-if="isAdmin" key="admin">用户管理</a-menu-item>
            <a-menu-divider v-if="isAdmin" />
            <a-menu-item key="logout">退出登录</a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>

      <a-button v-else class="login-button" type="primary" @click="handleLogin">登录</a-button>
    </div>
  </a-layout-header>
</template>

<style scoped>
.global-header {
  height: 64px;
  padding: 0 24px;
  background: #ffffff;
  border-bottom: 1px solid #dce3e1;
  line-height: normal;
}

.header-inner {
  display: grid;
  grid-template-columns: minmax(190px, 240px) minmax(0, 1fr) auto;
  align-items: center;
  width: 100%;
  max-width: 1200px;
  height: 100%;
  margin: 0 auto;
  gap: 20px;
}

.brand {
  display: inline-flex;
  align-items: center;
  min-width: 0;
  color: #173a35;
}

.brand:hover {
  color: #0f7567;
}

.brand-mark {
  width: 36px;
  height: 36px;
  flex: 0 0 36px;
}

.brand-name {
  margin-left: 10px;
  overflow: hidden;
  font-size: 17px;
  font-weight: 700;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.navigation {
  min-width: 0;
  border-bottom: 0;
  background: transparent;
}

.login-button {
  min-width: 72px;
  background: #0f7567;
  border-color: #0f7567;
}

.login-button:hover,
.login-button:focus {
  background: #0b5e53 !important;
  border-color: #0b5e53 !important;
}

.user-trigger {
  display: inline-flex;
  align-items: center;
  max-width: 180px;
  height: 42px;
  padding: 4px 8px;
  color: #1f2a27;
}

.user-avatar {
  flex: 0 0 auto;
  background: #0f7567;
}

.user-name {
  min-width: 0;
  margin-left: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 720px) {
  .global-header {
    height: auto;
    padding: 10px 16px 0;
  }

  .header-inner {
    grid-template-columns: minmax(0, 1fr) auto;
    gap: 6px 12px;
  }

  .navigation {
    grid-column: 1 / -1;
    order: 3;
  }
}

@media (max-width: 420px) {
  .brand-name {
    font-size: 15px;
  }
}
</style>
