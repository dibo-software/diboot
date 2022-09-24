import { createI18n } from 'vue-i18n'
import type { Locale } from './locales/zhCN'

const locales = import.meta.glob<Locale>('./locales/**', {
  import: 'default',
  eager: true
})

const messages: Record<string, Locale> = {}

Object.keys(locales).reduce((all: Record<string, unknown>, path: string) => {
  const name = path.replace(/.*\/(.+)\.ts/, '$1').replace(/([a-z]+)([A-Z]+)/, '$1-$2')
  all[name] = locales[path]
  return all
}, messages)

const i18n = createI18n({
  legacy: false,
  globalInjection: true,
  locale: navigator.language,
  fallbackLocale: 'zh-CN',
  messages
})

export default i18n
