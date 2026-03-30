import request from '../utils/request'

export function listOpenJobPosts() {
  return request.get('/job-posts')
}

export function getJobPostDetail(id) {
  return request.get(`/job-posts/${id}`)
}

export function listMyJobPosts() {
  return request.get('/job-posts/mine')
}

export function createJobPost(payload) {
  return request.post('/job-posts', payload)
}

export function updateJobPost(id, payload) {
  return request.put(`/job-posts/${id}`, payload)
}

export function updateJobPostStatus(id, payload) {
  return request.put(`/job-posts/${id}/status`, payload)
}
