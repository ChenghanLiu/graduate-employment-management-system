export function formatDate(value) {
  if (!value) {
    return '-'
  }

  return String(value).slice(0, 10)
}

export function formatDateTime(value) {
  if (!value) {
    return '-'
  }

  return String(value).replace('T', ' ').slice(0, 16)
}
