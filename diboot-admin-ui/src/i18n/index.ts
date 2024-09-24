import { createI18n } from 'vue-i18n'
import I18nUtils from '@/utils/i18n'
import type { App } from 'vue'

const locales = import.meta.glob('@/**/_locales/**', {
  import: 'default',
  eager: true
})

const messages: any = {}

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

export const i18nInstall = {
  install: (app: App) => {
    if ('true' === import.meta.env.VITE_APP_ENABLE_I18N) {
      api
        .get('/i18n-config/all')
        .then(res => Object.assign(messages[unref(i18n.global.locale)], res.data ?? {}))
        .catch(err => console.error(err.msg || err.message))
        .finally(() =>
          Object.keys(messages).forEach(locale => i18n.global.mergeLocaleMessage(locale, messages[locale]))
        )
    }
    app.use(i18n)
  }
}
