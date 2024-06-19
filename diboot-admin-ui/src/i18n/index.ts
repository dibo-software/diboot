import { createI18n } from 'vue-i18n'
import I18nUtils from '@/utils/i18n'
import type { Locale } from './locales/zhCN'

const locales = import.meta.glob<Locale>('@/**/locales/**', {
  import: 'default',
  eager: true
})

const messages: Record<string, Locale> = {}

Object.keys(locales).forEach((path: string) => {
  const name = path.replace(/.*\/(.+)\.ts/, '$1').replace(/([a-z]+)([A-Z]+)/, '$1-$2')
  const localeData = messages[name]

  if (localeData) messages[name] = _.merge(localeData, locales[path])
  else messages[name] = locales[path]
  return messages
})
const i18n = createI18n({
  legacy: false,
  globalInjection: true,
  locale: I18nUtils.get() || navigator.language,
  fallbackLocale: I18nUtils.get(),
  messages
})
export default i18n
