const TOKEN_KEY = 'token'

export const AUTH_HEADER_KEY = 'Authorization'

export default {
  getToken(): string | null {
    return sessionStorage.getItem(TOKEN_KEY)
  },
  setToken(token: string) {
    sessionStorage.setItem(TOKEN_KEY, token)
  },
  cleanToken() {
    sessionStorage.removeItem(TOKEN_KEY)
  }
}
