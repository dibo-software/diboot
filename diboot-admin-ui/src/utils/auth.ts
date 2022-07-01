const TOKEN_KEY = 'token'

export const AUTH_HEADER_KEY = 'Authorization'

export default {
  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY)
  },
  setToken(token: string) {
    localStorage.setItem(TOKEN_KEY, token)
  },
  clearToken() {
    localStorage.removeItem(TOKEN_KEY)
  }
}
