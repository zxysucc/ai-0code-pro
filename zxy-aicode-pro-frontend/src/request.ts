import axios from 'axios'
import { message } from 'ant-design-vue'

type BusinessResponse = {
  code?: number
}

const request = axios.create({
  baseURL: '/api',
  timeout: 60_000,
  withCredentials: true,
})

request.interceptors.response.use(
  (response) => {
    const data = response.data as BusinessResponse | string | undefined

    if (typeof data === 'object' && data?.code === 40100) {
      const responseUrl = response.request?.responseURL ?? ''
      const isLoginRequest = responseUrl.includes('user/get/login')
      const isLoginPage = window.location.pathname.includes('/user/login')

      if (!isLoginRequest && !isLoginPage) {
        void message.warning('请先登录')
        const currentPath = `${window.location.pathname}${window.location.search}${window.location.hash}`
        const redirect = encodeURIComponent(currentPath)
        window.location.href = `/user/login?redirect=${redirect}`
      }
    }

    return response
  },
  (error: unknown) => Promise.reject(error),
)

export default request
