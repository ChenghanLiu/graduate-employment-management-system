import request from '../utils/request'

export function login(payload) {
  return request.post('/auth/login', payload)
}

export function registerStudent(payload) {
  return request.post('/auth/register/student', payload)
}

export function registerEnterprise(payload) {
  return request.post('/auth/register/enterprise', payload)
}

export function getCurrentUser() {
  return request.get('/auth/me')
}
