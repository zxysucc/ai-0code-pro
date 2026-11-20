import type { AxiosRequestConfig } from 'axios'
import request from '@/request'
import type {
  BaseResponse,
  DeleteRequest,
  LoginUserVO,
  PageResult,
  UserLoginRequest,
  UserQueryRequest,
  UserRegisterRequest,
  UserVO,
} from '@/types/user'

export function userRegister(body: UserRegisterRequest, options?: AxiosRequestConfig) {
  return request<BaseResponse<number | string>>('/user/register', {
    method: 'POST',
    data: body,
    ...options,
  })
}

export function userLogin(body: UserLoginRequest, options?: AxiosRequestConfig) {
  return request<BaseResponse<LoginUserVO>>('/user/login', {
    method: 'POST',
    data: body,
    ...options,
  })
}

export function userLogout(options?: AxiosRequestConfig) {
  return request<BaseResponse<boolean>>('/user/logout', {
    method: 'POST',
    ...options,
  })
}

export function getLoginUser(options?: AxiosRequestConfig) {
  return request<BaseResponse<LoginUserVO>>('/user/get/login', {
    method: 'GET',
    ...options,
  })
}

export function listUserVoByPage(body: UserQueryRequest, options?: AxiosRequestConfig) {
  return request<BaseResponse<PageResult<UserVO>>>('/user/list/page/vo', {
    method: 'POST',
    data: body,
    ...options,
  })
}

export function deleteUser(body: DeleteRequest, options?: AxiosRequestConfig) {
  return request<BaseResponse<boolean>>('/user/delete', {
    method: 'POST',
    data: body,
    ...options,
  })
}
