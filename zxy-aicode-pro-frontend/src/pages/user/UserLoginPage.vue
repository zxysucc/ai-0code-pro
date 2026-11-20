<script setup lang="ts">
import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import { userLogin } from '@/api/userController'
import { useLoginUserStore } from '@/stores/loginUser'
import type { UserLoginRequest } from '@/types/user'
import { getRequestErrorMessage } from '@/utils/requestError'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()
const submitting = ref(false)
const formState = reactive<UserLoginRequest>({
  userAccount: typeof route.query.account === 'string' ? route.query.account : '',
  userPassword: '',
})

function getRedirectPath() {
  const redirect = route.query.redirect

  if (typeof redirect === 'string' && redirect.startsWith('/') && !redirect.startsWith('//')) {
    return redirect
  }

  return '/'
}

async function handleSubmit() {
  submitting.value = true

  try {
    const response = await userLogin(formState)
    const result = response.data

    if (result.code !== 200 || !result.data) {
      void message.error(result.message || '登录失败')
      return
    }

    loginUserStore.setLoginUser(result.data)
    void message.success('登录成功')
    await router.replace(getRedirectPath())
  } catch (error) {
    void message.error(getRequestErrorMessage(error, '登录失败，请稍后重试'))
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <main class="auth-page" aria-labelledby="login-title">
    <section class="auth-intro">
      <p class="auth-eyebrow">ZXY AI 零代码</p>
      <h1 id="login-title">欢迎回来</h1>
      <p>登录后继续创建和管理你的 AI 应用。</p>
    </section>

    <section class="auth-panel" aria-label="账号登录">
      <header class="auth-panel-heading">
        <h2>账号登录</h2>
        <p>还没有账号？<RouterLink to="/user/register">立即注册</RouterLink></p>
      </header>

      <a-form :model="formState" layout="vertical" @finish="handleSubmit">
        <a-form-item
          label="账号"
          name="userAccount"
          :rules="[
            { required: true, message: '请输入账号' },
            { min: 4, message: '账号至少 4 位' },
          ]"
        >
          <a-input
            v-model:value="formState.userAccount"
            size="large"
            autocomplete="username"
            placeholder="请输入账号"
          />
        </a-form-item>

        <a-form-item
          label="密码"
          name="userPassword"
          :rules="[
            { required: true, message: '请输入密码' },
            { min: 8, message: '密码至少 8 位' },
          ]"
        >
          <a-input-password
            v-model:value="formState.userPassword"
            size="large"
            autocomplete="current-password"
            placeholder="请输入密码"
          />
        </a-form-item>

        <a-button
          class="auth-submit"
          type="primary"
          html-type="submit"
          size="large"
          :loading="submitting"
        >
          登录
        </a-button>
      </a-form>
    </section>
  </main>
</template>

<style scoped>
.auth-page {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(360px, 440px);
  align-items: center;
  min-height: calc(100vh - 200px);
  gap: 72px;
  padding: 44px 0;
}

.auth-intro {
  max-width: 560px;
}

.auth-eyebrow {
  margin: 0 0 12px;
  color: #b24f3f;
  font-size: 14px;
  font-weight: 700;
}

.auth-intro h1 {
  margin: 0;
  color: #173a35;
  font-size: 42px;
  line-height: 1.2;
}

.auth-intro > p:last-child {
  margin: 18px 0 0;
  color: #52635f;
  font-size: 17px;
  line-height: 1.7;
}

.auth-panel {
  padding: 32px;
  border: 1px solid #dce3e1;
  border-radius: 8px;
  background: #ffffff;
}

.auth-panel-heading {
  margin-bottom: 28px;
}

.auth-panel-heading h2 {
  margin: 0;
  color: #1f2a27;
  font-size: 24px;
}

.auth-panel-heading p {
  margin: 8px 0 0;
  color: #6a7874;
}

.auth-panel-heading a {
  color: #0f7567;
  font-weight: 600;
}

.auth-submit {
  width: 100%;
  margin-top: 4px;
  background: #0f7567;
  border-color: #0f7567;
}

.auth-submit:hover,
.auth-submit:focus {
  background: #0b5e53 !important;
  border-color: #0b5e53 !important;
}

@media (max-width: 820px) {
  .auth-page {
    grid-template-columns: 1fr;
    min-height: 0;
    gap: 28px;
    padding: 16px 0 36px;
  }

  .auth-intro h1 {
    font-size: 32px;
  }

  .auth-intro > p:last-child {
    margin-top: 10px;
    font-size: 15px;
  }
}

@media (max-width: 480px) {
  .auth-panel {
    padding: 24px 20px;
  }
}
</style>
