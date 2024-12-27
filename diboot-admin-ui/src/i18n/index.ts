import { createI18n } from 'vue-i18n'
import type { I18nOptions, VueMessageType } from 'vue-i18n'
import I18nUtils from '@/utils/i18n'
import type { App } from 'vue'
import type { LocaleMessage } from '@intlify/core-base'

const locales = import.meta.glob('@/**/_locales/**', {
  import: 'default',
  eager: true
})

const messages: I18nOptions['messages'] = {}

Object.keys(locales).forEach((path: string) => {
  const name = path.replace(/.*\/(.+)\.ts/, '$1').replace(/([a-z]+)([A-Z]+)/, '$1-$2')
  const localeData = messages[name]

  if (localeData) messages[name] = _.merge(localeData, locales[path])
  else messages[name] = locales[path] as LocaleMessage<VueMessageType>
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
    app.use(i18n)
  }
}
