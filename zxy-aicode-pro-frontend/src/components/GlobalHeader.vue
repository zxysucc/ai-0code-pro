<script setup lang="ts">
import { computed, h } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { message, type MenuProps } from 'ant-design-vue'

const route = useRoute()
const router = useRouter()

const selectedKeys = computed(() => [route.path])

const menuItems = [
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

const handleMenuClick: MenuProps['onClick'] = ({ key }) => {
  if (typeof key === 'string' && key.startsWith('/')) {
    void router.push(key)
  }
}

const handleLogin = () => {
  void message.info('登录功能尚未接入')
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

      <a-button class="login-button" type="primary" @click="handleLogin">登录</a-button>
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
