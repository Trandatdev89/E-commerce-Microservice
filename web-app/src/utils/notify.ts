import { ElMessage } from 'element-plus'
import { ApiError } from '@/api/http'

/** Hiện lỗi API kèm mã lỗi và traceId (copy traceId để tìm log / Zipkin) */
export function showError(error: unknown) {
  if (error instanceof ApiError) {
    const meta = [error.code, error.traceId ? `traceId: ${error.traceId}` : null].filter(Boolean).join(' | ')
    ElMessage({ type: 'error', message: meta ? `${error.message} (${meta})` : error.message, duration: 6000, showClose: true })
  } else {
    ElMessage.error(error instanceof Error ? error.message : String(error))
  }
}
