import request from '../utils/request'

export function getCurrentResume() {
  return request.get('/resumes/current')
}

export function createCurrentResume(payload) {
  return request.post('/resumes/current', payload)
}

export function updateCurrentResume(payload) {
  return request.put('/resumes/current', payload)
}

export function listResumeEducations() {
  return request.get('/resumes/current/educations')
}

export function createResumeEducation(payload) {
  return request.post('/resumes/current/educations', payload)
}

export function updateResumeEducation(id, payload) {
  return request.put(`/resumes/current/educations/${id}`, payload)
}

export function deleteResumeEducation(id) {
  return request.delete(`/resumes/current/educations/${id}`)
}

export function listResumeProjects() {
  return request.get('/resumes/current/projects')
}

export function createResumeProject(payload) {
  return request.post('/resumes/current/projects', payload)
}

export function updateResumeProject(id, payload) {
  return request.put(`/resumes/current/projects/${id}`, payload)
}

export function deleteResumeProject(id) {
  return request.delete(`/resumes/current/projects/${id}`)
}

export function listResumeSkills() {
  return request.get('/resumes/current/skills')
}

export function createResumeSkill(payload) {
  return request.post('/resumes/current/skills', payload)
}

export function updateResumeSkill(id, payload) {
  return request.put(`/resumes/current/skills/${id}`, payload)
}

export function deleteResumeSkill(id) {
  return request.delete(`/resumes/current/skills/${id}`)
}
