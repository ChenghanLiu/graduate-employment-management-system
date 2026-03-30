import { ElMessage } from 'element-plus'

export function notifySuccess(message) {
  if (message) {
    ElMessage.success(message)
  }
}

export function notifyError(message) {
  if (message) {
    ElMessage.error(message)
  }
}

export function notifyWarning(message) {
  if (message) {
    ElMessage.warning(message)
  }
}
