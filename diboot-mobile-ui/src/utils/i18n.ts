export const LOCALE_KEY = 'locale'
export const LANGUAGE = 'Accept-Language'

export default {
  get(): string {
    return localStorage.getItem(LOCALE_KEY) || 'zhCN'
  },
  set(locale: string) {
    localStorage.setItem(LOCALE_KEY, locale)
  },
  clear() {
    localStorage.removeItem(LOCALE_KEY)
  }
}
