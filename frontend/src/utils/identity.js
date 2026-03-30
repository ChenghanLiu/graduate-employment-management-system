import { computed, reactive } from 'vue'
import { ROLE_LABELS, resolveRoleHomePath } from '../constants/identity'

const STORAGE_KEY = 'employment-system-auth-identity'

function normalizeIdentity(rawIdentity) {
  if (!rawIdentity || !rawIdentity.userId || !rawIdentity.roleCode) {
    return null
  }

  return {
    userId: Number(rawIdentity.userId),
    username: rawIdentity.username || '',
    realName: rawIdentity.realName || '',
    role: rawIdentity.roleCode,
    roleCode: rawIdentity.roleCode,
    roleCodes: Array.isArray(rawIdentity.roleCodes) ? rawIdentity.roleCodes : [rawIdentity.roleCode],
    roleName: rawIdentity.roleName || ROLE_LABELS[rawIdentity.roleCode] || rawIdentity.roleCode,
    defaultPath: resolveRoleHomePath(rawIdentity.roleCode)
  }
}

function loadIdentity() {
  if (typeof window === 'undefined') {
    return null
  }

  const stored = window.localStorage.getItem(STORAGE_KEY)
  if (!stored) {
    return null
  }

  try {
    return normalizeIdentity(JSON.parse(stored))
  } catch {
    window.localStorage.removeItem(STORAGE_KEY)
    return null
  }
}

const state = reactive({
  current: loadIdentity()
})

function persistIdentity(identity) {
  if (typeof window === 'undefined') {
    return
  }

  if (!identity) {
    window.localStorage.removeItem(STORAGE_KEY)
    return
  }

  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(identity))
}

export function useIdentityStore() {
  const currentIdentity = computed(() => state.current)
  const isAuthenticated = computed(() => Boolean(state.current?.userId && state.current?.roleCode))

  const setIdentity = (identity) => {
    state.current = normalizeIdentity(identity)
    persistIdentity(state.current)
  }

  const clearIdentity = () => {
    state.current = null
    persistIdentity(null)
  }

  return {
    currentIdentity,
    isAuthenticated,
    setIdentity,
    clearIdentity
  }
}

export function getCurrentIdentity() {
  return state.current
}

export function isAuthenticated() {
  return Boolean(state.current?.userId && state.current?.roleCode)
}
