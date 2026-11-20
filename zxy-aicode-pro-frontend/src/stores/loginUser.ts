import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { getLoginUser, userLogout } from '@/api/userController'
import type { BaseResponse, LoginUserVO } from '@/types/user'

export const useLoginUserStore = defineStore('loginUser', () => {
  const loginUser = ref<LoginUserVO | null>(null)
  const initialized = ref(false)
  const isLoggedIn = computed(() => loginUser.value !== null)
  const isAdmin = computed(() => loginUser.value?.userRole === 'admin')

  function setLoginUser(user: LoginUserVO | null) {
    loginUser.value = user
    initialized.value = true
  }

  async function fetchLoginUser() {
    try {
      const response = await getLoginUser()
      const result = response.data
      loginUser.value = result.code === 200 && result.data ? result.data : null
    } catch {
      loginUser.value = null
    } finally {
      initialized.value = true
    }

    return loginUser.value
  }

  async function ensureLoginUser() {
    if (!initialized.value) {
      await fetchLoginUser()
    }

    return loginUser.value
  }

  async function logout(): Promise<BaseResponse<boolean>> {
    const response = await userLogout()
    const result = response.data

    if (result.code === 200 && result.data) {
      setLoginUser(null)
    }

    return result
  }

  return {
    loginUser,
    initialized,
    isLoggedIn,
    isAdmin,
    setLoginUser,
    fetchLoginUser,
    ensureLoginUser,
    logout,
  }
})
