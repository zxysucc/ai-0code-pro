import axios from 'axios'

type ErrorResponse = {
  message?: string
}

export function getRequestErrorMessage(error: unknown, fallback: string) {
  if (axios.isAxiosError<ErrorResponse>(error)) {
    return error.response?.data?.message || error.message || fallback
  }

  return error instanceof Error ? error.message : fallback
}
