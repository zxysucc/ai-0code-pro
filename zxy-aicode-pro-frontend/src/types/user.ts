export type UserRole = 'user' | 'admin'

export type BaseResponse<T> = {
  code: number
  data?: T
  message?: string
}

export type PageResult<T> = {
  records?: T[]
  total?: number
  totalRow?: number
  size?: number
  current?: number
  pages?: number
  pageSize?: number
  pageNumber?: number
  totalPage?: number
}

export type LoginUserVO = {
  id?: number | string
  userAccount?: string
  userName?: string
  userAvatar?: string
  userProfile?: string
  userRole?: UserRole
  createTime?: string
  updateTime?: string
}

export type UserVO = LoginUserVO & {
  editTime?: string
}

export type UserLoginRequest = {
  userAccount: string
  userPassword: string
}

export type UserRegisterRequest = UserLoginRequest & {
  checkPassword: string
}

export type UserQueryRequest = {
  id?: number | string
  userAccount?: string
  userName?: string
  userRole?: UserRole
  pageNum?: number
  pageSize?: number
  sortField?: string
  sortOrder?: 'ascend' | 'descend'
}

export type DeleteRequest = {
  id: number | string
}
