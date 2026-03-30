import axios from 'axios'
import { getCurrentIdentity } from './identity'
import { notifyError, notifyWarning } from './message'

const service = axios.create({
  baseURL: '/api',
  timeout: 12000
})

service.interceptors.request.use((config) => {
  const identity = getCurrentIdentity()
  config.headers = config.headers || {}

  if (identity?.userId && identity?.roleCode) {
    config.headers['X-User-Id'] = String(identity.userId)
    config.headers['X-User-Role'] = identity.roleCode
  }

  return config
})

service.interceptors.response.use(
  (response) => {
    const payload = response.data

    if (payload && payload.code === 0) {
      return payload.data
    }

    const message = payload?.message || '请求失败'
    notifyError(message)
    return Promise.reject(new Error(message))
  },
  (error) => {
    const status = error.response?.status
    let message = error.response?.data?.message || error.message || '请求失败'

    if (status === 403) {
      message = '当前账号无权执行该操作'
      notifyWarning(message)
      return Promise.reject(error)
    }

    if (status === 401) {
      message = '登录状态已失效，请重新登录'
    }

    notifyError(message)
    return Promise.reject(error)
  }
)

export default service
